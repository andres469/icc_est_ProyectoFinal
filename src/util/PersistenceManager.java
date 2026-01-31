package util;

import java.io.*;
import java.util.*;
import model.Graph;
import model.Node;

public class PersistenceManager {
    private static final String FILE_PATH = "data/config_grafo.txt";

    // Método para GUARDAR
    public static void saveConfig(Map<Node, List<Node>> adj) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Node n : adj.keySet()) {
                out.println("NODE," + n.getId() + "," + n.getX() + "," + n.getY());
            }
            for (Map.Entry<Node, List<Node>> entry : adj.entrySet()) {
                Node src = entry.getKey();
                for (Node dest : entry.getValue()) {
                    out.println("EDGE," + src.getId() + "," + dest.getId());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    // MÉTODO PARA CARGAR (Este es el que te falta o está mal escrito)
    public static void loadConfig(Graph graph) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            Map<String, Node> idMap = new HashMap<>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                
                if (parts[0].equals("NODE")) {
                    Node n = new Node(parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    graph.addNode(n);
                    idMap.put(n.getId(), n);
                } else if (parts[0].equals("EDGE")) {
                    Node src = idMap.get(parts[1]);
                    Node dest = idMap.get(parts[2]);
                    if (src != null && dest != null) {
                        graph.addEdge(src, dest);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }
}