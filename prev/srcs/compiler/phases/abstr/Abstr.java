package compiler.phases.abstr;

import common.report.*;
import compiler.phases.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.synan.dertree.DerTree;

/**
 * Abstract syntax.
 * 
 * @author sliva
 *
 */
public class Abstr extends Phase {

	/** The costructed abstract syntax tree. */
	private static AbsExpr absTree;

	/**
	 * Returns the constructed abstract syntax tree.
	 * 
	 * @return The constructed abstract syntax tree.
	 */
	public static AbsExpr absTree() {
		return absTree;
	}

	/**
	 * Constructs a new abstract syntax phase.
	 */
	public Abstr() {
		super("abstr");
	}

	/**
	 * Converts a derivation tree to an abstract syntax tree.
	 * 
	 * @param derTree
	 *            A derivation tree.
	 * @return An abstract syntax tree.
	 */
	public AbsTree fromDerTree(DerTree derTree) {
		AbsTree absTree = derTree.accept(new DerToAbsTree(), null);
		if (!(absTree instanceof AbsExpr))
			throw new Report.InternalError();
		Abstr.absTree = (AbsExpr) absTree;
		return Abstr.absTree;
	}

	@Override
	public void close() {
		Abstr.absTree().accept(new AbsLogger(logger), null);
		super.close();
	}

}
