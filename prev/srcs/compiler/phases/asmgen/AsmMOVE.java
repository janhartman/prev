package compiler.phases.asmgen;

import java.util.*;
import common.report.*;
import compiler.phases.frames.*;

/**
 * An assembly move.
 * 
 * @author sliva
 *
 */
public class AsmMOVE extends AsmOPER {

	public AsmMOVE(String instr, Vector<Temp> uses, Vector<Temp> defs, Vector<Label> jumps) {
		super(instr, uses, defs, jumps);
		if (uses.size() != 1 || defs.size() != 1 || jumps != null)
			throw new Report.InternalError();
	}

}
