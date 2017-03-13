package compiler.phases.synan;

import compiler.phases.synan.dertree.*;

/**
 * The visitor that does nothing.
 * 
 * @author sliva
 *
 * @param <Result>
 *            The result the visitor produces.
 * @param <Arg>
 *            The argument the visitor carries around.
 */
public class DerNullVisitor<Result, Arg> implements DerVisitor<Result, Arg> {

	@Override
	public Result visit(DerLeaf leaf, Arg visArg) {
		return null;
	}

	@Override
	public Result visit(DerNode node, Arg visArg) {
		return null;
	}

}
