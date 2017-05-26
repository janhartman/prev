package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author jan
 *
 * An object of this class represents a node in the interference graph.
 */
public class Node {

    public Temp temp;
    private HashSet<Edge> edges;
    public boolean spill;
    public int color;

    public Node(Temp temp) {
        this.temp = temp;
        this.edges = new HashSet<>();
        this.spill = false;
        this.color = -1;
    }

    public int deg() {
        return edges.size();
    }

    public void addEdge(Edge e) {
        this.edges.add(e);
    }

    public LinkedList<Node> neighbors() {
        LinkedList<Node> neighbors = new LinkedList<>();
        for (Edge e : edges) {
            Temp other = e.t1() == temp ? e.t2() : e.t1();
         }

        return neighbors;
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

    public void color(int c) {
        this.color = c;
    }


}
