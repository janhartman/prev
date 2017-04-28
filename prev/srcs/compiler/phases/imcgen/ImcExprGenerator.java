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


    public ImcExpr visit(AbsArrExpr node, Stack<Frame> stack) {
        ImcExpr array = node.array.accept(this, stack);
        ImcExpr index = node.index.accept(this, stack);

        if (array instanceof ImcMEM) {
            array = ((ImcMEM) array).addr;
        }

        SemType exprType = SemAn.isOfType().get(node);
        ImcBINOP times = new ImcBINOP(ImcBINOP.Oper.MUL, index, new ImcCONST(exprType.size()));
        ImcBINOP plus = new ImcBINOP(ImcBINOP.Oper.ADD, array, times);
        ImcGen.exprImCode.put(node, plus);

        return new ImcMEM(plus);
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

        ImcCONST constant = new ImcCONST(value);
        ImcGen.exprImCode.put(node, constant);
        return constant;
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

        ImcBINOP binop = new ImcBINOP(oper, fst, snd);
        ImcGen.exprImCode.put(node, binop);
        return binop;
    }


    public ImcExpr visit(AbsCastExpr node, Stack<Frame> stack) {
        return node.expr.accept(this, stack);
    }


    public ImcExpr visit(AbsDelExpr node, Stack<Frame> stack) {
        ImcMEM mem = (ImcMEM) node.expr.accept(this, stack);
        Vector<ImcExpr> vec = new Vector<>(2);
        vec.add(new ImcTEMP(ImcGen.FP));
        vec.add(mem.addr);

        ImcCALL freeCall = new ImcCALL(new Label("free"), vec);
        ImcGen.exprImCode.put(node, freeCall);
        return freeCall;
    }


    public ImcExpr visit(AbsFunName node, Stack<Frame> stack) {
        AbsFunDecl decl = (AbsFunDecl) SemAn.declAt().get(node);
        Vector<ImcExpr> args = new Vector<>(node.args.args().size());
        Label label;
        ImcExpr staticLink = new ImcTEMP(ImcGen.FP);

        if (! (decl instanceof AbsFunDef)) {
            label = new Label(node.name);
        }

        else {
            AbsFunDef funDef = (AbsFunDef) decl;
            Frame frame = Frames.frames.get(funDef);
            label = frame.label;

            if (stack != null) {
                int depth = stack.size() + 1;
                while (depth > frame.depth) {
                    staticLink = new ImcMEM(staticLink);
                    depth--;
                }
            }
        }

        args.add(staticLink);
        for (AbsExpr expr : node.args.args()) {
            args.add(expr.accept(this, stack));
        }

        ImcCALL funCall = new ImcCALL(label, args);
        ImcGen.exprImCode.put(node, funCall);
        return funCall;
    }


    public ImcExpr visit(AbsNewExpr node, Stack<Frame> stack) {
        SemType semType = SemAn.descType().get(node.type);
        Vector<ImcExpr> vec = new Vector<>(2);
        vec.add(new ImcTEMP(ImcGen.FP));
        vec.add(new ImcCONST(semType.size()));

        ImcCALL mallocCall = new ImcCALL(new Label("malloc"), vec);
        ImcGen.exprImCode.put(node, mallocCall);
        return mallocCall;
    }


    public ImcExpr visit(AbsRecExpr node, Stack<Frame> stack) {
        ImcExpr record = node.record.accept(this, stack);

        if (record instanceof ImcMEM) {
            record = ((ImcMEM) record).addr;
        }

        AbsCompDecl compDecl = (AbsCompDecl) SemAn.declAt().get(node.comp);
        RelAccess relAccess = (RelAccess) Frames.accesses.get(compDecl);
        ImcCONST offset = new ImcCONST(relAccess.offset);
        ImcBINOP plus = new ImcBINOP(ImcBINOP.Oper.ADD, record, offset);
        ImcGen.exprImCode.put(node, plus);

        return new ImcMEM(plus);
    }


    public ImcExpr visit(AbsStmtExpr node, Stack<Frame> stack) {
        node.decls.accept(this, stack);
        ImcStmt stmts = node.stmts.accept(new ImcStmtGenerator(), stack);
        ImcExpr expr = node.expr.accept(this, stack);
        ImcSEXPR sexpr = new ImcSEXPR(stmts, expr);
        ImcGen.exprImCode.put(node, sexpr);
        return sexpr;
    }


    public ImcExpr visit(AbsUnExpr node, Stack<Frame> stack) {
        ImcExpr subExpr = node.subExpr.accept(this, stack);
        ImcExpr expr = null;

        switch (node.oper) {
            case NOT:
                expr = new ImcUNOP(ImcUNOP.Oper.NOT, subExpr);
                break;
            case ADD:
                expr = subExpr;
                break;
            case SUB:
                expr = new ImcUNOP(ImcUNOP.Oper.NEG, subExpr);
                break;
            case MEM:
                expr = ((ImcMEM) subExpr).addr;
                break;
            case VAL:
                expr = new ImcMEM(subExpr);
                break;
        }

        ImcGen.exprImCode.put(node, expr);
        return expr;
    }


    public ImcExpr visit(AbsVarName node, Stack<Frame> stack) {
        AbsVarDecl decl = (AbsVarDecl) SemAn.declAt().get(node);
        Access access = Frames.accesses.get(decl);
        ImcExpr expr;

        if (access instanceof AbsAccess) {
            expr = new ImcNAME(((AbsAccess) access).label);
        } else {
            RelAccess relAccess = (RelAccess) access;
            expr = new ImcTEMP(ImcGen.FP);

            int depth = stack.size() + 1;
            while (depth > relAccess.depth) {
                expr = new ImcMEM(expr);
                depth--;
            }

            expr = new ImcBINOP(ImcBINOP.Oper.ADD, expr, new ImcCONST(relAccess.offset));
        }

        expr = new ImcMEM(expr);
        ImcGen.exprImCode.put(node, expr);
        return expr;
    }


    /**
     * declarations
     */


    public ImcExpr visit(AbsDecls node, Stack<Frame> stack) {

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsFunDef) {
                decl.accept(this, stack);
            }

        }
        return null;
    }


    public ImcExpr visit(AbsFunDef node, Stack<Frame> stack) {
        if (stack == null) {
            stack = new Stack<>();
        }

        Frame frame = Frames.frames.get(node);
        stack.add(frame);
        ImcExpr expr = node.value.accept(this, stack);
        ImcGen.exprImCode.put(node.value, expr);
        stack.pop();
        return null;
    }
}
