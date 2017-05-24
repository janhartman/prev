package compiler.phases.lincode;

import java.util.*;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;
import compiler.phases.imcgen.code.*;

public class Fragmenter extends AbsFullVisitor<Object, Object> {

    private Stack<Vector<ImcStmt>> stack;
    private ImcExpr globExpr;

    public Fragmenter() {
        this.stack = new Stack<>();
        this.globExpr = null;
    }

    /**
     * expressions
     */

    public Object visit(AbsArgs node, Object visArg) {
        Vector<ImcExpr> vec = new Vector<>(node.args().size());

        for (AbsExpr expr : node.args()) {
            ImcExpr arg = (ImcExpr) expr.accept(this, visArg);

            if (arg instanceof ImcCALL) {
                ImcTEMP t = new ImcTEMP(new Temp());
                stack.peek().add(new ImcMOVE(t, arg));
                arg = t;
            }

            vec.add(arg);
        }
        return vec;
    }

    public Object visit(AbsArrExpr node, Object visArg) {
        ImcExpr origExpr = ImcGen.exprImCode.get(node);
        ImcExpr array = (ImcExpr) node.array.accept(this, visArg);
        ImcExpr index = (ImcExpr) node.index.accept(this, visArg);

        if (array instanceof ImcMEM) {
            array = ((ImcMEM) array).addr;
        }

        if (index instanceof ImcCALL) {
            ImcTEMP t = new ImcTEMP(new Temp());
            stack.peek().add(new ImcMOVE(t, index));
            index = t;
        }

        ImcExpr size = ((ImcBINOP) ((ImcBINOP) ((ImcMEM) origExpr).addr).sndExpr).sndExpr;
        ImcBINOP times = new ImcBINOP(ImcBINOP.Oper.MUL, index, size);
        ImcBINOP plus = new ImcBINOP(ImcBINOP.Oper.ADD, array, times);

        return new ImcMEM(plus);
    }

    public Object visit(AbsAtomExpr node, Object visArg) {
        return ImcGen.exprImCode.get(node);
    }

    public Object visit(AbsBinExpr node, Object visArg) {
        ImcBINOP origExpr = (ImcBINOP) ImcGen.exprImCode.get(node);

        if (globExpr == null) {
            globExpr = origExpr;
            stack.add(new Vector<>());
        }
        Vector<ImcStmt> stmts = stack.peek();

        ImcExpr fstExpr = (ImcExpr) node.fstExpr.accept(this, visArg);

        if (!(origExpr.fstExpr instanceof ImcCONST)) {
            ImcTEMP t = new ImcTEMP(new Temp());
            stmts.add(new ImcMOVE(t, fstExpr));
            fstExpr = t;
        }

        ImcExpr sndExpr = (ImcExpr) node.sndExpr.accept(this, visArg);

        if (!(origExpr.sndExpr instanceof ImcCONST)) {
            ImcTEMP t = new ImcTEMP(new Temp());
            stmts.add(new ImcMOVE(t, sndExpr));
            sndExpr = t;
        }

        ImcBINOP newExpr = new ImcBINOP(origExpr.oper, fstExpr, sndExpr);

        if (globExpr.equals(origExpr)) {
            globExpr = newExpr;
            addGlobalCodeFragment();
        }

        return newExpr;
    }

    public Object visit(AbsCastExpr node, Object visArg) {
        return node.expr.accept(this, visArg);
    }

    public Object visit(AbsDelExpr node, Object visArg) {
        ImcMEM mem = (ImcMEM) node.expr.accept(this, visArg);
        ImcCALL freeCall = (ImcCALL) ImcGen.exprImCode.get(node);
        Vector<ImcExpr> vec = new Vector<>(freeCall.args());
        vec.remove(1);
        vec.add(mem.addr);
        return new ImcCALL(freeCall.label, vec);
    }

    @SuppressWarnings("unchecked")
    public Object visit(AbsFunName node, Object visArg) {
        ImcCALL origExpr = (ImcCALL) ImcGen.exprImCode.get(node);

        Vector<ImcExpr> args = (Vector<ImcExpr>) node.args.accept(this, visArg);
        args.add(0, origExpr.args().get(0));

        return new ImcCALL(origExpr.label, args);
    }

