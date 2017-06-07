package compiler.phases.asmgen;

import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;

import java.util.HashMap;
import java.util.Vector;

/**
 * An assembly instruction (operation or label).
 *
 * @author sliva
 */
public abstract class AsmInstr {

    /**
     * list of temporaries used by this instruction.
     *
     * @return The list of temporaries used by this instruction.
     */
    public abstract Vector<Temp> uses();

    /**
     * Returns the list of temporaries defined by this instruction.
     *
     * @return The list of temporaries defined by this instruction.
     */
    public abstract Vector<Temp> defs();

    /**
     * Returns the list of labels this instruction can jump to.
     *
     * @return The list of labels this instruction can jump to.
     */
    public abstract Vector<Label> jumps();

    /**
     * Returns the string representation of this instruction.
     *
     * @return the string representation of this instruction.
     */
    public abstract String instr();

    /**
     * Returns a string representing this instruction with temporaries.
     *
     * @return A string representing this instruction with temporaries.
     */
    public abstract String toString();

    /**
     * Returns a string representing this instruction with registers.
     *
     * @param regs A mapping of temporaries to registers.
     * @return A string representing this instruction with registers.
     */
    public abstract String toString(HashMap<Temp, Integer> regs);

}
