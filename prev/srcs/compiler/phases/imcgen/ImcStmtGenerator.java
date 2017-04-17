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
        return new ImcMOVE(dst, src);
    }


    public ImcStmt visit(AbsExprStmt node, Stack<Frame> stack) {
        return new ImcESTMT(node.expr.accept(new ImcExprGenerator(), stack));
    }


    // TODO label ?
    public ImcStmt visit(AbsIfStmt node, Stack<Frame> stack) {
        ImcExpr cond = node.cond.accept(new ImcExprGenerator(), stack);
        ImcStmt thenBody = node.thenBody.accept(this, stack);
        ImcStmt elseBody = node.elseBody.accept(this, stack);
        //return new ImcCJUMP(cond, thenBody, elseBody);
        return null;
    }


    public ImcStmt visit(AbsStmts node, Stack<Frame> stack) {
        Vector<ImcStmt> stmts = new Vector<>(node.stmts().size());
        for (AbsStmt stmt : node.stmts()) {
            stmts.add(stmt.accept(this, stack));
        }

        return new ImcSTMTS(stmts);
    }


    public ImcStmt visit(AbsWhileStmt node, Stack<Frame> stack) {
        return null;
    }

}
