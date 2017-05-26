package compiler.phases.asmgen;

import java.util.*;
import compiler.phases.frames.*;

/**
 * A general assembly operation.
 * 
 * @author sliva
 *
 */
public class AsmOPER extends AsmInstr {

	/** The string representation of the instruction. */
	private final String instr;

	/** The list of temporaries used by this instruction. */
	private final Vector<Temp> uses;

	/** The list of temporaries defined by this instruction. */
	private final Vector<Temp> defs;

	/** The list of labels this instruction can jump to. */
	private final Vector<Label> jumps;

	/**
	 * Constructs a new assembly instruction.
	 * 
	 * @param instr
	 *            The string representation of the instruction.
	 * @param uses
	 *            The list of temporaries used by this instruction.
	 * @param defs
	 *            The list of temporaries defined by this instruction.
	 * @param jumps
	 *            The list of labels this instruction can jump to.
	 */
	public AsmOPER(String instr, Vector<Temp> uses, Vector<Temp> defs, Vector<Label> jumps) {
		this.instr = instr;
		this.uses = uses == null ? new Vector<Temp>() : uses;
		this.defs = defs == null ? new Vector<Temp>() : defs;
		this.jumps = jumps == null ? new Vector<Label>() : jumps;
	}

	@Override
	public Vector<Temp> uses() {
		return new Vector<Temp>(uses);
	}

	@Override
	public Vector<Temp> defs() {
		return new Vector<Temp>(defs);
	}

	@Override
	public Vector<Label> jumps() {
		return new Vector<Label>(jumps);
	}

	@Override
	public String toString() {
		String instruction = this.instr;
		for (int i = 0; i < uses.size(); i++)
			instruction = instruction.replace("`s" + i, "T" + uses.get(i).temp);
		for (int i = 0; i < defs.size(); i++)
			instruction = instruction.replace("`d" + i, "T" + defs.get(i).temp);
		return instruction;
	}

	@Override
	public String toString(HashMap<Temp, Integer> regs) {
		String instruction = this.instr;
		for (int i = 0; i < uses.size(); i++)
			instruction = instruction.replace("`s" + i, "$" + regs.get(uses.get(i)));
		for (int i = 0; i < defs.size(); i++)
			instruction = instruction.replace("`d" + i, "$" + regs.get(defs.get(i)));
		return instruction;
	}

}
