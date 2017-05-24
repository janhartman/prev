package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author jan
 */
class InterferenceGraph {

    private HashMap<Temp, Node> nodes;

    private HashSet<Edge> edges;

    public InterferenceGraph() {
        this.nodes = new HashMap<>();
        this.edges = new HashSet<>();
    }

    public void add(Temp t1, Temp t2) {
        Edge edge = new Edge(t1, t2);
        edges.add(edge);
        addEdgeToNode(t1, edge);
        addEdgeToNode(t2, edge);
    }

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

    public void remove(Node node) {
        if (nodes.containsKey(node.temp)) {
            nodes.remove(node.temp);
            // save node somewhere
        }
    }

    public void printEdges() {
        for (Edge e : edges) {
            System.out.println(e);
        }
    }

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


