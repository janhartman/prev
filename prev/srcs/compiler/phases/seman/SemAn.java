package compiler.phases.seman;

import compiler.phases.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;

/**
 * Semantic analysis.
 * 
 * @author sliva
 *
 */
public class SemAn extends Phase {

	/** The attribute linking the usage of a name to its declaration. */
	private static final AbsAttribute<AbsName, AbsDecl> declAt = new AbsAttribute<AbsName, AbsDecl>();

	public static AbsAttribute<AbsName, AbsDecl> declAt() {
		return declAt;
	}

	/**
	 * Constructs a new semantic analysis phase.
	 */
	public SemAn() {
		super("seman");
	}

	@Override
	public void close() {
		declAt.lock();
		Abstr.absTree().accept(new AbsLogger(logger).addSubvisitor(new SemLogger(logger)), null);
		super.close();
	}

}
