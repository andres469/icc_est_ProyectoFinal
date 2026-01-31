    package model;

    public class Edge {
        private Node source;
        private Node dest;

        public Edge(Node source, Node dest) {
            this.source = source;
            this.dest = dest;
        }
        // Getters
        public Node getSource() { return source; }
        public Node getDest() { return dest; }
    }