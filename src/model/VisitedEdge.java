package model;

public class VisitedEdge {
    private Node from;
    private Node to;

    public VisitedEdge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() { return from; }
    public Node getTo() { return to; }
}
