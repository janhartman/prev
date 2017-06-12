package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

import java.util.Vector;

public class AbsStmts extends AbsTree {

    private final Vector<AbsStmt> stmts;

    public AbsStmts(Locatable location, Vector<AbsStmt> stmts) {
        super(location);
        this.stmts = new Vector<AbsStmt>(stmts);
    }

    public Vector<AbsStmt> stmts() {
        return stmts;
    }

    public AbsStmt stmt(int index) {
        return stmts.elementAt(index);
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
