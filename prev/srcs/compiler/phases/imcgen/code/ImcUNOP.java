package compiler.phases.imcgen.code;

import compiler.phases.imcgen.*;

public class ImcUNOP extends ImcExpr {
	
	public enum Oper {
		NOT, NEG,
	}

	public final Oper oper;

	public final ImcExpr subExpr;

	public ImcUNOP(Oper oper, ImcExpr subExpr) {
		this.oper = oper;
		this.subExpr = subExpr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
