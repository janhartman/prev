package compiler.phases.synan;

import common.logger.*;
import common.report.Report;
import compiler.phases.synan.dertree.*;

/**
 * The visitor that produces the log of the derivation tree.
 *
 * @author sliva
 *
 */
public class DerPrinter implements DerVisitor<Object, Object> {



    public DerPrinter() {

    }

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
