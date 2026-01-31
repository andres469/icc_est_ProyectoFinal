package model;

import java.util.*;

public class Graph {

    private Map<Node, List<Node>> adjList = new HashMap<>();

    // =========================
    // Agregar nodo
    // =========================
    public void addNode(Node node) {
        if (node == null) return;
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    // =========================
    // Agregar arista NO dirigida (sin duplicados)
    // =========================
    public void addEdge(Node a, Node b) {
        if (a == null || b == null || a.equals(b)) return;

        adjList.putIfAbsent(a, new ArrayList<>());
        adjList.putIfAbsent(b, new ArrayList<>());

        if (!adjList.get(a).contains(b)) {
            adjList.get(a).add(b);
        }
        if (!adjList.get(b).contains(a)) {
            adjList.get(b).add(a);
        }
    }

    public Map<Node, List<Node>> getAdjList() {
        return adjList;
    }

    // =========================
    // BFS (CORRECTO – NO TOCAR)
    // =========================
    public List<Node> bfs(Node start, Node target,
                          List<Node> visitedOrder,
                          List<VisitedEdge> visitedEdges) {

        if (start == null || target == null) return null;

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        visitedOrder.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.equals(target)) {
                return reconstructPath(parentMap, target);
            }

            for (Node neighbor : adjList.getOrDefault(current, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);

                    visitedOrder.add(neighbor);
                    visitedEdges.add(new VisitedEdge(current, neighbor));

                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    // =========================
    // DFS (EXPLORACIÓN REAL + BACKTRACKING)
    // =========================
    public List<Node> dfs(Node start, Node target,
                          List<Node> visitedOrder,
                          List<VisitedEdge> visitedEdges) {

        if (start == null || target == null) return null;

        Set<Node> visited = new HashSet<>();
        List<Node> path = new ArrayList<>();

        boolean found = dfsRec(
                start,
                target,
                visited,
                path,
                visitedOrder,
                visitedEdges
        );

        return found ? path : null;
    }

    private boolean dfsRec(Node current,
                           Node target,
                           Set<Node> visited,
                           List<Node> path,
                           List<Node> visitedOrder,
                           List<VisitedEdge> visitedEdges) {

        visited.add(current);

        // 🔵 exploración DFS real
        visitedOrder.add(current);

        // 🔴 camino actual
        path.add(current);

        if (current.equals(target)) {
            return true;
        }

        for (Node neighbor : adjList.getOrDefault(current, List.of())) {

            // registrar TODA arista explorada (ida y retroceso)
            visitedEdges.add(new VisitedEdge(current, neighbor));

            if (!visited.contains(neighbor)) {
                if (dfsRec(
                        neighbor,
                        target,
                        visited,
                        path,
                        visitedOrder,
                        visitedEdges)) {
                    return true;
                }
            }
        }

        // ⬅️ BACKTRACKING
        path.remove(path.size() - 1);
        return false;
    }

    // =========================
    // Reconstruir camino (solo BFS)
    // =========================
    private List<Node> reconstructPath(Map<Node, Node> parents, Node target) {
        LinkedList<Node> path = new LinkedList<>();
        Node curr = target;

        while (curr != null) {
            path.addFirst(curr);
            curr = parents.get(curr);
        }
        return path;
    }

    // =========================
    // Eliminar nodo (y conexiones)
    // =========================
    public void removeNode(Node node) {
        if (node == null || !adjList.containsKey(node)) return;

        for (Node n : adjList.keySet()) {
            adjList.get(n).remove(node);
        }

        adjList.remove(node);
    }

    // =========================
    // Eliminar arista
    // =========================
    public void removeEdge(Node a, Node b) {
        if (a == null || b == null) return;

        List<Node> la = adjList.get(a);
        List<Node> lb = adjList.get(b);

        if (la != null) la.remove(b);
        if (lb != null) lb.remove(a);
    }
}
