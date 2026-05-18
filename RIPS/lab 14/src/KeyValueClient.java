import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class KeyValueClient {
    public static String get(String host, int port, String key) throws Exception {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("GET " + key);
            return in.readLine();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java KeyValueClient <host> <port> <key>");
            return;
        }

        String response = get(args[0], Integer.parseInt(args[1]), args[2]);
        System.out.println(response);
    }
}

