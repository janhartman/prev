package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

import java.util.HashMap;
import java.util.Vector;

/**
 * Tests whether types constructed by {@link TypeDefiner} make sense.
 * 
 * @author sliva
 *
 */
public class TypeTester implements AbsVisitor<Object, Object> {


    /**
     * types
     */

    public Object visit(AbsArrType node, Object visArg) {
        node.elemType.accept(this, null);
        SemType arrType = SemAn.descType().get(node);

        if (arrType == null || ! arrType.isAKindOf(SemArrType.class)) {
            throw new Report.Error(node.location(), "Semantic array type not found");
        }
        return null;
    }

    public Object visit(AbsAtomType node, Object visArg) {
        SemType atomType = SemAn.descType().get(node);

        if (atomType == null) {
            throw new Report.Error(node.location(), "Semantic atom type not found");
        }
        return null;
    }

    public Object visit(AbsPtrType node, Object visArg) {
        node.subType.accept(this, null);
        SemType ptrType = SemAn.descType().get(node);

        if (ptrType == null || ! ptrType.isAKindOf(SemPtrType.class)) {
            throw new Report.Error(node.location(), "Semantic pointer type not found");
        }
        return null;
    }

    public Object visit(AbsRecType node, Object visArg) {
        node.compDecls.accept(this, null);
        SemType recType = SemAn.descType().get(node);

        if (recType == null || ! recType.isAKindOf(SemRecType.class)) {
            throw new Report.Error(node.location(), "Semantic record type not found");
        }
        return null;
    }

    public Object visit(AbsTypeName node, Object visArg) {
        SemType namedType = SemAn.descType().get(node);

        if (namedType == null)  {
            throw new Report.Error(node.location(), "Semantic named type not found");
        }

        return null;
    }


    /**
     * declarations
     */

    public Object visit(AbsDecls node, Object visArg) {
        // checkTypeHierarchy(node);

        for (AbsDecl decl : node.decls()) {
            decl.accept(this, visArg);
        }

        return null;
    }

    public Object visit(AbsTypeDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsCompDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsFunDecl node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        return null;
    }

    public Object visit(AbsFunDef node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        node.value.accept((TypeChecker) visArg, null);

        return null;
    }

    public Object visit(AbsParDecl node, Object visArg) {
        return node.type.accept(this, null);
    }

    public Object visit(AbsParDecls node, Object visArg) {
        for (AbsParDecl parDecl : node.parDecls()) {
            parDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsVarDecl node, Object visArg) {
        return node.type.accept(this, null);
    }


}