    public Object visit(AbsNewExpr node, Object visArg) {
        return ImcGen.exprImCode.get(node);
    }

    public Object visit(AbsRecExpr node, Object visArg) {
        ImcExpr record = (ImcExpr) node.record.accept(this, visArg);

        if (record instanceof ImcMEM) {
            record = ((ImcMEM) record).addr;
        }

        ImcExpr origExpr = ((ImcMEM) ImcGen.exprImCode.get(node)).addr;
        return new ImcMEM(new ImcBINOP(ImcBINOP.Oper.ADD, record, ((ImcBINOP) origExpr).sndExpr));
    }

    public Object visit(AbsStmtExpr node, Object visArg) {
        ImcExpr origExpr = ImcGen.exprImCode.get(node);

        if (globExpr == null) {
            globExpr = origExpr;
            stack.add(new Vector<>());
        }

        node.decls.accept(this, visArg);
        node.stmts.accept(this, visArg);
        ImcExpr newExpr = (ImcExpr) node.expr.accept(this, visArg);

        if (globExpr.equals(origExpr)) {
            globExpr = newExpr;
            addGlobalCodeFragment();
        }
        return newExpr;
    }

    public Object visit(AbsUnExpr node, Object visArg) {
        ImcExpr origExpr = ImcGen.exprImCode.get(node);

        if (globExpr == null) {
            globExpr = origExpr;
            stack.add(new Vector<>());
        }

        ImcExpr subExpr = (ImcExpr) node.subExpr.accept(this, visArg);

        if (subExpr instanceof ImcCALL) {
            ImcTEMP t = new ImcTEMP(new Temp());
            stack.peek().add(new ImcMOVE(t, subExpr));
            subExpr = t;
        }

        ImcExpr newExpr = subExpr;


        switch (node.oper) {
            case NOT:
                newExpr = new ImcUNOP(ImcUNOP.Oper.NOT, subExpr);
                break;
            case SUB:
                newExpr = new ImcUNOP(ImcUNOP.Oper.NEG, subExpr);
                break;
            case MEM:
                newExpr = ((ImcMEM) subExpr).addr;
                break;
            case VAL:
                newExpr = new ImcMEM(subExpr);
                break;
        }

        if (globExpr.equals(origExpr)) {
            globExpr = newExpr;
            addGlobalCodeFragment();
        }

        return newExpr;
    }

    public Object visit(AbsVarName node, Object visArg) {
        return ImcGen.exprImCode.get(node);
    }


    /**
     * statements
     */

    public Object visit(AbsAssignStmt node, Object visArg) {
        ImcExpr dst = (ImcExpr) node.dst.accept(this, visArg);
        ImcExpr src = (ImcExpr) node.src.accept(this, visArg);
        ImcStmt origStmt = ImcGen.stmtImCode.get(node);

        if (src instanceof ImcCALL) {
            ImcTEMP t = new ImcTEMP(new Temp());
            stack.peek().add(new ImcMOVE(t, src));
            src = t;
        }

        // array / record copying
        if (origStmt instanceof ImcSTMTS) {
            stack.peek().addAll(((ImcSTMTS) origStmt).stmts());
        } else {
            stack.peek().add(new ImcMOVE(dst, src));
        }

        return null;
    }

    public Object visit(AbsExprStmt node, Object visArg) {
        ImcExpr expr = (ImcExpr) node.expr.accept(this, visArg);
        stack.peek().add(new ImcESTMT(expr));
        return null;
    }

