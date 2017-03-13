package compiler.phases.synan;

import compiler.phases.synan.dertree.*;

/**
 * An abstract visitor of the derivation tree.
 * 
 * @author sliva
 *
 * @param <Result>
 *            The result the visitor produces.
 * @param <Arg>
 *            The argument the visitor carries around.
 */
public interface DerVisitor<Result, Arg> {

	public Result visit(DerLeaf leaf, Arg visArg);

	public Result visit(DerNode node, Arg visArg);

}
