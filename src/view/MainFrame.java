package view;

import controller.GraphController;
import java.awt.*;
import javax.swing.*;
import model.*;
import util.PersistenceManager;

/**
 * Clase principal de la interfaz gráfica siguiendo el patrón MVC.
 */
public class MainFrame extends JFrame {

    // ===== MODOS DE INTERACCIÓN =====
    public enum InteractionMode {
        NORMAL,
        DELETE_NODE,
        DELETE_EDGE
    }

    private InteractionMode currentMode = InteractionMode.NORMAL;

    // ===== LÓGICA =====
    private Graph graph;
    private GraphController controller;
    private MapPanel mapPanel;

    // ===== UI =====
    private JComboBox<String> comboAlgorithm;
    private JCheckBox checkExplorationMode;
    private JCheckBox checkUndirected; // ✅ NUEVO
    private JTextField txtInicio;
    private JTextField txtDestino;
    private JLabel lblStatus;

    private int nodeCounter = 1;

    public MainFrame() {

        
    graph = new Graph();
    controller = new GraphController(graph);

    setTitle("Ruta Óptima en Laberinto - UPS");
    setSize(1500, 1100);
    setLocationRelativeTo(null); // 👈 centra la ventana
    setResizable(false);         // 👈 DESACTIVA maximizar
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

        // ================= PANEL SUPERIOR =================
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(235, 235, 235));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());

        comboAlgorithm = new JComboBox<>(new String[]{"BFS", "DFS"});
        checkExplorationMode = new JCheckBox("Modo Exploración");

        // ✅ checkbox de dirección
        checkUndirected = new JCheckBox("Grafo NO dirigido (doble vía)");
        checkUndirected.setSelected(true);

        txtInicio = new JTextField("N1", 4);
        txtDestino = new JTextField("N2", 4);

        JButton btnRun = new JButton("Buscar Ruta");
        JButton btnClear = new JButton("Limpiar Todo");
        JButton btnMetrics = new JButton("Ver Métricas");

        JButton btnDeleteNode = new JButton("Eliminar Nodo");
        JButton btnDeleteEdge = new JButton("Eliminar Conexión");

        lblStatus = new JLabel("Modo: Normal");
        lblStatus.setForeground(new Color(0, 102, 204));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));

        controlPanel.add(new JLabel("Inicio:"));
        controlPanel.add(txtInicio);
        controlPanel.add(new JLabel("Fin:"));
        controlPanel.add(txtDestino);
        controlPanel.add(new JLabel(" | Algoritmo:"));
        controlPanel.add(comboAlgorithm);
        controlPanel.add(checkExplorationMode);
        controlPanel.add(checkUndirected);
        controlPanel.add(btnRun);
        controlPanel.add(btnClear);
        controlPanel.add(btnMetrics);
        controlPanel.add(btnDeleteNode);
        controlPanel.add(btnDeleteEdge);
        controlPanel.add(new JLabel(" | Estado: "));
        controlPanel.add(lblStatus);

        add(controlPanel, BorderLayout.NORTH);

        // ================= MAPA =================
        mapPanel = new MapPanel("data/mapa.jpg", this);
        add(mapPanel, BorderLayout.CENTER);

        // ================= PERSISTENCIA =================
        PersistenceManager.loadConfig(graph);
        actualizarContadorNodos();
        mapPanel.setGraphData(graph.getAdjList());

        // ================= EVENTOS =================

        btnMetrics.addActionListener(e -> new MetricsFrame().setVisible(true));

        btnRun.addActionListener(e -> ejecutarBusqueda());

        btnDeleteNode.addActionListener(e -> setMode(InteractionMode.DELETE_NODE));
        btnDeleteEdge.addActionListener(e -> setMode(InteractionMode.DELETE_EDGE));

        btnClear.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea borrar todos los nodos y conexiones?",
                    "Reiniciar Grafo",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                graph = new Graph();
                controller = new GraphController(graph);
                mapPanel.setGraphData(graph.getAdjList());
                mapPanel.setPath(null, null, null, false);
                nodeCounter = 1;
                PersistenceManager.saveConfig(graph.getAdjList());

                try {
                    new java.io.PrintWriter("data/tiempos_ejecucion.csv").close();
                } catch (Exception ex) {}

                setMode(InteractionMode.NORMAL);
                lblStatus.setText("Grafo reiniciado");
            }
        });
    }

    // ================= CONTROL DE MODOS =================
    private void setMode(InteractionMode mode) {

        if (currentMode == mode) {
            currentMode = InteractionMode.NORMAL;
            lblStatus.setText("Modo: Normal");
        } else {
            currentMode = mode;

            switch (mode) {
                case DELETE_NODE ->
                        lblStatus.setText("Modo: Eliminar nodo (clic sobre nodo)");
                case DELETE_EDGE ->
                        lblStatus.setText("Modo: Eliminar conexión (clic nodo A → nodo B)");
                default ->
                        lblStatus.setText("Modo: Normal");
            }
        }

        mapPanel.resetSelection();
    }

    private void ejecutarBusqueda() {

    Node start = null, target = null;

    for (Node n : graph.getAdjList().keySet()) {
        if (n.getId().equalsIgnoreCase(txtInicio.getText().trim())) {
            start = n;
        }
        if (n.getId().equalsIgnoreCase(txtDestino.getText().trim())) {
            target = n;
        }
    }

    if (start == null || target == null) {
        JOptionPane.showMessageDialog(
                this,
                "Uno o ambos nodos no existen.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    SearchResult result = controller.buscarRuta(
            (String) comboAlgorithm.getSelectedItem(),
            start,
            target
    );

    // 🔴 SI NO EXISTE RUTA
    if (result.getPath() == null || result.getPath().isEmpty()) {

        mapPanel.setPath(null, null, null, false);

        JOptionPane.showMessageDialog(
                this,
                "No existe una ruta desde " + start.getId() +
                        " hasta " + target.getId() +
                        ".\nVerifique la dirección de las conexiones.",
                "Ruta no encontrada",
                JOptionPane.WARNING_MESSAGE
        );

        lblStatus.setText("Sin ruta");
        return;
    }

    // ✅ SI EXISTE RUTA
    mapPanel.setPath(
            result.getPath(),
            result.getVisitedNodes(),
            result.getVisitedEdges(),
            checkExplorationMode.isSelected()
    );

    lblStatus.setText("Ruta encontrada");
}


    // ================= MAPA =================
    public void crearNodo(int x, int y) {
        if (currentMode != InteractionMode.NORMAL) return;

        Node nuevo = new Node("N" + nodeCounter++, x, y);
        graph.addNode(nuevo);
        PersistenceManager.saveConfig(graph.getAdjList());
        mapPanel.setGraphData(graph.getAdjList());
        lblStatus.setText("Nodo creado: " + nuevo.getId());
    }

    public void conectarNodos(Node a, Node b) {
        if (currentMode != InteractionMode.NORMAL) return;

        boolean noDirigido = checkUndirected.isSelected();
        graph.addEdge(a, b, noDirigido);

        PersistenceManager.saveConfig(graph.getAdjList());
        mapPanel.setGraphData(graph.getAdjList());

        lblStatus.setText(
                noDirigido
                        ? "Conexión creada (doble vía): " + a.getId() + " ↔ " + b.getId()
                        : "Conexión creada (una vía): " + a.getId() + " → " + b.getId()
        );
    }

    public void eliminarNodo(Node n) {
        graph.removeNode(n);
        PersistenceManager.saveConfig(graph.getAdjList());
        mapPanel.setGraphData(graph.getAdjList());
        lblStatus.setText("Nodo eliminado: " + n.getId());
    }

    public void eliminarConexion(Node a, Node b) {
        boolean noDirigido = checkUndirected.isSelected();
        graph.removeEdge(a, b, noDirigido);

        PersistenceManager.saveConfig(graph.getAdjList());
        mapPanel.setGraphData(graph.getAdjList());

        lblStatus.setText("Conexión eliminada");
    }

    public InteractionMode getCurrentMode() {
        return currentMode;
    }

    private void actualizarContadorNodos() {
        for (Node n : graph.getAdjList().keySet()) {
            try {
                int num = Integer.parseInt(n.getId().substring(1));
                if (num >= nodeCounter) nodeCounter = num + 1;
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        new java.io.File("data").mkdirs();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
