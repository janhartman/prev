package compiler.phases.seman;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;


// checks if the expression is an L-value
public class AddrChecker implements AbsVisitor<Boolean, Object> {


    /**
     * expressions
     */

    public Boolean visit(AbsArgs node, Object visArg) {
        for (AbsExpr expr : node.args()) {
            expr.accept(this, null);
        }
        return false;
    }

    public Boolean visit(AbsArrExpr node, Object visArg) {
        node.index.accept(this, null);
        boolean isLValue = node.array.accept(this , null);
        SemAn.isLValue().put(node, isLValue);
        return isLValue;
    }

    public Boolean visit(AbsAtomExpr node, Object visArg) {
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsBinExpr node, Object visArg) {
        node.fstExpr.accept(this, null);
        node.sndExpr.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsCastExpr node, Object visArg) {
        node.type.accept(this, null);
        node.expr.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsDelExpr node, Object visArg) {
        node.expr.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsFunName node, Object visArg) {
        node.args.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsNewExpr node, Object visArg) {
        node.type.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsRecExpr node, Object visArg) {
        boolean isLValue = node.record.accept(this, null);
        SemAn.isLValue().put(node, isLValue);
        return isLValue;
    }

    public Boolean visit(AbsUnExpr node, Object visArg) {
        boolean isLValue = (node.subExpr.accept(this, null) && node.oper == AbsUnExpr.Oper.VAL);
        SemAn.isLValue().put(node, isLValue);
        return isLValue;
    }

    public Boolean visit(AbsVarName node, Object visArg) {
        AbsDecl decl = SemAn.declAt().get(node);
        boolean isLValue = (decl instanceof AbsVarDecl) || (decl instanceof AbsParDecl);
        SemAn.isLValue().put(node, isLValue);
        return isLValue;
    }



    /**
     * types
     */

    public Boolean visit(AbsArrType node, Object visArg) {
        node.len.accept(this, null);
        node.elemType.accept(this, null);
        return false;
    }

    public Boolean visit(AbsAtomType node, Object visArg) {
        return false;
    }

    public Boolean visit(AbsPtrType node, Object visArg) {
        node.subType.accept(this, null);
        return false;
    }

    public Boolean visit(AbsRecType node, Object visArg) {
        node.compDecls.accept(this, null);
        return false;
    }

    public Boolean visit(AbsTypeName node, Object visArg) {
        return false;
    }



    /**
     * statements
     */

    public Boolean visit(AbsAssignStmt node, Object visArg) {
        node.dst.accept(this, null);
        node.src.accept(this, null);
        return false;
    }

    public Boolean visit(AbsExprStmt node, Object visArg) {
        node.expr.accept(this, null);
        return false;
    }

    public Boolean visit(AbsIfStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.thenBody.accept(this, null);
        node.elseBody.accept(this, null);
        return false;
    }

    public Boolean visit(AbsStmtExpr node, Object visArg) {
        node.decls.accept(this, null);
        node.stmts.accept(this, null);
        node.expr.accept(this, null);
        SemAn.isLValue().put(node, false);
        return false;
    }

    public Boolean visit(AbsStmts node, Object visArg) {
        for (AbsStmt stmt : node.stmts()) {
            stmt.accept(this, null);
        }
        return false;
    }

    public Boolean visit(AbsWhileStmt node, Object visArg) {
        node.cond.accept(this, null);
        node.body.accept(this, null);
        return false;
    }




    /**
     * declarations
     */

    public Boolean visit(AbsCompDecl node, Object visArg) {
        node.type.accept(this, null);
        return false;
    }

    public Boolean visit(AbsCompDecls node, Object visArg) {
        for (AbsCompDecl compDecl : node.compDecls()) {
            compDecl.accept(this, null);
        }
        return false;
    }

    public Boolean visit(AbsDecls node, Object visArg) {
        for (AbsDecl decl : node.decls()) {
            decl.accept(this, null);
        }
        return false;
    }

    public Boolean visit(AbsFunDecl node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        return false;
    }

    public Boolean visit(AbsFunDef node, Object visArg) {
        node.type.accept(this, null);
        node.parDecls.accept(this, null);
        node.value.accept(this, null);
        return false;
    }

    public Boolean visit(AbsParDecl node, Object visArg) {
        node.type.accept(this, null);
        return false;
    }

    public Boolean visit(AbsParDecls node, Object visArg) {
        for (AbsParDecl parDecl : node.parDecls()) {
            parDecl.accept(this, null);
        }
        return false;
    }

    public Boolean visit(AbsTypeDecl node, Object visArg) {
        node.type.accept(this, null);
        return false;
    }

    public Boolean visit(AbsVarDecl node, Object visArg) {
        node.type.accept(this, null);
        return false;
    }




}
