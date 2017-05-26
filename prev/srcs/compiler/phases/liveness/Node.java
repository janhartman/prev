package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashSet;

/**
 * @author jan
 *
 * An object of this class represents a node in the interference graph.
 */
public class Node {

    public Temp temp;
    private HashSet<Edge> edges;
    public boolean spill;

    public Node(Temp temp) {
        this.temp = temp;
        this.edges = new HashSet<>();
        this.spill = false;
    }

    public int deg() {
        return edges.size();
    }

    public void addEdge(Edge e) {
        this.edges.add(e);
    }

    public boolean isNeighbor(Node node) {
        Edge edge = new Edge(temp, node.temp);
        for (Edge e : this.edges) {
            if (e.equals(edge)) {
                return true;
            }
        }
        return false;
    }


}
