package compiler.phases.liveness;

import compiler.phases.frames.Temp;

/**
 * @author jan
 *
 * An object of this class represents an edge in the interference graph.
 */
class Interference {
    private Temp t1;
    private Temp t2;

    public Interference(Temp t1, Temp t2) {
        this.t1 = t1.temp < t2.temp ? t1 : t2;
        this.t2 = t1.temp < t2.temp ? t2 : t1;
    }

    public String toString() {
        return t1.toString() + " " + t2.toString();
    }
}