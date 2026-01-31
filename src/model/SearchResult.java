package model;

import java.util.List;

public class SearchResult{

    private List<Node> path;
    private List<Node> visitedNodes;
    private List<VisitedEdge> visitedEdges;

    public SearchResult(List<Node> path,
                        List<Node> visitedNodes,
                        List<VisitedEdge> visitedEdges) {
        this.path = path;
        this.visitedNodes = visitedNodes;
        this.visitedEdges = visitedEdges;
    }

    public List<Node> getPath() {
        return path;
    }

    public List<Node> getVisitedNodes() {
        return visitedNodes;
    }

    public List<VisitedEdge> getVisitedEdges() {
        return visitedEdges;
    }
}