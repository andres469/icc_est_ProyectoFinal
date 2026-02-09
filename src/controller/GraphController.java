package controller;

import java.util.ArrayList;
import java.util.List;
import model.*;
import util.MetricsLogger;

public class GraphController {

    private Graph graph;

    public GraphController(Graph graph) {
        this.graph = graph;
    }

    public SearchResult buscarRuta(String metodo, Node inicio, Node fin) {

        List<Node> visitedNodes = new ArrayList<>();
        List<VisitedEdge> visitedEdges = new ArrayList<>();
        List<Node> ruta;

        long startTime = System.nanoTime();

        if (metodo.equalsIgnoreCase("BFS")) {
            ruta = graph.bfs(inicio, fin, visitedNodes, visitedEdges);
        } else {
            ruta = graph.dfs(inicio, fin, visitedNodes, visitedEdges);
        }

        long endTime = System.nanoTime();

        MetricsLogger.log(
            metodo,
            inicio.getId(),
            fin.getId(),
            (endTime - startTime)
        );

        return new SearchResult(ruta, visitedNodes, visitedEdges);
    }
}