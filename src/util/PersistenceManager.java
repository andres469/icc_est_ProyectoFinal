package util;

import java.io.*;
import java.util.*;
import model.Graph;
import model.Node;

public class PersistenceManager {

    private static final String FILE_PATH = "data/config_grafo.txt";

    // =========================
    // GUARDAR CONFIGURACIÓN
    // =========================
    public static void saveConfig(Map<Node, List<Node>> adj) {

        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {

            // Guardar nodos
            for (Node n : adj.keySet()) {
                out.println("NODE," + n.getId() + "," + n.getX() + "," + n.getY());
            }

            // Para evitar duplicar conexiones bidireccionales
            Set<String> processed = new HashSet<>();

            // Guardar aristas
            for (Map.Entry<Node, List<Node>> entry : adj.entrySet()) {

                Node src = entry.getKey();

                for (Node dest : entry.getValue()) {

                    String forwardKey = src.getId() + "->" + dest.getId();
                    String reverseKey = dest.getId() + "->" + src.getId();

                    // Detectar si es bidireccional
                    boolean isBidirectional =
                            adj.containsKey(dest) &&
                            adj.get(dest).contains(src);

                    if (isBidirectional) {

                        // Evitar guardar dos veces la misma conexión
                        if (!processed.contains(reverseKey)) {
                            out.println("EDGE," + src.getId() + "," + dest.getId() + ",true");
                            processed.add(forwardKey);
                        }

                    } else {

                        out.println("EDGE," + src.getId() + "," + dest.getId() + ",false");
                        processed.add(forwardKey);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    // =========================
    // CARGAR CONFIGURACIÓN
    // =========================
    public static void loadConfig(Graph graph) {

        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {

            Map<String, Node> idMap = new HashMap<>();

            while (sc.hasNextLine()) {

                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");

                // =========================
                // Cargar nodos
                // =========================
                if (parts[0].equals("NODE")) {

                    Node n = new Node(
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3])
                    );

                    graph.addNode(n);
                    idMap.put(n.getId(), n);
                }

                // =========================
                // Cargar aristas
                // =========================
                else if (parts[0].equals("EDGE")) {

                    Node src = idMap.get(parts[1]);
                    Node dest = idMap.get(parts[2]);

                    boolean isBidirectional = Boolean.parseBoolean(parts[3]);

                    if (src != null && dest != null) {
                        graph.addEdge(src, dest, isBidirectional);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }
}
