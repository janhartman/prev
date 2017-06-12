package compiler.phases.frames;

/**
 * A temporary variable.
 *
 * @author sliva
 */
public class Temp {

    /**
     * The name of a temporary variable.
     */
    public final long temp;

    /**
     * Counter of temporary variables.
     */
    private static long count = 0;

    /**
     * Creates a new temporary variable.
     */
    public Temp() {
        this.temp = count;
        count++;
        Frames.allTemps.add(this);
    }

    public String toString() {
        return "T" + temp;
    }

}
