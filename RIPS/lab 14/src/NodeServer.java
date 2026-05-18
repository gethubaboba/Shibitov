import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeServer implements AutoCloseable {
    private final int port;
    private final String key;
    private final String value;
    private final List<Integer> peers;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService workers = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private Thread acceptThread;

    public NodeServer(int port, String key, String value, List<Integer> peers) {
        this.port = port;
        this.key = key;
        this.value = value;
        this.peers = List.copyOf(peers);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(250);
        running.set(true);
        acceptThread = new Thread(this::acceptLoop, "node-" + port);
        acceptThread.start();
        System.out.printf("Node %d started: %s=%s, peers=%s%n", port, key, value, peers);
    }

    private void acceptLoop() {
        while (running.get()) {
            try {
                Socket socket = serverSocket.accept();
                workers.submit(() -> handle(socket));
            } catch (SocketTimeoutException ignored) {
                // Allows the loop to observe the running flag.
            } catch (IOException e) {
                if (running.get()) {
                    System.out.printf("Node %d accept error: %s%n", port, e.getMessage());
                }
            }
        }
    }

    private void handle(Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String request = in.readLine();
            out.println(process(request));
        } catch (IOException e) {
            System.out.printf("Node %d client error: %s%n", port, e.getMessage());
        }
    }

    private String process(String request) {
        if (request == null || request.isBlank()) {
            return "ERROR empty request";
        }

        String[] parts = request.trim().split("\\s+");
        if (parts.length == 2 && parts[0].equals("GET")) {
            return find(parts[1], new HashSet<>(Set.of(port)));
        }
        if (parts.length >= 3 && parts[0].equals("PEER_GET")) {
            Set<Integer> visited = parseVisited(parts[2]);
            visited.add(port);
            return find(parts[1], visited);
        }

        return "ERROR unknown command";
    }

    private String find(String requestedKey, Set<Integer> visited) {
        if (key.equals(requestedKey)) {
            return "FOUND " + key + "=" + value + " at node " + port;
        }

        for (int peer : peers) {
            if (visited.contains(peer)) {
                continue;
            }

            Set<Integer> nextVisited = new HashSet<>(visited);
            nextVisited.add(peer);
            String response = askPeer(peer, requestedKey, nextVisited);
            if (response != null && response.startsWith("FOUND")) {
                return response;
            }
        }

        return "NOT_FOUND " + requestedKey;
    }

    private String askPeer(int peerPort, String requestedKey, Set<Integer> visited) {
        try (Socket socket = new Socket("localhost", peerPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("PEER_GET " + requestedKey + " " + serializeVisited(visited));
            return in.readLine();
        } catch (IOException e) {
            return "NOT_FOUND " + requestedKey;
        }
    }

    private static Set<Integer> parseVisited(String text) {
        Set<Integer> visited = new HashSet<>();
        for (String item : text.split(",")) {
            if (!item.isBlank()) {
                visited.add(Integer.parseInt(item));
            }
        }
        return visited;
    }

    private static String serializeVisited(Set<Integer> visited) {
        List<Integer> ordered = new ArrayList<>(visited);
        ordered.sort(Integer::compareTo);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ordered.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(ordered.get(i));
        }
        return sb.toString();
    }

    @Override
    public void close() throws Exception {
        running.set(false);
        if (serverSocket != null) {
            serverSocket.close();
        }
        if (acceptThread != null) {
            acceptThread.join(1000);
        }
        workers.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: java NodeServer <port> <key> <value> <peerPortsCsv>");
            return;
        }

        List<Integer> peerPorts = new ArrayList<>();
        if (!args[3].isBlank()) {
            for (String item : args[3].split(",")) {
                peerPorts.add(Integer.parseInt(item.trim()));
            }
        }

        NodeServer server = new NodeServer(
            Integer.parseInt(args[0]),
            args[1],
            args[2],
            peerPorts
        );
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.close();
            } catch (Exception ignored) {
            }
        }));
        Thread.currentThread().join();
    }
}

