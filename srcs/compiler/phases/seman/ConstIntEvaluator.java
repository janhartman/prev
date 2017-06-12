package compiler.phases.seman;

import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that computes the value of a constant integer expression.
 *
 * @author sliva
 */
public class ConstIntEvaluator implements AbsVisitor<Long, Object> {

    public Long visit(AbsArgs node, Object visArg) {
        return null;
    }

    public Long visit(AbsArrExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsArrType node, Object visArg) {
        return null;
    }

    public Long visit(AbsAssignStmt node, Object visArg) {
        return null;
    }

    public Long visit(AbsAtomType node, Object visArg) {
        return null;
    }

    public Long visit(AbsAtomExpr node, Object visArg) {
        if (node.type == AbsAtomExpr.Type.INT)
            return new Long(node.expr);
        else
            return null;
    }

    public Long visit(AbsBinExpr node, Object visArg) {
        Long val1 = node.fstExpr.accept(this, visArg);
        Long val2 = node.sndExpr.accept(this, visArg);

        if (val1 == null || val2 == null)
            return null;

        switch (node.oper) {
            case ADD:
                return val1 + val2;
            case SUB:
                return val1 - val2;
            case MUL:
                return val1 * val2;
            case DIV:
                return val1 / val2;
            case MOD:
                return val1 % val2;
            default:
                return null;
        }

    }

    public Long visit(AbsUnExpr node, Object visArg) {
        long mult = 1;
        if (node.oper == AbsUnExpr.Oper.SUB)
            mult = -1;

        Long subValue = node.subExpr.accept(this, visArg);
        if (subValue == null)
            return null;
        else
            return mult * subValue;
    }

    public Long visit(AbsCastExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsCompDecl node, Object visArg) {
        return null;
    }

    public Long visit(AbsCompDecls node, Object visArg) {
        return null;
    }

    public Long visit(AbsDecls node, Object visArg) {
        return null;
    }

    public Long visit(AbsDelExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsExprStmt node, Object visArg) {
        return null;
    }

    public Long visit(AbsFunDecl node, Object visArg) {
        return null;
    }

    public Long visit(AbsFunDef node, Object visArg) {
        return null;
    }

    public Long visit(AbsFunName node, Object visArg) {
        return null;
    }

    public Long visit(AbsIfStmt node, Object visArg) {
        return null;
    }

    public Long visit(AbsNewExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsParDecl node, Object visArg) {
        return null;
    }

    public Long visit(AbsParDecls node, Object visArg) {
        return null;
    }

    public Long visit(AbsPtrType node, Object visArg) {
        return null;
    }

    public Long visit(AbsRecExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsRecType node, Object visArg) {
        return null;
    }

    public Long visit(AbsStmtExpr node, Object visArg) {
        return null;
    }

    public Long visit(AbsStmts node, Object visArg) {
        return null;
    }

    public Long visit(AbsTypeDecl node, Object visArg) {
        return null;
    }

    public Long visit(AbsTypeName node, Object visArg) {
        return null;
    }

    public Long visit(AbsVarDecl node, Object visArg) {
        return null;
    }

    public Long visit(AbsVarName node, Object visArg) {
        return null;
    }

    public Long visit(AbsWhileStmt node, Object visArg) {
        return null;
    }


}
