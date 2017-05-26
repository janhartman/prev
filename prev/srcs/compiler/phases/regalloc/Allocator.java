package compiler.phases.regalloc;

import compiler.phases.frames.Temp;
import compiler.phases.liveness.InterferenceGraph;
import compiler.phases.liveness.Node;

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
    public void coalesce() {
        freeze();
    }
    public void freeze() {
        spill();
    }

    public void spill() {
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
    public void select() {

    }

    public void startOver() {

    }

}
