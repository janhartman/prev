package compiler.phases.imcgen;

import java.util.*;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.*;
import compiler.phases.seman.type.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.code.*;

public class ImcExprGenerator implements AbsVisitor<ImcExpr, Stack<Frame>> {

    /**
     * expressions
     */


    // TODO what is actually done here
    public ImcExpr visit(AbsArrExpr node, Stack<Frame> stack) {

        return null;
    }


    public ImcExpr visit(AbsAtomExpr node, Stack<Frame> stack) {
        long value = 0;
        switch (node.type) {
            case VOID:
            case PTR:
                value = 0;
                break;
            case BOOL:
                value = Boolean.valueOf(node.expr) ? 1 : 0;
                break;
            case CHAR:
                value = Character.getNumericValue(node.expr.charAt(1));
                break;
            case INT:
                value = Long.parseLong(node.expr);
                break;
        }

        return new ImcCONST(value);
    }


    public ImcExpr visit(AbsBinExpr node, Stack<Frame> stack) {
        ImcExpr fst = node.fstExpr.accept(this, stack);
        ImcExpr snd = node.sndExpr.accept(this, stack);
        ImcBINOP.Oper oper = ImcBINOP.Oper.ADD;

        switch (node.oper) {
            case IOR:
                oper = ImcBINOP.Oper.IOR;
                break;
            case XOR:
                oper = ImcBINOP.Oper.XOR;
                break;
            case AND:
                oper = ImcBINOP.Oper.AND;
                break;
            case EQU:
                oper = ImcBINOP.Oper.EQU;
                break;
            case NEQ:
                oper = ImcBINOP.Oper.NEQ;
                break;
            case LTH:
                oper = ImcBINOP.Oper.LTH;
                break;
            case GTH:
                oper = ImcBINOP.Oper.GTH;
                break;
            case LEQ:
                oper = ImcBINOP.Oper.LEQ;
                break;
            case GEQ:
                oper = ImcBINOP.Oper.GEQ;
                break;
            case ADD:
                oper = ImcBINOP.Oper.ADD;
                break;
            case SUB:
                oper = ImcBINOP.Oper.SUB;
                break;
            case MUL:
                oper = ImcBINOP.Oper.MUL;
                break;
            case DIV:
                oper = ImcBINOP.Oper.DIV;
                break;
            case MOD:
                oper = ImcBINOP.Oper.MOD;
                break;
        }

        return new ImcBINOP(oper, fst, snd);
    }


    // TODO what is actually done here
    public ImcExpr visit(AbsCastExpr node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsDelExpr node, Stack<Frame> stack) {
        Vector<ImcExpr> vec = new Vector<>(1);
        vec.add(node.expr.accept(this, stack));
        return new ImcCALL(new Label("malloc"), vec);
    }


    public ImcExpr visit(AbsFunName node, Stack<Frame> stack) {
        Vector<ImcExpr> args = new Vector<>(node.args.args().size());

        for (AbsExpr expr : node.args.args()) {
            args.add(expr.accept(this, stack));
        }

        return new ImcCALL(stack.peek().label, args);
    }


    public ImcExpr visit(AbsNewExpr node, Stack<Frame> stack) {
        Vector<ImcExpr> vec = new Vector<>(1);
        SemType semType = SemAn.descType().get(node.type);
        vec.add(new ImcCONST(semType.size()));
        return new ImcCALL(new Label("malloc"), vec);
    }


    // TODO what is actually done here
    public ImcExpr visit(AbsRecExpr node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsStmtExpr node, Stack<Frame> stack) {
        ImcStmt stmts = node.stmts.accept(new ImcStmtGenerator(), stack);
        ImcExpr expr = node.expr.accept(this, stack);
        return new ImcSEXPR(stmts, expr);
    }


    public ImcExpr visit(AbsUnExpr node, Stack<Frame> stack) {
        ImcExpr subExpr = node.subExpr.accept(this, stack);

        switch (node.oper) {
            case NOT:
                return new ImcUNOP(ImcUNOP.Oper.NOT, subExpr);
            case ADD:
                return subExpr;
            case SUB:
                return new ImcUNOP(ImcUNOP.Oper.NEG, subExpr);
            case MEM:
            case VAL:
                return new ImcMEM(subExpr);
        }

        return null;
    }


    // TODO get right label?
    public ImcExpr visit(AbsVarName node, Stack<Frame> stack) {
        return new ImcNAME(new Label(node.name));
    }




    // TODO do we need these?

    /**
     * types
     */

    public ImcExpr visit(AbsArrType node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsAtomType node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsPtrType node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsRecType node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsTypeName node, Stack<Frame> stack) {
        return null;
    }


    /**
     * declarations
     */

    public ImcExpr visit(AbsCompDecl node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsCompDecls node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsDecls node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsFunDecl node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsFunDef node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsParDecl node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsParDecls node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsTypeDecl node, Stack<Frame> stack) {
        return null;
    }


    public ImcExpr visit(AbsVarDecl node, Stack<Frame> stack) {
        return null;
    }



    public ImcExpr visit(AbsArgs node, Stack<Frame> stack) {
        return null;
    }

}
