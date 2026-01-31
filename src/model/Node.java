package model;
import java.util.Objects;

public class Node {
    private String id;
    private int x, y; // Coordenadas para la imagen de fondo [cite: 18]

    public Node(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Getters
    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return id; }
}
