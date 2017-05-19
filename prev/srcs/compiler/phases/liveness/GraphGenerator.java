package compiler.phases.liveness;

import compiler.phases.asmgen.AsmInstr;
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

    public InterferenceGraph createGraph(LinkedList<AsmInstr> instrList) {

        LinkedList<HashSet<Temp>> ins = new LinkedList<>();
        LinkedList<HashSet<Temp>> outs = new LinkedList<>();
        LinkedList<HashSet<Temp>> oldIns = new LinkedList<>();
        LinkedList<HashSet<Temp>> oldOuts = new LinkedList<>();

        for (int i = 0; i < instrList.size(); i++) {
            oldIns.add(new HashSet<>());
            oldOuts.add(new HashSet<>());
        }


        do  {
            oldIns = ins;
            oldOuts = outs;

            ins = new LinkedList<>();
            outs = new LinkedList<>();

            for (AsmInstr instr : instrList) {
                HashSet<Temp> in = new HashSet<>();
                HashSet<Temp> out = new HashSet<>();

                // add new in-vars
                in.addAll(instr.uses());
                // in.addAll(oldOuts(instr) \ instr.defs());

                // add new out-vars
                for (Label l : instr.jumps()) {
                    // find the instruction succ following label l
                    // out.addAll(ins(succ));
                }

                ins.add(in);
                outs.add(out);
            }

        }
        while (compare(ins, oldIns) && compare(outs, oldOuts));

        addInterferences(ins);
        addInterferences(outs);

        return graph;
    }

    private boolean compare(LinkedList<HashSet<Temp>> list1, LinkedList<HashSet<Temp>> list2) {
        Iterator<HashSet<Temp>> it1 = list1.iterator();
        Iterator<HashSet<Temp>> it2 = list2.iterator();

        while(it1.hasNext() && it2.hasNext()) {
            if (! it1.next().equals(it2.next())) {
                return false;
            }
        }

        return true;
    }

    private void addInterferences(LinkedList<HashSet<Temp>> conns) {
        for (HashSet<Temp> s : conns) {
            for (Temp t1 : s) {
                for (Temp t2: s) {
                    if (! t1.equals(t2)) {
                        graph.add(t1, t2);
                    }
                }
            }
        }
    }

}
