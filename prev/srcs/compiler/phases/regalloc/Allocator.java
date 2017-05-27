package compiler.phases.regalloc;

import compiler.phases.asmgen.AsmInstr;
import compiler.phases.asmgen.AsmOPER;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;
import compiler.phases.liveness.InterferenceGraph;
import compiler.phases.liveness.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

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

    /**
     * The temp -> register mapping.
     */
    private HashMap<Temp, Integer> mapping;

    /**
     * The list of assembly instructions.
     */
    private LinkedList<AsmInstr> instrList;

    /**
     * A boolean value  to indicate whether a spill has occurred or not.
     */
    public boolean spill;

    public Allocator(InterferenceGraph graph, LinkedList<AsmInstr> instrList) {
        this.graph = graph;
        this.instrList = instrList;
        this.stack = new Stack<>();
        this.mapping = new HashMap<>();
        this.spill = false;
    }

    public HashMap<Temp, Integer> mapping() {
        return mapping;
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

        mapping.put(ImcGen.SP, 254);
        mapping.put(ImcGen.FP, 253);

        int K = RegAlloc.K;

        while (! stack.empty()) {
            Node node = stack.pop();

            if (node.temp.equals(ImcGen.FP)) {
                mapping.put(ImcGen.FP, 253);
                continue;
            }

            else if (node.temp.equals(ImcGen.SP)) {
                mapping.put(ImcGen.SP, 254);
                continue;
            }

            // for each color
            boolean colorOK = true;
            for (int c = 0; c < K; c++) {
                colorOK = true;

                // check if it is not in use by neighboring nodes
                for (Node neighbor : node.neighbors()) {
                    if (neighbor.color == c) {
                        colorOK = false;
                    }
                }

                if (colorOK) {
                    node.color = c;
                    mapping.put(node.temp, c);
                    graph.addNode(node);
                    break;
                }
            }

            // could not find color for node
            if (! colorOK) {
                if (! node.spill)
                    System.out.println("Found an unspilled node with no color option: " + node);
                spills.add(node);
            }

        }

        startOver(spills);
    }

    private void startOver(LinkedList<Node> spills) {
        spill = spills.size() > 0;

        System.out.println("SPILLS: " + spills.size() + " " + spills);

        int offset = 0;

        for (Node node : spills) {

            for (int idx = 0; idx < instrList.size(); idx++) {
                AsmInstr instr = instrList.get(idx);

                if (instr.defs().contains(node.temp)) {
                    Vector<Temp> uses = new Vector<>();
                    uses.add(node.temp);
                    uses.add(ImcGen.SP);
                    AsmOPER store = new AsmOPER("STO `s0,`s1," + offset, uses, null, null);
                    instrList.add(idx+1, store);
                }

                if (instr.uses().contains(node.temp)) {
                    Vector<Temp> uses = new Vector<>();
                    Vector<Temp> defs = new Vector<>();
                    uses.add(ImcGen.SP);

                    Temp newTemp = new Temp();
                    defs.add(newTemp);
                    AsmOPER load = new AsmOPER("LDO `d0,`s0," + offset, uses, defs, null);

                    uses = instr.uses();
                    int i = uses.indexOf(node.temp);
                    uses.remove(node.temp);
                    uses.add(i, newTemp);
                    AsmOPER newInstr = new AsmOPER(instr.instr(), uses, instr.defs(), instr.jumps());

                    // remove the old instruction and replace with new one
                    instrList.remove(idx);
                    instrList.add(idx, newInstr);
                    instrList.add(idx, load);
                }

            }
        }

    }

}
