package compiler.phases.imcgen.code;

import compiler.phases.imcgen.*;

public class ImcMEM extends ImcExpr {

	public final ImcExpr addr;

	public ImcMEM(ImcExpr addr) {
		this.addr = addr;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
