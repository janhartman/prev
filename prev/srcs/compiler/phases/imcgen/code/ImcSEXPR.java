package compiler.phases.imcgen.code;

import compiler.phases.imcgen.*;

public class ImcSEXPR extends ImcExpr {

	public final ImcStmt stmt;

	public final ImcExpr expr;

	public ImcSEXPR(ImcStmt stmt, ImcExpr expr) {
		this.stmt = stmt;
		this.expr = expr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
