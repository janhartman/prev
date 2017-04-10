package compiler.phases.frames;

import java.util.*;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.*;
import compiler.phases.seman.type.*;

public class FrameEvaluator extends AbsFullVisitor<Object, Long> {

    private int depth;

    public FrameEvaluator() {
        this.depth = 0;
    }

    /**
     * expressions
     */

    public Object visit(AbsArgs node, Long visArg) {
        return null;
    }

    public Object visit(AbsArrExpr node, Long visArg) {
        Size s1 = (Size) node.array.accept(this, visArg);
        Size s2 = (Size) node.index.accept(this, visArg);

        return new Size(s1.locsSize + s2.locsSize, max(s1.argsSize, s2.argsSize));
    }

    public Object visit(AbsAtomExpr node, Long visArg) {
        return new Size(0, 0);
    }

    public Object visit(AbsBinExpr node, Long visArg) {
        Size s1 = (Size) node.fstExpr.accept(this, visArg);
        Size s2 = (Size) node.sndExpr.accept(this, visArg);

        return new Size(s1.locsSize + s2.locsSize, max(s1.argsSize, s2.argsSize));
    }

    public Object visit(AbsCastExpr node, Long visArg) {
        return node.expr.accept(this, visArg);
    }

    public Object visit(AbsDelExpr node, Long visArg) {
        return node.expr.accept(this, visArg);
    }

    public Object visit(AbsFunName node, Long visArg) {
        AbsFunDef def = (AbsFunDef) SemAn.declAt().get(node);
        long argsSize = (Long) def.parDecls.accept(this, visArg);

        return new Size(0, argsSize);
    }

    public Object visit(AbsNewExpr node, Long visArg) {
        return new Size(0, 0);
    }

    public Object visit(AbsRecExpr node, Long visArg) {
        return node.record.accept(this, visArg);
    }

    public Object visit(AbsUnExpr node, Long visArg) {
        return node.subExpr.accept(this, visArg);
    }

    public Object visit(AbsVarName node, Long visArg) {
        return new Size(0, 0);
    }


    /**
     * statements
     */

    public Object visit(AbsAssignStmt node, Long visArg) {
        Size s1 = (Size) node.dst.accept(this, visArg);
        Size s2 = (Size) node.src.accept(this, visArg);

        return new Size(s1.locsSize + s2.locsSize, max(s1.argsSize, s2.argsSize));
    }

    public Object visit(AbsExprStmt node, Long visArg) {
        return node.expr.accept(this, visArg);
    }

    public Object visit(AbsIfStmt node, Long visArg) {
        Size s1 = (Size) node.cond.accept(this, visArg);
        Size s2 = (Size) node.thenBody.accept(this, visArg);
        Size s3 = (Size) node.elseBody.accept(this, visArg);

        return new Size(s1.locsSize + s2.locsSize + s3.locsSize, max(max(s1.argsSize, s2.argsSize), s3.argsSize));
    }

    public Object visit(AbsStmts node, Long visArg) {
        long argsSize = 0;

        for (AbsStmt stmt : node.stmts()) {
            Size s = (Size) stmt.accept(this, visArg);

            if (s.argsSize > argsSize) {
                argsSize = s.argsSize;
            }
        }

        return new Size(0, argsSize);
    }

    public Object visit(AbsWhileStmt node, Long visArg) {
        Size s1 = (Size) node.cond.accept(this, visArg);
        Size s2 = (Size) node.body.accept(this, visArg);

        return new Size(s1.locsSize + s2.locsSize, max(s1.argsSize, s2.argsSize));
    }

    public Object visit(AbsStmtExpr node, Long visArg) {
        if (depth == 0) {
            depth = 1;
        }

        long locsSize = (Long) node.decls.accept(this, visArg);
        Size s1 = (Size) node.stmts.accept(this, visArg);
        Size s2 = (Size) node.expr.accept(this, visArg);

        return new Size(locsSize + s1.locsSize + s2.locsSize, max(s1.argsSize, s2.argsSize));
    }


    /**
     * declarations
     */

    public Object visit(AbsDecls node, Long visArg) {
        long locsSize = 0;
        long SLSize = new SemPtrType(new SemVoidType()).size();

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsVarDecl) {
                locsSize += (Long) decl.accept(this, -locsSize-SLSize);
            }
            else if (decl instanceof AbsFunDecl) {
                decl.accept(this, visArg);
            }

        }

        return locsSize;
    }

    public Object visit(AbsParDecls node, Long visArg) {
        long locsSize = 0;
        long SLSize = new SemPtrType(new SemVoidType()).size();

        for (AbsParDecl parDecl : node.parDecls()) {
            locsSize += (Long) parDecl.accept(this, locsSize + SLSize);
        }

        return locsSize;
    }

    public Object visit(AbsCompDecls node, Long visArg) {
        long locsSize = 0;
        long SLSize = new SemPtrType(new SemVoidType()).size();

        for (AbsDecl compDecl : node.compDecls()) {
            locsSize += (Long) compDecl.accept(this, locsSize + SLSize);
        }

        return locsSize;
    }

    public Object visit(AbsFunDef node, Long visArg) {
        Label label = depth == 1 ? new Label(node.name) : new Label();
        node.parDecls.accept(this, visArg);

        depth++;
        Size s = (Size) node.value.accept(this, visArg);
        depth--;

        Report.info("putting new frame");
        Frames.frames.put(node, new Frame(label, depth, s.locsSize, s.argsSize + new SemPtrType(new SemVoidType()).size()));

        return null;
    }

    // TODO handle offset
    public Object visit(AbsVarDecl node, Long offset) {
        SemType type = SemAn.descType().get(node.type);
        long size = type.size();

        if (type.isAKindOf(SemRecType.class)) {
            node.type.accept(this, null);
        }

        Access access = depth == 1 ? new AbsAccess(size, new Label(node.name)) : new RelAccess(size, offset, depth);
        Frames.accesses.put(node, access);

        return size;
    }

    // TODO handle types
    public Object visit(AbsParDecl node, Long offset) {
        long size = SemAn.descType().get(node.type).size();
        Frames.accesses.put(node, new RelAccess(size, offset, depth));

        return size;
    }

    // TODO handle types
    public Object visit(AbsCompDecl node, Long offset) {
        long size = SemAn.descType().get(node.type).size();
        Frames.accesses.put(node, new RelAccess(size, offset, 0));

        return size;
    }






    private long max(long a, long b) {
        return a > b ? a : b;
    }

    private class Size {
        private long locsSize;
        private long argsSize;

        private Size(long locsSize, long argsSize) {
            this.locsSize = locsSize;
            this.argsSize = argsSize;
        }
    }


}
