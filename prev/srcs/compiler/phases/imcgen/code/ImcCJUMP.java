package compiler.phases.imcgen.code;

import compiler.phases.frames.*;
import compiler.phases.imcgen.*;

public class ImcCJUMP extends ImcStmt {

	public ImcExpr cond;

	public Label posLabel;
	
	public Label negLabel;
	
	public ImcCJUMP(ImcExpr cond, Label posLabel, Label negLabel) {
		this.cond = cond;
		this.posLabel = posLabel;
		this.negLabel = negLabel;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
