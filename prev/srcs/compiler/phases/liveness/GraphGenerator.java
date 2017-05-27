package compiler.phases.liveness;

import compiler.phases.asmgen.AsmInstr;
import compiler.phases.asmgen.AsmLABEL;
import compiler.phases.asmgen.AsmMOVE;
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

        int iter = 0;

        // the algorithm for calculating interferences between variables
        do {
            oldOuts = outs;
            oldIns = ins;

            /*
            System.out.println("iter " + iter++);
            System.out.println("ins1 " + ins);
            System.out.println("ins2 " + oldIns);
            System.out.println("out1 " + outs);
            System.out.println("out2 " + oldOuts);
            System.out.println();
            */

            ins = new LinkedList<>();
            outs = new LinkedList<>();

            for (int idx = instrList.size() - 1; idx >= 0; idx--) {
                AsmInstr instr = instrList.get(idx);
                HashSet<Temp> in = new HashSet<>();
                HashSet<Temp> out = new HashSet<>();

                // add new in-vars
                in.addAll(instr.uses());
                if (oldOuts.size() > 0) {
                    int outIdx = instrList.size() - idx - 1;
                    HashSet<Temp> toAdd = (HashSet<Temp>) oldOuts.get(outIdx).clone();
                    toAdd.removeAll(instr.defs());
                    in.addAll(toAdd);
                }

                // add new out-vars
                if (instr.toString().contains("JMP") || instr.toString().contains("BNZ")) {
                    for (Label l : instr.jumps()) {
                        // find the instruction succ following label l

                        // because we go from back to front
                        int succIdx = instrList.size() - 1 - instrAfterLabel(l, instrList);

                        if (succIdx < ins.size()) {
                            out.addAll(ins.get(succIdx));
                        }

                    }
                } else {
                    if (ins.size() > 0) {
                        out.addAll(ins.get(ins.size() - 1));
                    }
                }

                ins.add(in);
                outs.add(out);
            }
        }
        while (compare(ins, oldIns) || compare(outs, oldOuts));

        addInterferences(ins);
        addInterferences(outs);

        printInsOuts(instrList, ins, outs);

        graph.addAllTemps();
        addMoves(instrList);

        return graph;
    }

    private boolean compare(LinkedList<HashSet<Temp>> list1, LinkedList<HashSet<Temp>> list2) {

        if (list1.size() != list2.size()) {
            return true;
        }

        Iterator<HashSet<Temp>> it1 = list1.iterator();
        Iterator<HashSet<Temp>> it2 = list2.iterator();

        while (it1.hasNext()) {
            HashSet<Temp> s1 = it1.next();
            HashSet<Temp> s2 = it2.next();

            if (!s1.equals(s2)) {
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
                        graph.addTemps(t1, t2);
                    }
                }
            }
        }
    }

    private int instrAfterLabel(Label label, LinkedList<AsmInstr> instrList) {
        for (int i = 0; i < instrList.size(); i++) {
            AsmInstr instr = instrList.get(i);
            if (instr instanceof AsmLABEL) {
                if (((AsmLABEL) instr).label().equals(label)) {

                    while (instrList.get(i) instanceof AsmLABEL) {
                        i++;
                    }
                    return i;
                }
            }
        }

        return -1;
    }

    private void addMoves(LinkedList<AsmInstr> instrList) {
        for (AsmInstr instr : instrList) {
            if (instr instanceof AsmMOVE) {
                graph.addMove(instr.uses().get(0), instr.defs().get(0));
            }
        }
    }

    private void printInsOuts(LinkedList<AsmInstr> instrList,
                              LinkedList<HashSet<Temp>> ins, LinkedList<HashSet<Temp>> outs) {
        System.out.println();
        for (int i = 0; i < instrList.size(); i++) {
            System.out.printf("%-15s", instrList.get(i));
            System.out.println(" " + ins.get(ins.size() - i - 1) + " " + outs.get(outs.size() - i - 1));
        }
        System.out.println();
    }

}
