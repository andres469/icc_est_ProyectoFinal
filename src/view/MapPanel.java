package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import model.Node;
import model.VisitedEdge;

public class MapPanel extends JPanel {

    private Image mapImage;
    private Map<Node, List<Node>> graphData;

    private List<Node> currentPath;
    private List<Node> animatedExploration;
    private List<VisitedEdge> visitedEdges;

    private Timer explorationTimer;
    private int explorationIndex = 0;

    private boolean isExplorationMode = false;
    private boolean explorationFinished = false;

    private Node selectedNode = null;
    private Node tempEdgeNode = null;

    private MainFrame frame;

    public MapPanel(String imagePath, MainFrame frame) {
        this.frame = frame;
        this.mapImage = new ImageIcon(imagePath).getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Node clicked = findNodeNear(e.getX(), e.getY());
                MainFrame.InteractionMode mode = frame.getCurrentMode();

                if (mode == MainFrame.InteractionMode.DELETE_NODE) {
                    if (clicked != null) {
                        frame.eliminarNodo(clicked);
                        resetSelection();
                    }
                    return;
                }

                if (mode == MainFrame.InteractionMode.DELETE_EDGE) {
                    if (clicked == null) return;

                    if (tempEdgeNode == null) {
                        tempEdgeNode = clicked;
                        selectedNode = clicked;
                    } else {
                        if (!tempEdgeNode.equals(clicked)) {
                            frame.eliminarConexion(tempEdgeNode, clicked);
                        }
                        resetSelection();
                    }
                    repaint();
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (clicked == null) {
                        frame.crearNodo(e.getX(), e.getY());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
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

    public void setPath(List<Node> path,
                        List<Node> exploration,
                        List<VisitedEdge> edges,
                        boolean explorationMode) {

        this.currentPath = path;
        this.visitedEdges = edges;
        this.isExplorationMode = explorationMode;
        this.explorationFinished = false;
        this.explorationIndex = 0;

        if (explorationTimer != null && explorationTimer.isRunning()) {
            explorationTimer.stop();
        }

        if (explorationMode && edges != null) {

            explorationTimer = new Timer(300, e -> {
                if (explorationIndex < edges.size()) {
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

        Stroke normal = new BasicStroke(2);
        Stroke dashed = new BasicStroke(
                2,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                new float[]{10},
                0
        );

        // ================= ARISTAS BASE =================
        Set<String> drawnEdges = new HashSet<>();

        for (Node a : graphData.keySet()) {
            for (Node b : graphData.get(a)) {

                String id = a.getId() + "-" + b.getId();
                String reverse = b.getId() + "-" + a.getId();

                boolean bidirectional =
                        graphData.containsKey(b) &&
                        graphData.get(b).contains(a);

                if (bidirectional && drawnEdges.contains(reverse)) continue;

                drawnEdges.add(id);

                g2.setStroke(bidirectional ? normal : dashed);
                g2.setColor(Color.BLACK);
                g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());

                if (!bidirectional) {
                    drawArrow(g2, a.getX(), a.getY(), b.getX(), b.getY());
                }
            }
        }

        // ================= EXPLORACIÓN VERDE =================
        if (isExplorationMode && visitedEdges != null) {

            int limit = Math.min(explorationIndex, visitedEdges.size());

            for (int i = 0; i < limit; i++) {

                VisitedEdge edge = visitedEdges.get(i);
                Node a = edge.getFrom();
                Node b = edge.getTo();

                boolean bidirectional =
                        graphData.containsKey(b) &&
                        graphData.get(b).contains(a);

                g2.setStroke(bidirectional ? new BasicStroke(3) : dashed);
                g2.setColor(Color.GREEN);
                g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());

                if (!bidirectional) {
                    drawArrow(g2, a.getX(), a.getY(), b.getX(), b.getY());
                }
            }
        }

        // ================= RUTA FINAL ROJA =================
        if (currentPath != null && (!isExplorationMode || explorationFinished)) {

            for (int i = 0; i < currentPath.size() - 1; i++) {

                Node a = currentPath.get(i);
                Node b = currentPath.get(i + 1);

                boolean bidirectional =
                        graphData.containsKey(b) &&
                        graphData.get(b).contains(a);

                if (bidirectional) {
                    g2.setStroke(new BasicStroke(5));
                } else {
                    g2.setStroke(new BasicStroke(
                            5,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_BEVEL,
                            0,
                            new float[]{12},
                            0
                    ));
                }

                g2.setColor(Color.RED);
                g2.drawLine(a.getX(), a.getY(), b.getX(), b.getY());

                if (!bidirectional) {
                    drawArrow(g2, a.getX(), a.getY(), b.getX(), b.getY());
                }
            }
        }

        // ================= NODOS =================
        for (Node n : graphData.keySet()) {

            boolean selected = (n == selectedNode);

            g2.setColor(selected ? Color.GREEN : Color.BLUE);
            g2.fillOval(n.getX() - 15, n.getY() - 15, 30, 30);

            g2.setColor(Color.WHITE);
            g2.drawString(n.getId(), n.getX() - 10, n.getY() + 5);

            if (selected) {
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(n.getX() - 18, n.getY() - 18, 36, 36);
            }
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {

        double phi = Math.toRadians(25);
        int barb = 12;

        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);

        for (int j = 0; j < 2; j++) {
            double rho = theta + (j == 0 ? phi : -phi);
            int x = (int) (x2 - barb * Math.cos(rho));
            int y = (int) (y2 - barb * Math.sin(rho));
            g2.drawLine(x2, y2, x, y);
        }
    }

    public void resetSelection() {
        selectedNode = null;
        tempEdgeNode = null;
        repaint();
    }
}
