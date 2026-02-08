package model;

import java.util.*;

public class Graph {

    private Map<Node, List<Node>> adjList = new HashMap<>();

    // Agregar nodo
    public void addNode(Node node) {
        if (node == null) return;
        adjList.putIfAbsent(node, new ArrayList<>());
    }

    // Agregar arista (por defecto NO DIRIGIDA)
    public void addEdge(Node a, Node b) {
        addEdge(a, b, true);
    }

    // Agregar arista (DIRIGIDA / NO DIRIGIDA)
    // true  = no dirigida (doble vía)
    // false = dirigida
    public void addEdge(Node a, Node b, boolean noDirigido) {
        if (a == null || b == null || a.equals(b)) return;

        adjList.putIfAbsent(a, new ArrayList<>());
        adjList.putIfAbsent(b, new ArrayList<>());

        if (!adjList.get(a).contains(b)) {
            adjList.get(a).add(b);
        }

        if (noDirigido) {
            if (!adjList.get(b).contains(a)) {
                adjList.get(b).add(a);
            }
        }
    }

    public Map<Node, List<Node>> getAdjList() {
        return adjList;
    }

    // BFS
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

                    boolean bidirectional =
                            adjList.containsKey(neighbor) &&
                            adjList.get(neighbor).contains(current);

                    visitedEdges.add(
                            new VisitedEdge(current, neighbor, bidirectional)
                    );

                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    visitedOrder.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    // DFS
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
        visitedOrder.add(current);
        path.add(current);

        if (current.equals(target)) return true;

        for (Node neighbor : adjList.getOrDefault(current, List.of())) {

            if (!visited.contains(neighbor)) {

                boolean bidirectional =
                        adjList.containsKey(neighbor) &&
                        adjList.get(neighbor).contains(current);

                visitedEdges.add(
                        new VisitedEdge(current, neighbor, bidirectional)
                );

                if (dfsRec(neighbor, target,
                        visited, path, visitedOrder, visitedEdges)) {
                    return true;
                }
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    // Reconstruir camino (BFS)
    private List<Node> reconstructPath(Map<Node, Node> parents, Node target) {
        LinkedList<Node> path = new LinkedList<>();
        Node curr = target;

        while (curr != null) {
            path.addFirst(curr);
            curr = parents.get(curr);
        }
        return path;
    }

    // Eliminar nodo
    public void removeNode(Node node) {
        if (node == null || !adjList.containsKey(node)) return;

        for (Node n : adjList.keySet()) {
            adjList.get(n).remove(node);
        }

        adjList.remove(node);
    }

    // Eliminar arista (por defecto NO DIRIGIDA)
    public void removeEdge(Node a, Node b) {
        removeEdge(a, b, true);
    }

    // Eliminar arista (DIRIGIDA / NO DIRIGIDA)
    public void removeEdge(Node a, Node b, boolean noDirigido) {
        if (a == null || b == null) return;

        List<Node> la = adjList.get(a);
        if (la != null) la.remove(b);

        if (noDirigido) {
            List<Node> lb = adjList.get(b);
            if (lb != null) lb.remove(a);
        }
    }
    public boolean isBidirectional(Node a, Node b) {
    List<Node> vecinosA = adjList.get(a);
    List<Node> vecinosB = adjList.get(b);

    if (vecinosA == null || vecinosB == null) return false;

    return vecinosA.contains(b) && vecinosB.contains(a);
}

}
