package compiler.phases.asmgen;

import common.report.Report;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;

import java.util.Vector;

/**
 * An assembly move.
 *
 * @author sliva
 */
public class AsmMOVE extends AsmOPER {

    public AsmMOVE(String instr, Vector<Temp> uses, Vector<Temp> defs, Vector<Label> jumps) {
        super(instr, uses, defs, jumps);
        if (uses.size() != 1 || defs.size() != 1 || jumps != null)
            throw new Report.InternalError();
    }

}
