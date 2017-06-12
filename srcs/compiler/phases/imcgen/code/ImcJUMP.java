package compiler.phases.imcgen.code;

import compiler.phases.frames.Label;
import compiler.phases.imcgen.ImcVisitor;

public class ImcJUMP extends ImcStmt {

    public Label label;

    public ImcJUMP(Label label) {
        this.label = label;
    }

    @Override
    public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
