package compiler.phases.finphase;

import compiler.phases.Phase;

/**
 * @author jan
 */
public class FinPhase extends Phase {

    public FinPhase() {
        super("finphase");
    }

    /**
     * Finish the compilation:
     * - generate assembly code from data fragments
     * - add the prologue and epilogue to each function
     * - add the init code
     * - add the stdlib functions
     */
    public void finish() {

    }

    @Override
    public void close() {

    }
}
