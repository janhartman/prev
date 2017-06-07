package compiler.phases.synan;

import compiler.phases.synan.dertree.DerLeaf;
import compiler.phases.synan.dertree.DerNode;
import compiler.phases.synan.dertree.DerTree;

/**
 * The visitor that produces the log of the derivation tree.
 *
 * @author sliva
 */
public class DerPrinter implements DerVisitor<Object, Object> {

    @Override
    public Object visit(DerLeaf leaf, Object visArg) {
        System.out.print(leaf.symb.toString() + " ");
        return null;
    }

    @Override
    public Object visit(DerNode node, Object visArg) {
        for (DerTree subTree : node.subtrees()) {
            subTree.accept(this, visArg);
        }
        return null;
    }

}
