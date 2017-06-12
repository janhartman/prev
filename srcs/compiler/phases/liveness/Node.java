package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashSet;

/**
 * @author jan
 *         <p>
 *         An object of this class represents a node in the interference graph.
 */
public class Node {

    /**
     * The temporary value which represents this node in the graph.
     */
    public Temp temp;

    /**
     * The neighbor nodes.
     */
    private HashSet<Node> neighbors;

    /**
     * To be used in register allocation.
     */
    public boolean spill;
    public int color;

    public Node(Temp temp) {
        this.temp = temp;
        this.neighbors = new HashSet<>();
        this.spill = false;
        this.color = -1;
    }

    public void addNeighbor(Node node) {
        neighbors.add(node);
    }

    public HashSet<Node> neighbors() {
        return neighbors;
    }

    public int deg() {
        return neighbors.size();
    }

    public boolean isNeighbor(Node node) {
        return neighbors.contains(node);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && ((Node) obj).temp == this.temp;
    }

    @Override
    public int hashCode() {
        return (int) this.temp.temp;
    }

    public String toString() {
        return temp.toString();
    }

}
