package compiler.phases.imcgen.code;

import compiler.phases.frames.Label;
import compiler.phases.imcgen.ImcVisitor;

public class ImcLABEL extends ImcStmt {

    public Label label;

    public ImcLABEL(Label label) {
        this.label = label;
    }

    @Override
    public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
