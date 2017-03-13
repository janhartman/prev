package compiler.phases.synan;

import compiler.phases.synan.dertree.*;

/**
 * The visitor that visits every node.
 * 
 * @author sliva
 *
 * @param <Result>
 *            The result the visitor produces.
 * @param <Arg>
 *            The argument the visitor carries around.
 */
public class DerFullVisitor<Result, Arg> implements DerVisitor<Result, Arg> {

	@Override
	public Result visit(DerLeaf leaf, Arg visArg) {
		return null;
	}

	@Override
	public Result visit(DerNode node, Arg visArg) {
		for (DerTree subtree : node.subtrees())
			subtree.accept(this, visArg);
		return null;
	}

}
