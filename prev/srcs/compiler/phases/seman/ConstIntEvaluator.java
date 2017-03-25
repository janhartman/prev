package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that computes the value of a constant integer expression.
 * 
 * @author sliva
 *
 */
public class ConstIntEvaluator implements AbsVisitor<Long, Object> {

    @Override
    public Long visit(AbsArgs node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsArrExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsArrType node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsAssignStmt node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsAtomExpr node, Object visArg) {
        if (node.type == AbsAtomExpr.Type.INT)
            return new Long(node.expr);
        else
            return new Long(0);
    }

    @Override
    public Long visit(AbsAtomType node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsBinExpr node, Object visArg) {
        long val1 = node.fstExpr.accept(this, visArg);
        long val2 = node.sndExpr.accept(this, visArg);

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
                break;
        }
        return new Long(0);
    }

    @Override
    public Long visit(AbsCastExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsCompDecl node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsCompDecls node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsDecls node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsDelExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsExprStmt node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsFunDecl node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsFunDef node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsFunName node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsIfStmt node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsNewExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsParDecl node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsParDecls node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsPtrType node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsRecExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsRecType node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsStmtExpr node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsStmts node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsTypeDecl node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsTypeName node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsUnExpr node, Object visArg) {
        long mult = 1;
        if (node.oper == AbsUnExpr.Oper.SUB)
            mult = -1;

        return mult * node.subExpr.accept(this, visArg);
    }

    @Override
    public Long visit(AbsVarDecl node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsVarName node, Object visArg) {
        return new Long(0);
    }

    @Override
    public Long visit(AbsWhileStmt node, Object visArg) {
        return new Long(0);
    }



}
