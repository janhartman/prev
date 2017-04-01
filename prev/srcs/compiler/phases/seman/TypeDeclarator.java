package compiler.phases.seman;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Declares type synonyms introduced by type declarations.
 * 
 * Methods of this visitor return {@code null} but leave their results in
 * {@link SemAn#declType()}.
 * 
 * @author sliva
 *
 */
public class TypeDeclarator implements AbsVisitor<Object, Object> {

    /**
     * declarations
     */

    // the only method that does something apart from visiting subtrees
    public Object visit(AbsTypeDecl node, Object visArg) {
        node.type.accept(this, null);
        SemAn.declType().put(node, new SemNamedType(node));
        return null;
    }

    public Object visit(AbsCompDecl node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }

    public Object visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsDecls node, Object visArg) {
        for (AbsDecl decl : node.decls()) {
            decl.accept(this, null);
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
        node.value.accept(this, null);
        return null;
    }

    public Object visit(AbsParDecl node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }

    public Object visit(AbsParDecls node, Object visArg) {
        for (AbsParDecl parDecl : node.parDecls()) {
            parDecl.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsVarDecl node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }


    /**
     * expressions
     */

    public Object visit(AbsArgs node, Object visArg) {
        for (AbsExpr expr : node.args()) {
            expr.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsArrExpr node, Object visArg) {
        node.index.accept(this, null);
        node.array.accept(this , null);
        return null;
    }

    public Object visit(AbsAtomExpr node, Object visArg) {
        return null;
    }

    public Object visit(AbsBinExpr node, Object visArg) {
        node.fstExpr.accept(this, null);
        node.sndExpr.accept(this, null);
        return null;
    }

    public Object visit(AbsCastExpr node, Object visArg) {
        node.type.accept(this, null);
        node.expr.accept(this, null);
        return null;
    }

    public Object visit(AbsDelExpr node, Object visArg) {
        node.expr.accept(this, null);
        return null;
    }

    public Object visit(AbsFunName node, Object visArg) {
        node.args.accept(this, null);
        return null;
    }

    public Object visit(AbsNewExpr node, Object visArg) {
        node.type.accept(this, null);
        return null;
    }

    public Object visit(AbsRecExpr node, Object visArg) {
        node.record.accept(this, null);
        return null;
    }

    public Object visit(AbsUnExpr node, Object visArg) {
        node.subExpr.accept(this, null);
        return null;
    }

    public Object visit(AbsVarName node, Object visArg) {
        return null;
    }



    /**
     * types
     */

    public Object visit(AbsArrType node, Object visArg) {
        node.len.accept(this, null);
        node.elemType.accept(this, null);
        return null;
    }

    public Object visit(AbsAtomType node, Object visArg) {
        return null;
    }

    public Object visit(AbsPtrType node, Object visArg) {
        node.subType.accept(this, null);
        return null;
    }

    public Object visit(AbsRecType node, Object visArg) {
        node.compDecls.accept(this, null);
        return null;
    }

    public Object visit(AbsTypeName node, Object visArg) {
        return null;
    }



    /**
     * statements
     */

    public Object visit(AbsAssignStmt node, Object visArg) {
        node.dst.accept(this, null);
        node.src.accept(this, null);
        return null;
    }

    public Object visit(AbsExprStmt node, Object visArg) {
        node.expr.accept(this, null);
        return null;
    }

    public Object visit(AbsIfStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.thenBody.accept(this, null);
        node.elseBody.accept(this, null);
        return null;
    }

    public Object visit(AbsStmtExpr node, Object visArg) {
        node.decls.accept(this, null);
        node.stmts.accept(this, null);
        node.expr.accept(this, null);
        return null;
    }

    public Object visit(AbsStmts node, Object visArg) {
        for (AbsStmt stmt : node.stmts()) {
            stmt.accept(this, null);
        }
        return null;
    }

    public Object visit(AbsWhileStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.body.accept(this, null);
        return null;
    }



}
