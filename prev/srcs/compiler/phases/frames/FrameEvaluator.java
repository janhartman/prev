package compiler.phases.frames;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.*;
import compiler.phases.seman.type.*;

import java.util.Stack;

public class FrameEvaluator extends AbsFullVisitor<Object, Long> {

    private Stack<FrameSize> stack;
    private int scope;

    public FrameEvaluator() {
        this.stack = new Stack<>();
        this.scope = 1;
    }

    /**
     * expressions
     */

    public Object visit(AbsArgs node, Long visArg) {
        for (AbsExpr arg : node.args()) {
            arg.accept(this, 1L);
        }
        return null;
    }

    public Object visit(AbsArrExpr node, Long visArg) {
        node.array.accept(this, 1L);
        node.index.accept(this, 1L);
        return null;
    }

    public Object visit(AbsAtomExpr node, Long visArg) {
        return null;
    }

    public Object visit(AbsBinExpr node, Long visArg) {
        node.fstExpr.accept(this, 1L);
        node.sndExpr.accept(this, 1L);
        return null;
    }

    public Object visit(AbsCastExpr node, Long visArg) {
        return node.expr.accept(this, 1L);
    }

    public Object visit(AbsDelExpr node, Long visArg) {
        return node.expr.accept(this, 1L);
    }

    public Object visit(AbsNewExpr node, Long visArg) {
        return null;
    }

    public Object visit(AbsRecExpr node, Long visArg) {
        return node.record.accept(this, 1L);
    }

    public Object visit(AbsUnExpr node, Long visArg) {
        return node.subExpr.accept(this, 1L);
    }

    public Object visit(AbsVarName node, Long visArg) {
        return null;
    }


    public Object visit(AbsStmtExpr node, Long visArg) {
        if (stack.empty()) {
            stack.push(new FrameSize(1));
        }

        if (visArg != null && visArg == 1L) {
            scope++;
        }

        node.decls.accept(this, 1L);
        node.stmts.accept(this, 1L);
        node.expr.accept(this, 1L);

        if (visArg != null && visArg == 1L) {
            scope--;
        }
        return null;
    }

    public Object visit(AbsFunName node, Long visArg) {
        node.args.accept(this, 1L);
        AbsFunDecl def = (AbsFunDecl) SemAn.declAt().get(node);
        if (! (def instanceof AbsFunDef)) {
            return null;
        }
        long argsSize = (Long) def.parDecls.accept(this, -1L) + new SemPtrType(new SemVoidType()).size();

        FrameSize fs = stack.peek();
        fs.argsSize = argsSize > fs.argsSize ? argsSize : fs.argsSize;

        return null;
    }


    /**
     * declarations
     */

    public Object visit(AbsDecls node, Long visArg) {
        FrameSize fs = stack.peek();

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsVarDecl) {
                long size = (Long) decl.accept(this, null);
                fs.locsSize += size;

                if (!(fs.depth == 1 && scope == 1)) {
                    fs.offset -= size;
                }
            }
        }

        for (AbsDecl decl : node.decls()) {
            if (decl instanceof AbsFunDecl || decl instanceof AbsTypeDecl) {
                decl.accept(this, null);
            }
        }

        return fs.locsSize;
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

        for (AbsDecl compDecl : node.compDecls()) {
            locsSize += (Long) compDecl.accept(this, locsSize);
        }

        return locsSize;
    }

    public Object visit(AbsFunDef node, Long visArg) {
        FrameSize fs = stack.peek();
        Label label = (fs.depth == 1 && scope == 1) ? new Label(node.name) : new Label();

        stack.push(new FrameSize(fs.depth + 1));
        node.parDecls.accept(this, visArg);

        node.value.accept(this, visArg);
        FrameSize newFrame = stack.pop();

        //Report.info("new frame, name: " + label.name + " depth: " + fs.depth + " locs: " + newFrame.locsSize + " args: " + newFrame.argsSize);
        Frames.frames.put(node, new Frame(label, fs.depth, newFrame.locsSize, newFrame.argsSize));

        return null;
    }

    public Object visit(AbsVarDecl node, Long visArg) {
        long size =  SemAn.descType().get(node.type).size();
        node.type.accept(this, null);
        FrameSize fs = stack.peek();

        //Report.info("new " + ((fs.depth == 1 && scope == 1) ? "abs access, " + node.name : "rel access, offset: " + fs.offset + " depth: " + fs.depth));
        Access access = (fs.depth == 1) ? new AbsAccess(size, (scope == 1) ? new Label(node.name) : new Label()) : new RelAccess(size, fs.offset-size, fs.depth);

        Frames.accesses.put(node, access);
        return size;
    }

    // visArg = offset in this case as we do not need to track it globally
    public Object visit(AbsParDecl node, Long offset) {
        long size = SemAn.descType().get(node.type).size();
        node.type.accept(this, null);
        FrameSize fs = stack.peek();

        //Report.info("new parameter access, parName: " + node.name +", offset: " + offset + " depth: " + fs.depth);
        if (offset != -1)
            Frames.accesses.put(node, new RelAccess(size, offset, fs.depth+1));
        return size;
    }

    // visArg = offset in this case as we do not need to track it globally
    public Object visit(AbsCompDecl node, Long offset) {
        long size = SemAn.descType().get(node.type).size();
        node.type.accept(this, null);
        FrameSize fs = stack.peek();

        //Report.info("new component access, offset: " + offset + " depth: " + fs.depth);
        Frames.accesses.put(node, new RelAccess(size, offset, 0));
        return size;
    }


    private class FrameSize {
        private long locsSize;
        private long argsSize;
        private long offset;
        private int depth;

        private FrameSize(int depth) {
            this.depth = depth;
        }
    }

}
