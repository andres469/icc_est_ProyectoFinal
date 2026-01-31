package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.Node;
import model.VisitedEdge;

public class MapPanel extends JPanel {

    private Image mapImage;
    private Map<Node, List<Node>> graphData;

    private List<Node> currentPath;
    private List<Node> explorationOrder;
    private List<Node> animatedExploration;
    private List<VisitedEdge> visitedEdges;

    private Timer explorationTimer;
    private int explorationIndex = 0;

    private boolean isExplorationMode = false;
    private boolean explorationFinished = false;

    private Node selectedNode = null;      // para conectar
    private Node tempEdgeNode = null;      // para eliminar conexión

    private MainFrame frame;

    public MapPanel(String imagePath, MainFrame frame) {
        this.frame = frame;
        this.mapImage = new ImageIcon(imagePath).getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Node clicked = findNodeNear(e.getX(), e.getY());
                MainFrame.InteractionMode mode = frame.getCurrentMode();

                // ================= ELIMINAR NODO =================
                if (mode == MainFrame.InteractionMode.DELETE_NODE) {
                    if (clicked != null) {
                        frame.eliminarNodo(clicked);
                        resetSelection();
                    }
                    return;
                }

                // ================= ELIMINAR CONEXIÓN =================
                if (mode == MainFrame.InteractionMode.DELETE_EDGE) {
                    if (clicked == null) return;

                    // Primer clic
                    if (tempEdgeNode == null) {
                        tempEdgeNode = clicked;
                        selectedNode = clicked; // feedback visual
                    }
                    // Segundo clic
                    else {
                        if (!tempEdgeNode.equals(clicked)) {
                            frame.eliminarConexion(tempEdgeNode, clicked);
                        }
                        resetSelection();
                    }
                    repaint();
                    return;
                }

                // ================= MODO NORMAL =================
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (clicked == null) {
                        frame.crearNodo(e.getX(), e.getY());
                    }
                } 
                else if (SwingUtilities.isRightMouseButton(e)) {
                    if (clicked != null) {
                        if (selectedNode == null) {
                            selectedNode = clicked;
                        } else {
                            if (!selectedNode.equals(clicked)) {
                                frame.conectarNodos(selectedNode, clicked);
                            }
                            selectedNode = null;
                        }
                        repaint();
                    }
                }
            }
        });
    }

    // =========================
    // Buscar nodo cercano
    // =========================
    private Node findNodeNear(int x, int y) {
        if (graphData == null) return null;
        for (Node n : graphData.keySet()) {
            if (Math.hypot(n.getX() - x, n.getY() - y) < 22) {
                return n;
            }
        }
        return null;
    }

    public void setGraphData(Map<Node, List<Node>> data) {
        this.graphData = data;
        repaint();
    }

    // =========================
    // ANIMACIÓN BFS / DFS
    // =========================
    public void setPath(List<Node> path,
                        List<Node> exploration,
                        List<VisitedEdge> edges,
                        boolean explorationMode) {

        this.currentPath = path;
        this.visitedEdges = edges;
        this.isExplorationMode = explorationMode;
        this.explorationFinished = false;

        if (explorationTimer != null && explorationTimer.isRunning()) {
            explorationTimer.stop();
        }

        this.explorationOrder = new ArrayList<>();
        this.animatedExploration = exploration;
        this.explorationIndex = 0;

        if (explorationMode && exploration != null) {

            explorationTimer = new Timer(300, e -> {
                if (explorationIndex < animatedExploration.size()) {
                    explorationOrder.add(animatedExploration.get(explorationIndex));
                    explorationIndex++;
                    repaint();
                } else {
                    explorationFinished = true;
                    explorationTimer.stop();
                    repaint();
                }
            });

            explorationTimer.start();

        } else {
            explorationFinished = true;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (graphData == null) return;

        Graphics2D g2 = (Graphics2D) g;

        // -------- ARISTAS --------
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        for (Node src : graphData.keySet()) {
            for (Node dest : graphData.get(src)) {
                g2.drawLine(src.getX(), src.getY(), dest.getX(), dest.getY());
            }
        }

        // -------- ARISTAS EXPLORADAS --------
        if (isExplorationMode && visitedEdges != null && explorationOrder != null) {
            g2.setColor(new Color(0, 180, 0));
            g2.setStroke(new BasicStroke(4));

            int max = Math.min(explorationOrder.size(), visitedEdges.size());
            for (int i = 0; i < max; i++) {
                VisitedEdge e = visitedEdges.get(i);
                g2.drawLine(
                        e.getFrom().getX(), e.getFrom().getY(),
                        e.getTo().getX(), e.getTo().getY()
                );
            }
        }

        // -------- RUTA FINAL --------
        if (currentPath != null && (!isExplorationMode || explorationFinished)) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(5));
            for (int i = 0; i < currentPath.size() - 1; i++) {
                Node a = currentPath.get(i);
                Node b = currentPath.get(i + 1);
                g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
            }
        }

        // -------- NODOS --------
        for (Node n : graphData.keySet()) {

            boolean seleccionado = (n == selectedNode);

            g2.setColor(seleccionado ? Color.GREEN : Color.BLUE);
            g2.fillOval(n.getX() - 15, n.getY() - 15, 30, 30);

            g2.setColor(Color.WHITE);
            g2.drawString(n.getId(), n.getX() - 10, n.getY() + 5);

            if (seleccionado) {
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(n.getX() - 18, n.getY() - 18, 36, 36);
            }
        }
    }

    // =========================
    // Reset selección
    // =========================
    public void resetSelection() {
        selectedNode = null;
        tempEdgeNode = null;
        repaint();
    }
}
