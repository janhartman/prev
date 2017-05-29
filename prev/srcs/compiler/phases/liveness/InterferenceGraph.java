package compiler.phases.liveness;

import compiler.phases.frames.Frames;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;

import java.util.LinkedList;

/**
 * @author jan
 */
public class InterferenceGraph {

    /**
     * The list of nodes in the graph.
     */
    private LinkedList<Node> nodes;

    /**
     * The list of edges (pairs of variables connected with a MOVE).
     */
    private LinkedList<Edge> edges;


    public InterferenceGraph() {
        this.nodes = new LinkedList<>();
        this.edges = new LinkedList<>();
    }

    public LinkedList<Edge> edges() {
        return edges;
    }

    /**
     * Add a new pair of interfering temporary variables to the graph.
     */
    public void addTemps(Temp t1, Temp t2) {
        if (t1.equals(ImcGen.FP) || t2.equals(ImcGen.FP) || t1.equals(ImcGen.SP) || t1.equals(ImcGen.SP)) {
            return;
        }

        Node node1 = addNode(t1);
        Node node2 = addNode(t2);
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);
    }

    /**
     * Add a node to the graph. If it exists, return the existing value.
     */
    public Node addNode(Temp temp) {
        for (Node node : nodes) {
            if (node.temp.temp == temp.temp) {
                return node;
            }
        }

        Node newNode = new Node(temp);
        nodes.add(newNode);
        return newNode;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Adds a pair of variables connected by a MOVE.
     */
    public void addMove(Temp t1, Temp t2) {
        edges.add(new Edge(t1, t2));
    }

    /**
     * Adds all the missing temporaries (with no interferences) to the graph.
     */
    public void addAllTemps() {
        for (Temp temp : Frames.allTemps) {
            addNode(temp);
        }
    }

    /**
     * Remove a node from the graph.
     */
    public void remove(Node node) {
        for (Node neighbor : node.neighbors()) {
            neighbor.neighbors().remove(node);
        }
        nodes.remove(node);
    }

    /**
     * Return the first node with a degree lower than k.
     */
    public Node lowDegNode(int k) {
        for (Node node : nodes) {
            if (node.deg() < k) {
                return node;
            }
        }
        return null;
    }

    public int numNodes() {
        return nodes.size();
    }


    /**
     * Print the graph as an adjacency matrix.
     */
    public void printAsMatrix() {
        System.out.print("    ");

        for (Node node : nodes) {
            System.out.printf("%5s", node.temp);
        }
        System.out.println();

        for (Node node : nodes) {
            System.out.printf("%5s", node.temp);
            for (Node node2 : nodes) {
                if (node.isNeighbor(node2)) {
                    System.out.print("    x");
                }
                else {
                    System.out.print("     ");
                }
            }
            System.out.println();
        }
    }

}


