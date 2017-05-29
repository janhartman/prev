package compiler.phases.regalloc;

import common.report.Report;
import compiler.phases.asmgen.AsmInstr;
import compiler.phases.asmgen.AsmOPER;
import compiler.phases.frames.Frame;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;
import compiler.phases.liveness.Edge;
import compiler.phases.liveness.InterferenceGraph;
import compiler.phases.liveness.Node;

import java.util.*;

/**
 * @author jan
 *
 * Allocates registers by coloring the interference graph.
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
     * The frame.
     */
    private Frame frame;

    /**
     * A boolean value  to indicate whether a spill has occurred or not.
     */
    public boolean spill;

    public Allocator(InterferenceGraph graph, LinkedList<AsmInstr> instrList, Frame frame) {
        this.graph = graph;
        this.instrList = instrList;
        this.stack = new Stack<>();
        this.mapping = new HashMap<>();
        this.spill = false;
        this.frame = frame;
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
            select();
        }
        else {
            coalesce();
        }

    }

    // TODO check if this makes sense to implement
    private void coalesce() {
        /*
        for (Edge e : graph.edges()) {
            Node n1 = graph.addNode(e.t1());
            Node n2 = graph.addNode(e.t2());

            HashSet<Node> allNeighbors = new HashSet<>();
            allNeighbors.addAll(n1.neighbors());
            allNeighbors.addAll(n2.neighbors());

        }
        */

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

        int K = RegAlloc.K;

        mapping.put(ImcGen.FP, 253);
        mapping.put(ImcGen.SP, 254);

        while (! stack.empty()) {
            Node node = stack.pop();

            if (node.temp.equals(ImcGen.FP) || node.temp.equals(ImcGen.SP)) {
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
                    Report.warning("Found an unspilled node with no color option: " + node);
                spills.add(node);
            }

        }

        startOver(spills);
    }

    private void startOver(LinkedList<Node> spills) {
        spill = spills.size() > 0;

        Report.info("SPILLS: " + spills.size() + " " + spills);

        long frameOffset = frame.argsSize;

        for (Node node : spills) {

            frameOffset += 8;

            for (int idx = 0; idx < instrList.size(); idx++) {
                AsmInstr instr = instrList.get(idx);

                if (instr.defs().contains(node.temp) && instr.uses().contains(node.temp)) {
                    Temp newTemp = new Temp();
                    Vector<Temp> uses = new Vector<>();
                    Vector<Temp> defs = new Vector<>();
                    uses.add(ImcGen.SP);
                    defs.add(newTemp);
                    AsmOPER load = new AsmOPER("LDO `d0,`s0," + frameOffset, uses, defs, null);

                    uses = instr.uses();
                    int i = uses.indexOf(node.temp);
                    uses.remove(node.temp);
                    uses.add(i, newTemp);
                    defs.clear();
                    defs.add(newTemp);

                    AsmOPER newInstr = new AsmOPER(instr.instr(), uses, defs, instr.jumps());

                    uses.clear();
                    uses.add(newTemp);
                    uses.add(ImcGen.SP);
                    AsmOPER store = new AsmOPER("STO `s0,`s1," + frameOffset, uses, null, null);

                    instrList.remove(idx);
                    instrList.add(idx, load);
                    instrList.add(idx+1, newInstr);
                    instrList.add(idx+2, store);
                    idx += 2;
                }

                else if (instr.defs().contains(node.temp)) {
                    Vector<Temp> uses = new Vector<>();
                    uses.add(node.temp);
                    uses.add(ImcGen.SP);
                    AsmOPER store = new AsmOPER("STO `s0,`s1," + frameOffset, uses, null, null);
                    instrList.add(idx + 1, store);
                }

                else if (instr.uses().contains(node.temp)) {
                    Temp newTemp = new Temp();
                    Vector<Temp> uses = new Vector<>();
                    Vector<Temp> defs = new Vector<>();
                    uses.add(ImcGen.SP);
                    defs.add(newTemp);

                    AsmOPER load = new AsmOPER("LDO `d0,`s0," + frameOffset, uses, defs, null);

                    uses = instr.uses();
                    int i = uses.indexOf(node.temp);
                    uses.remove(node.temp);
                    uses.add(i, newTemp);
                    AsmOPER newInstr = new AsmOPER(instr.instr(), uses, instr.defs(), instr.jumps());

                    // remove the old instruction and replace with new one
                    instrList.remove(idx);
                    instrList.add(idx, newInstr);
                    instrList.add(idx-1, load);
                }

            }
        }

        Report.info("OFFSET: " + (frameOffset - frame.argsSize));

        // add the size of saved temporaries to frame size
        frame.addTempsSize(frameOffset - frame.argsSize);
    }
}
