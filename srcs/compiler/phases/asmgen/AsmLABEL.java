package compiler.phases.asmgen;

import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;

import java.util.HashMap;

/**
 * An assembly label.
 *
 * @author sliva
 */
public class AsmLABEL extends AsmOPER {

    /**
     * The label.
     */
    private final Label label;

    public AsmLABEL(Label label) {
        super("", null, null, null);
        this.label = label;
    }

    public Label label() {
        return label;
    }

    @Override
    public String toString() {
        return label.name;
    }

    @Override
    public String toString(HashMap<Temp, Integer> regs) {
        return label.name;
    }

}
