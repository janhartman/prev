package compiler.phases.imcgen.code;

import compiler.phases.imcgen.ImcVisitor;

import java.util.Vector;

public class ImcSTMTS extends ImcStmt {

    private final Vector<ImcStmt> stmts;

    public ImcSTMTS(Vector<ImcStmt> stmts) {
        this.stmts = new Vector<ImcStmt>(stmts);
    }

    public Vector<ImcStmt> stmts() {
        return new Vector<ImcStmt>(stmts);
    }

    @Override
    public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
