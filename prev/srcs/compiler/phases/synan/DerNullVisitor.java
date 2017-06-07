package compiler.phases.synan;

import compiler.phases.synan.dertree.DerLeaf;
import compiler.phases.synan.dertree.DerNode;

/**
 * The visitor that does nothing.
 *
 * @param <Result> The result the visitor produces.
 * @param <Arg>    The argument the visitor carries around.
 * @author sliva
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
