package compiler.phases.imcgen;

import compiler.phases.abstr.AbsVisitor;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.Frame;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.code.*;
import compiler.phases.seman.SemAn;
import compiler.phases.seman.type.SemArrType;
import compiler.phases.seman.type.SemRecType;
import compiler.phases.seman.type.SemType;

import java.util.Stack;
import java.util.Vector;

public class ImcStmtGenerator implements AbsVisitor<ImcStmt, Stack<Frame>> {

    private ImcSTMTS copyArrRec(ImcExpr dst, ImcExpr src, SemType type) {
        long typeSize = type.size();
        Vector<ImcStmt> vec = new Vector<>();

        if (dst instanceof ImcMEM) {
            dst = ((ImcMEM) dst).addr;
        }
        if (src instanceof ImcMEM) {
            src = ((ImcMEM) src).addr;
        }

        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();

        // temporary variables for offset and size - copying one 8-byte word at a time
        ImcTEMP offset = new ImcTEMP(new Temp());
        ImcTEMP size = new ImcTEMP(new Temp());

        ImcBINOP dstOp = new ImcBINOP(ImcBINOP.Oper.ADD, dst, offset);
        ImcBINOP srcOp = new ImcBINOP(ImcBINOP.Oper.ADD, src, offset);
        ImcBINOP offsetOp = new ImcBINOP(ImcBINOP.Oper.ADD, offset, new ImcCONST(8));
        ImcExpr cond = new ImcBINOP(ImcBINOP.Oper.LTH, offset, size);

        vec.add(new ImcMOVE(offset, new ImcCONST(0)));
        vec.add(new ImcLABEL(l0));
        vec.add(new ImcMOVE(size, new ImcCONST(typeSize)));

        ImcTEMP t = new ImcTEMP(new Temp());
        vec.add(new ImcMOVE(t, cond));
        cond = t;

        vec.add(new ImcCJUMP(cond, l1, l2));
        vec.add(new ImcLABEL(l2));
        vec.add(new ImcJUMP(l3));
        vec.add(new ImcLABEL(l1));
        vec.add(new ImcMOVE(new ImcMEM(dstOp), new ImcMEM(srcOp)));
        vec.add(new ImcMOVE(offset, offsetOp));
        vec.add(new ImcJUMP(l0));
        vec.add(new ImcLABEL(l3));

        return new ImcSTMTS(vec);
    }

    /**
     * statements
     */

    public ImcStmt visit(AbsAssignStmt node, Stack<Frame> stack) {
        ImcExpr dst = node.dst.accept(new ImcExprGenerator(), stack);
        ImcExpr src = node.src.accept(new ImcExprGenerator(), stack);
        ImcStmt move = new ImcMOVE(dst, src);

        SemType type = SemAn.isOfType().get(node.dst);
        if (type.isAKindOf(SemArrType.class) || type.isAKindOf(SemRecType.class)) {
            move = copyArrRec(dst, src, type);
        }
        ImcGen.stmtImCode.put(node, move);
        return move;
    }


    public ImcStmt visit(AbsExprStmt node, Stack<Frame> stack) {
        ImcESTMT estmt = new ImcESTMT(node.expr.accept(new ImcExprGenerator(), stack));
        ImcGen.stmtImCode.put(node, estmt);
        return estmt;
    }


    public ImcStmt visit(AbsIfStmt node, Stack<Frame> stack) {
        ImcExpr cond = node.cond.accept(new ImcExprGenerator(), stack);
        ImcStmt thenBody = node.thenBody.accept(this, stack);
        ImcStmt elseBody = node.elseBody.accept(this, stack);

        Vector<ImcStmt> stmts = new Vector<>();
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();

        stmts.add(new ImcCJUMP(cond, l1, l2));
        stmts.add(new ImcLABEL(l1));
        stmts.add(thenBody);
        stmts.add(new ImcJUMP(l3));
        stmts.add(new ImcLABEL(l2));
        stmts.add(elseBody);
        stmts.add(new ImcLABEL(l3));

        ImcSTMTS ifStmt = new ImcSTMTS(stmts);
        ImcGen.stmtImCode.put(node, ifStmt);
        return ifStmt;
    }


    public ImcStmt visit(AbsStmts node, Stack<Frame> stack) {
        Vector<ImcStmt> stmts = new Vector<>(node.stmts().size());
        for (AbsStmt stmt : node.stmts()) {
            stmts.add(stmt.accept(this, stack));
        }

        return new ImcSTMTS(stmts);
    }


    public ImcStmt visit(AbsWhileStmt node, Stack<Frame> stack) {
        ImcExpr cond = node.cond.accept(new ImcExprGenerator(), stack);
        ImcStmt body = node.body.accept(this, stack);

        Vector<ImcStmt> stmts = new Vector<>();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();

        stmts.add(new ImcLABEL(l0));
        stmts.add(new ImcCJUMP(cond, l1, l2));
        stmts.add(new ImcLABEL(l1));
        stmts.add(body);
        stmts.add(new ImcJUMP(l0));
        stmts.add(new ImcLABEL(l2));

        ImcSTMTS whileStmt = new ImcSTMTS(stmts);
        ImcGen.stmtImCode.put(node, whileStmt);
        return whileStmt;
    }

}
