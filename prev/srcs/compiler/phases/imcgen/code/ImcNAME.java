package compiler.phases.imcgen.code;

import compiler.phases.frames.*;
import compiler.phases.imcgen.*;

public class ImcNAME extends ImcExpr {

	public final Label label;

	public ImcNAME(Label label) {
		this.label = label;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
