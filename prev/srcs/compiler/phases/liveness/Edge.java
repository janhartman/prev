package compiler.phases.liveness;

import compiler.phases.frames.Temp;

/**
 * @author jan
 *
 * Represents two variables connected with a MOVE.
 */
public class Edge {

    private Temp t1;
    private Temp t2;

    public Edge(Temp t1, Temp t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public Temp t1() {
        return t1;
    }

    public Temp t2() {
        return t2;
    }

}
