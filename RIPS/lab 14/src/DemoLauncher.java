import java.util.ArrayList;
import java.util.List;

public class DemoLauncher {
    public static void main(String[] args) throws Exception {
        Topology topology = args.length == 0 ? Topology.FULL : Topology.fromName(args[0]);

        List<Integer> ports = List.of(5201, 5202, 5203, 5204, 5205);
        List<String> keys = List.of("alpha", "beta", "gamma", "delta", "omega");
        List<String> values = List.of("red", "green", "blue", "yellow", "white");
        List<List<Integer>> peers = topology.buildPeerPorts(ports);

        List<NodeServer> nodes = new ArrayList<>();
        try {
            System.out.println("Topology: " + topology);
            for (int i = 0; i < ports.size(); i++) {
                NodeServer node = new NodeServer(ports.get(i), keys.get(i), values.get(i), peers.get(i));
                node.start();
                nodes.add(node);
            }

            Thread.sleep(500);
            request(ports.get(0), "alpha");
            request(ports.get(0), "omega");
            request(ports.get(2), "delta");
            request(ports.get(4), "missing");
        } finally {
            for (NodeServer node : nodes) {
                node.close();
            }
        }
    }

    private static void request(int port, String key) throws Exception {
        String response = KeyValueClient.get("localhost", port, key);
        System.out.printf("Client -> node %d, key=%s: %s%n", port, key, response);
    }
}

