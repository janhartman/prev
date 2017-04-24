package compiler.phases.imcgen;

import java.util.*;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.code.*;
import compiler.phases.seman.SemAn;
import compiler.phases.seman.type.SemArrType;
import compiler.phases.seman.type.SemRecType;
import compiler.phases.seman.type.SemType;

public class ImcStmtGenerator implements AbsVisitor<ImcStmt, Stack<Frame>> {

    private ImcSTMTS copyArrRec (ImcExpr dst, ImcExpr src, SemType type) {
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

        ImcTEMP offset = new ImcTEMP(new Temp());
        ImcTEMP size = new ImcTEMP(new Temp());

        ImcBINOP dstOp = new ImcBINOP(ImcBINOP.Oper.ADD, dst, new ImcMEM(offset));
        ImcBINOP srcOp = new ImcBINOP(ImcBINOP.Oper.ADD, src, new ImcMEM(offset));
        ImcBINOP offsetOp = new ImcBINOP(ImcBINOP.Oper.ADD, new ImcMEM(offset), new ImcCONST(8));
        ImcBINOP cond = new ImcBINOP(ImcBINOP.Oper.LTH, new ImcMEM(offset), new ImcMEM(size));

        vec.add(new ImcMOVE(new ImcMEM(offset), new ImcCONST(0)));
        vec.add(new ImcMOVE(new ImcMEM(size), new ImcCONST(typeSize)));
        vec.add(new ImcLABEL(l0));
        vec.add(new ImcCJUMP(cond, l1, l2));
        vec.add(new ImcLABEL(l1));
        vec.add(new ImcMOVE(new ImcMEM(dstOp), new ImcMEM(srcOp)));
        vec.add(new ImcMOVE(new ImcMEM(offset), new ImcMEM(offsetOp)));
        vec.add(new ImcJUMP(l0));
        vec.add(new ImcLABEL(l2));

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

        stmts.add(new ImcCJUMP(cond, l1, l2));
        stmts.add(new ImcLABEL(l1));
        stmts.add(thenBody);
        stmts.add(new ImcLABEL(l2));
        stmts.add(elseBody);

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


    public ImcStmt visit(AbsDecls node, Stack<Frame> stack) {
        Vector<ImcStmt> vec = new Vector<>();

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsVarDecl && (stack == null || stack.empty())) {
                vec.add(new ImcLABEL(((AbsAccess) Frames.accesses.get((AbsVarDecl) decl)).label));
            }
        }
        return new ImcSTMTS(vec);
    }
}
