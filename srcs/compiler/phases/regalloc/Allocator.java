package compiler.phases.regalloc;

import common.report.Report;
import compiler.phases.asmgen.AsmInstr;
import compiler.phases.asmgen.AsmOPER;
import compiler.phases.frames.Frame;
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
 *         Allocates registers by coloring the interference graph.
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
     * A boolean value to indicate whether a spill has occurred or not.
     */
    private boolean spilled;


    public Allocator(InterferenceGraph graph, LinkedList<AsmInstr> instrList, Frame frame) {
        this.graph = graph;
        this.instrList = instrList;
        this.frame = frame;
        this.stack = new Stack<>();
        this.mapping = new HashMap<>();
        this.spilled = false;
    }

    public HashMap<Temp, Integer> mapping() {
        return mapping;
    }

    public boolean spilled() {
        return spilled;
    }


    /**
     * Removes nodes with deg < K from the graph and pushes them onto the stack.
     */
    public void simplify() {
        Node node;
        while ((node = graph.lowDegNode(RegAlloc.K)) != null) {
            stack.push(node);
            graph.remove(node);
        }

        if (graph.numNodes() == 0) {
            select();
        } else {
            spill();
        }

    }

    /**
     * Adds a node with deg >= K onto the stack.
     */
    private void spill() {
        // add node to stack and mark spilled = true
        Node node = graph.lowDegNode(Integer.MAX_VALUE);

        if (node != null) {
            node.spill = true;
            stack.push(node);
            graph.remove(node);
            simplify();
        } else {
            select();
        }
    }

    /**
     * Colors the graph by popping nodes from the stack.
     */
    private void select() {

        LinkedList<Node> spills = new LinkedList<>();

        int K = RegAlloc.K;

        mapping.put(ImcGen.FP, 253);
        mapping.put(ImcGen.SP, 254);

        while (!stack.empty()) {
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
            if (!colorOK) {
                if (!node.spill)
                    Report.warning("Found an unspilled node with no color option: " + node);
                spills.add(node);
            }

        }

        startOver(spills);
    }

    /**
     * Adds necessary LOAD/STORE instructions for spilled nodes to the list of instructions.
     *
     * @param spills The list of spilled nodes.
     */
    private void startOver(LinkedList<Node> spills) {
        spilled = spills.size() > 0;

        //Report.info(frame.label + " SPILLS: " + spills.size() + " " + spills);

        long offset = frame.argsSize + frame.tempsSize;

        for (Node node : spills) {
            //Report.info("offset for node " + node.temp + " is " + offset);

            for (int idx = 0; idx < instrList.size(); idx++) {
                AsmInstr instr = instrList.get(idx);

                if (instr.defs().contains(node.temp) && instr.uses().contains(node.temp)) {
                    Temp newTemp = new Temp();
                    Vector<Temp> uses = new Vector<>();
                    Vector<Temp> defs = new Vector<>();
                    uses.add(ImcGen.SP);
                    defs.add(newTemp);
                    AsmOPER load = new AsmOPER("LDO `d0,`s0," + offset, uses, defs, null);

                    Vector<Temp> uses2 = new Vector<>();
                    Vector<Temp> defs2 = new Vector<>();
                    uses2.addAll(instr.uses());
                    int i = uses2.indexOf(node.temp);
                    uses2.remove(node.temp);
                    uses2.add(i, newTemp);
                    defs2.add(newTemp);

                    AsmOPER newInstr = new AsmOPER(instr.instr(), uses2, defs2, instr.jumps());

                    Vector<Temp> uses3 = new Vector<>();
                    uses3.add(newTemp);
                    uses3.add(ImcGen.SP);
                    AsmOPER store = new AsmOPER("STO `s0,`s1," + offset, uses3, null, null);

                    instrList.remove(idx);
                    instrList.add(idx, load);
                    instrList.add(idx + 1, newInstr);
                    instrList.add(idx + 2, store);
                    idx += 2;

                } else if (instr.defs().contains(node.temp)) {
                    Vector<Temp> uses = new Vector<>();
                    uses.add(node.temp);
                    uses.add(ImcGen.SP);
                    AsmOPER store = new AsmOPER("STO `s0,`s1," + offset, uses, null, null);
                    instrList.add(idx + 1, store);
                    idx++;
                } else if (instr.uses().contains(node.temp)) {
                    Temp newTemp = new Temp();
                    Vector<Temp> uses = new Vector<>();
                    Vector<Temp> defs = new Vector<>();
                    uses.add(ImcGen.SP);
                    defs.add(newTemp);

                    AsmOPER load = new AsmOPER("LDO `d0,`s0," + offset, uses, defs, null);
                    Vector<Temp> uses2 = new Vector<>();
                    uses2.addAll(instr.uses());
                    int i = uses2.indexOf(node.temp);
                    uses2.remove(node.temp);
                    uses2.add(i, newTemp);
                    AsmOPER newInstr = new AsmOPER(instr.instr(), uses2, instr.defs(), instr.jumps());

                    // remove the old instruction and replace with new one
                    instrList.remove(idx);
                    instrList.add(idx, newInstr);
                    instrList.add(idx, load);
                    idx++;
                }

            }
            offset += 8;
        }

        // add the size of saved temporaries to frame size
        frame.incTempsSize(offset - frame.argsSize - frame.tempsSize);
    }
}
