package compiler.phases.regalloc;

import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;
import compiler.phases.liveness.InterferenceGraph;
import compiler.phases.liveness.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author jan
 *
 * Allocates the registers by coloring the interference graph.
 */
public class Allocator {

    /**
     * The interference graph.
     */
    private InterferenceGraph graph;

    /**
     * The node stack.
     */
    private Stack<Node> stack;


    public Allocator(InterferenceGraph graph) {
        this.graph = graph;
        this.stack = new Stack<>();
    }

    // TODO implement all methods

    public void simplify() {
        Node node;
        while ((node = graph.lowDegNode(RegAlloc.K)) != null) {
            stack.push(node);
            graph.remove(node);
        }

        if (graph.numNodes() == 0) {
            spill();
        }
        else {
            coalesce();
        }

    }

    // TODO check if this makes sense to implement
    private void coalesce() {
        freeze();
    }
    private void freeze() {
        spill();
    }

    private void spill() {
        // add node to stack and mark spill = true
        Node node = graph.lowDegNode(Integer.MAX_VALUE);

        if (node != null) {
            node.spill = true;
            stack.push(node);
            graph.remove(node);
            simplify();
        }
        else {
            select();
        }
    }

    // graph is empty - pop values from stack and assign colors
    private void select() {

        LinkedList<Node> spills = new LinkedList<>();

        HashMap<Temp, Integer> mapping = new HashMap<>();
        mapping.put(ImcGen.FP, 253);
        mapping.put(ImcGen.SP, 254);

        int K = RegAlloc.K;

        while (! stack.empty()) {
            Node node = stack.pop();

            // for each color
            for (int c = 0; c < K; c++) {
                boolean ok = true;

                // check if it is not in use by neighboring nodes
                for (Node neighbor : node.neighbors()) {
                    if (neighbor.color == c) {
                        ok = false;
                    }
                }

                if (ok) {
                    node.color = c;
                    mapping.put(node.temp, c);
                    graph.addNode(node);

                    break;
                }
                else {
                    if (! node.spill)
                        System.out.println("Found an unspilled node with no color option: " + node);
                    spills.add(node);
                }

            }


        }

        //System.out.println(spills);
        System.out.println(mapping);
        startOver(spills);
    }

    private void startOver(LinkedList<Node> spills) {

    }

}
