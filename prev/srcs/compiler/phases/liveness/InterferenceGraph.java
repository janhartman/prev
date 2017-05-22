package compiler.phases.liveness;

import compiler.phases.frames.Temp;

import java.util.HashSet;

/**
 * @author jan
 */
class InterferenceGraph {

    private HashSet<Interference> interferences;

    public InterferenceGraph() {
        this.interferences = new HashSet<>();
    }

    public void add(Temp t1, Temp t2) {
        interferences.add(new Interference(t1, t2));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Interference e : interferences) {
            sb.append(e.toString()+"\n");
        }

        return sb.toString();
    }

}


