package compiler.phases.liveness;

import compiler.phases.frames.Temp;

/**
 * @author jan
 *
 * An object of this class represents an edge in the interference graph.
 */
class Edge {
    private Temp t1;
    private Temp t2;

    public Edge(Temp t1, Temp t2) {
        this.t1 = t1.temp < t2.temp ? t1 : t2;
        this.t2 = t1.temp < t2.temp ? t2 : t1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Edge && this.toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return (int) (1000 * t1.temp + t2.temp);
    }

    public String toString() {
        return t1.toString() + " " + t2.toString();
    }
}