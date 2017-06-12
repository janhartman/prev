package compiler.phases.synan;

import compiler.phases.synan.dertree.DerLeaf;
import compiler.phases.synan.dertree.DerNode;

/**
 * An abstract visitor of the derivation tree.
 *
 * @param <Result> The result the visitor produces.
 * @param <Arg>    The argument the visitor carries around.
 * @author sliva
 */
public interface DerVisitor<Result, Arg> {

    public Result visit(DerLeaf leaf, Arg visArg);

    public Result visit(DerNode node, Arg visArg);

}
