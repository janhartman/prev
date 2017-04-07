package compiler.phases.frames;

import compiler.phases.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.*;

/**
 * Computing frames and accesses.
 * 
 * @author sliva
 *
 */
public class Frames extends Phase {

	public static final AbsAttribute<AbsFunDef, Frame> frames = new AbsAttribute<AbsFunDef, Frame>();

	public static final AbsAttribute<AbsVarDecl, Access> accesses = new AbsAttribute<AbsVarDecl, Access>();

	/**
	 * Constructs a new phase for computing frames and accesses.
	 */
	public Frames() {
		super("frames");
	}

	@Override
	public void close() {
		frames.lock();
		accesses.lock();
		Abstr.absTree().accept(
				new AbsLogger(logger).addSubvisitor(new SemLogger(logger)).addSubvisitor(new FrmLogger(logger)), null);
		super.close();
	}

}
