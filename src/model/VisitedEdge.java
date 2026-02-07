package model;

public class VisitedEdge {

    private Node from;
    private Node to;
    private boolean bidirectional;

    public VisitedEdge(Node from, Node to, boolean bidirectional) {
        this.from = from;
        this.to = to;
        this.bidirectional = bidirectional;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public boolean isBidirectional() {
        return bidirectional;
    }
}
