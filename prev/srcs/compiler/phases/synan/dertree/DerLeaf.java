package compiler.phases.synan.dertree;

import common.report.*;
import compiler.phases.lexan.*;
import compiler.phases.synan.*;

/**
 * The leaf of the derivation tree.
 * 
 * @author sliva
 *
 */
public class DerLeaf extends DerTree {

	/** The CFG terminal this node represents. */
	public final Symbol symb;

	/**
	 * Construct a new leaf of the derivation tree.
	 * 
	 * @param symb The CFG terminal this node represents.
	 */
	public DerLeaf(Symbol symb) {
		this.symb = symb;
	}

	@Override
	public Location location() {
		return symb.location();
	}

	@Override
	public <Result, Arg> Result accept(DerVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
