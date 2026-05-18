import java.util.ArrayList;
import java.util.List;

public enum Topology {
    FULL,
    STAR,
    TREE;

    public static Topology fromName(String name) {
        return switch (name.toLowerCase()) {
            case "full" -> FULL;
            case "star" -> STAR;
            case "tree" -> TREE;
            default -> throw new IllegalArgumentException("Unknown topology: " + name);
        };
    }

    public List<List<Integer>> buildPeerPorts(List<Integer> ports) {
        List<List<Integer>> peers = new ArrayList<>();
        for (int i = 0; i < ports.size(); i++) {
            peers.add(new ArrayList<>());
        }

        switch (this) {
            case FULL -> {
                for (int i = 0; i < ports.size(); i++) {
                    for (int j = 0; j < ports.size(); j++) {
                        if (i != j) {
                            peers.get(i).add(ports.get(j));
                        }
                    }
                }
            }
            case STAR -> {
                for (int i = 1; i < ports.size(); i++) {
                    peers.get(0).add(ports.get(i));
                    peers.get(i).add(ports.get(0));
                }
            }
            case TREE -> {
                for (int i = 0; i < ports.size(); i++) {
                    int left = 2 * i + 1;
                    int right = 2 * i + 2;
                    if (left < ports.size()) {
                        link(peers, ports, i, left);
                    }
                    if (right < ports.size()) {
                        link(peers, ports, i, right);
                    }
                }
            }
        }
        return peers;
    }

    private static void link(List<List<Integer>> peers, List<Integer> ports, int a, int b) {
        peers.get(a).add(ports.get(b));
        peers.get(b).add(ports.get(a));
    }
}

