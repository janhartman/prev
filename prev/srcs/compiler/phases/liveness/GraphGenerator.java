package compiler.phases.liveness;

import compiler.phases.asmgen.AsmGen;
import compiler.phases.asmgen.AsmInstr;
import compiler.phases.asmgen.AsmLABEL;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author jan
 */
class GraphGenerator {

    private InterferenceGraph graph;

    public GraphGenerator() {
        this.graph = new InterferenceGraph();
    }

    @SuppressWarnings("unchecked")
    public InterferenceGraph createGraph(LinkedList<AsmInstr> instrList) {

        LinkedList<HashSet<Temp>> ins = new LinkedList<>();
        LinkedList<HashSet<Temp>> outs = new LinkedList<>();
        LinkedList<HashSet<Temp>> oldIns;
        LinkedList<HashSet<Temp>> oldOuts;

        // the algorithm for calculating interferences between variables
        do {
            oldIns = ins;
            oldOuts = outs;

            ins = new LinkedList<>();
            outs = new LinkedList<>();

            HashSet<Temp> oldIn = new HashSet<>();

            for (int i = instrList.size() - 1; i >= 0; i--) {
                AsmInstr instr = instrList.get(i);
                HashSet<Temp> in = new HashSet<>();
                HashSet<Temp> out = new HashSet<>();

                // add new in-vars
                in.addAll(instr.uses());
                if (oldOuts.size() > instrList.size() - i - 1) {
                    HashSet toAdd = oldOuts.get(i);
                    toAdd.removeAll(instr.defs());
                    in.addAll(toAdd);
                }

                // add new out-vars
                if (instr.toString().contains("JMP") || instr.toString().contains("BNZ")) {
                    for (Label l : instr.jumps()) {
                        // find the instruction succ following label l

                        // because we go from back to front
                        int index = instrList.size() - 1 - instrAfterLabel(l);

                        if (index < ins.size()) {
                            out.addAll(ins.get(index));
                        }

                    }
                } else {
                    out.addAll(oldIn);
                }

                ins.add(in);
                outs.add(out);
                oldIn = in;
            }
        }
        while (compare(ins, oldIns) || compare(outs, oldOuts));

        addInterferences(ins);
        addInterferences(outs);

        return graph;
    }

    private boolean compare(LinkedList<HashSet<Temp>> list1, LinkedList<HashSet<Temp>> list2) {
        Iterator<HashSet<Temp>> it1 = list1.iterator();
        Iterator<HashSet<Temp>> it2 = list2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return true;
            }
        }

        return false;
    }

    private void addInterferences(LinkedList<HashSet<Temp>> conns) {
        for (HashSet<Temp> s : conns) {
            for (Temp t1 : s) {
                for (Temp t2 : s) {
                    if (!t1.equals(t2)) {
                        graph.add(t1, t2);
                    }
                }
            }
        }
    }

    private int instrAfterLabel(Label label) {
        for (LinkedList<AsmInstr> instrList : AsmGen.instrs.values()) {
            for (int i = 0; i < instrList.size(); i++) {
                AsmInstr instr = instrList.get(i);
                if (instr instanceof AsmLABEL) {
                    if (((AsmLABEL) instr).label().equals(label)) {
                        return i + 1;
                    }
                }
            }
        }

        return -1;
    }

}