    public Object visit(AbsIfStmt node, Object visArg) {
        ImcExpr cond = (ImcExpr) node.cond.accept(this, visArg);

        ImcTEMP t = new ImcTEMP(new Temp());
        stack.peek().add(new ImcMOVE(t, cond));
        cond = t;

        Vector<ImcStmt> stmts = ((ImcSTMTS) ImcGen.stmtImCode.get(node)).stmts();
        Label l1 = ((ImcLABEL) stmts.get(1)).label;
        Label l2 = ((ImcLABEL) stmts.get(4)).label;
        Label l3 = new Label();

        stack.peek().add(new ImcCJUMP(cond, l1, l2));
        stack.peek().add(stmts.get(4));
        stack.peek().add(new ImcJUMP(l3));
        stack.peek().add(stmts.get(1));
        node.thenBody.accept(this, visArg);
        stack.peek().add(stmts.get(3));
        stack.peek().add(new ImcLABEL(l3));
        node.elseBody.accept(this, visArg);
        stack.peek().add(stmts.get(6));

        return null;
    }

    public Object visit(AbsStmts node, Object visArg) {
        for (AbsStmt stmt : node.stmts())
            stmt.accept(this, visArg);
        return null;
    }

    public Object visit(AbsWhileStmt node, Object visArg) {
        Vector<ImcStmt> stmts = ((ImcSTMTS) ImcGen.stmtImCode.get(node)).stmts();
        stack.peek().add(stmts.get(0));

        ImcExpr cond = (ImcExpr) node.cond.accept(this, visArg);
        Label l1 = ((ImcLABEL) stmts.get(2)).label;
        Label l2 = ((ImcLABEL) stmts.get(5)).label;
        Label l3 = new Label();

        ImcTEMP t = new ImcTEMP(new Temp());
        stack.peek().add(new ImcMOVE(t, cond));
        cond = t;

        stack.peek().add(new ImcCJUMP(cond, l1, l2));
        stack.peek().add(stmts.get(5));
        stack.peek().add(new ImcJUMP(l3));
        stack.peek().add(stmts.get(2));
        node.body.accept(this, visArg);
        stack.peek().add(stmts.get(4));
        stack.peek().add(new ImcLABEL(l3));
        return null;
    }


    /**
     * declarations
     */
    public Object visit(AbsCompDecl node, Object visArg) {
        node.type.accept(this, visArg);
        return null;
    }

    public Object visit(AbsDecls node, Object visArg) {
        for (AbsDecl decl : node.decls())
            decl.accept(this, visArg);
        return null;
    }

    public Object visit(AbsFunDef node, Object visArg) {
        Frame frame = Frames.frames.get(node);
        Temp RV = new Temp();
        Label begLabel = new Label();
        Label endLabel = new Label();
        {
            Vector<ImcStmt> canStmts = new Vector<>();
            canStmts.add(new ImcLABEL(begLabel));

            stack.add(canStmts);
            ImcExpr value = (ImcExpr) node.value.accept(this, null);
            canStmts = stack.pop();

            canStmts.add(new ImcMOVE(new ImcTEMP(RV), value));
            //canStmts.add(new ImcJUMP(endLabel));
            canStmts.add(new ImcLABEL(endLabel));

            CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
            LinCode.add(fragment);
        }
        return null;
    }

    public Object visit(AbsVarDecl node, Object visArg) {
        Access access = Frames.accesses.get(node);
        if (access instanceof AbsAccess) {
            AbsAccess absAccess = (AbsAccess) access;
            DataFragment fragment = new DataFragment(absAccess.label, absAccess.size);
            LinCode.add(fragment);
        }
        return null;
    }


    /**
     * Adds the global code fragment (not in a frame, so we make a new bogus one)
     */
    private void addGlobalCodeFragment() {
        Frame frame = new Frame(new Label(""), 0, 0, 0);
        Temp RV = new Temp();
        Label begLabel = new Label();
        Label endLabel = new Label();
        ImcStmt stmt = new ImcMOVE(new ImcTEMP(RV), globExpr);

        Vector<ImcStmt> canStmts = new Vector<>();
        canStmts.add(new ImcLABEL(begLabel));
        canStmts.addAll(stack.peek());
        canStmts.add(stmt);
        //canStmts.add(new ImcJUMP(endLabel));
        canStmts.add(new ImcLABEL(endLabel));

        CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
        LinCode.add(fragment);
    }
}
