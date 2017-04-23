package compiler.phases.imcgen;

import java.util.*;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.code.*;

public class ImcStmtGenerator implements AbsVisitor<ImcStmt, Stack<Frame>> {

    /**
     * statements
     */

    public ImcStmt visit(AbsAssignStmt node, Stack<Frame> stack) {
        ImcExpr dst = node.dst.accept(new ImcExprGenerator(), stack);
        ImcExpr src = node.src.accept(new ImcExprGenerator(), stack);

        ImcMOVE move = new ImcMOVE(dst, src);
        ImcGen.stmtImCode.put(node, move);
        return move;
    }


    public ImcStmt visit(AbsExprStmt node, Stack<Frame> stack) {
        return new ImcESTMT(node.expr.accept(new ImcExprGenerator(), stack));
    }


    public ImcStmt visit(AbsIfStmt node, Stack<Frame> stack) {
        ImcExpr cond = node.cond.accept(new ImcExprGenerator(), stack);
        ImcStmt thenBody = node.thenBody.accept(this, stack);
        ImcStmt elseBody = node.elseBody.accept(this, stack);

        Vector<ImcStmt> stmts = new Vector<>();
        Label l1 = new Label();
        Label l2 = new Label();

        stmts.add(new ImcCJUMP(cond, l1, l2));
        stmts.add(new ImcLABEL(l1));
        stmts.add(thenBody);
        stmts.add(new ImcLABEL(l2));
        stmts.add(elseBody);

        return new ImcSTMTS(stmts);
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

        return new ImcSTMTS(stmts);
    }

}
