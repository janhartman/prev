package compiler.phases.imcgen.code;

import compiler.phases.frames.Label;
import compiler.phases.imcgen.ImcVisitor;

import java.util.Vector;

public class ImcCALL extends ImcExpr {

    public final Label label;

    private final Vector<ImcExpr> args;

    public ImcCALL(Label label, Vector<ImcExpr> args) {
        this.label = label;
        this.args = new Vector<ImcExpr>(args);
    }

    public Vector<ImcExpr> args() {
        return new Vector<ImcExpr>(args);
    }

    @Override
    public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
