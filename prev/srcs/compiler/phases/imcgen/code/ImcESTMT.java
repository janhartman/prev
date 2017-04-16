package compiler.phases.imcgen.code;

import compiler.phases.imcgen.*;

public class ImcESTMT extends ImcStmt {

	public final ImcExpr expr;

	public ImcESTMT(ImcExpr expr) {
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
