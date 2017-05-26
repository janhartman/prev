package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author jan
 */
public class InterferenceGraph {

    /**
     * The mapping of temporary variables to actual nodes in the graph.
     */
    private HashMap<Temp, Node> nodes;

    /**
     * The edges of the graph.
     */
    private HashSet<Edge> edges;

    public InterferenceGraph() {
        this.nodes = new HashMap<>();
        this.edges = new HashSet<>();
    }

    /**
     * Add a new pair of interfering temporary variables to the graph.
     */
    public void addTemps(Temp t1, Temp t2) {
        Edge edge = new Edge(t1, t2);
        edges.add(edge);
        addEdgeToNode(t1, edge);
        addEdgeToNode(t2, edge);
    }


    public void recreate() {

    }

    /**
     * Add an edge to a node.
     */
    public void addEdgeToNode(Temp temp, Edge edge) {
        if (nodes.containsKey(temp)) {
            nodes.get(temp).addEdge(edge);
        }
        else {
            Node node = new Node(temp);
            node.addEdge(edge);
            nodes.put(temp, node);
        }
    }


    /**
     * Remove a node from the graph.
     */

    // TODO remove edges to other nodes
    public void remove(Node node) {
        if (nodes.containsKey(node.temp)) {
            nodes.remove(node.temp);
        }

    }

    /**
     * Return the first node with a degree lower than k.
     */
    public Node lowDegNode(int k) {
        for (Node node : nodes.values()) {
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
     * Print all the edges of the graph.
     */
    public void printEdges() {
        for (Edge e : edges) {
            System.out.println(e);
        }
    }

    /**
     * Print the graph as an adjacency graph.
     */
    public void printAsMatrix() {
        System.out.print("    ");

        for (Node node : nodes.values()) {
            System.out.printf("%5s", node.temp);
        }
        System.out.println();

        for (Node node : nodes.values()) {
            System.out.printf("%5s", node.temp);
            for (Node node2 : nodes.values()) {
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


