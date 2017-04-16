package compiler.phases.imcgen.code;

import compiler.phases.frames.*;
import compiler.phases.imcgen.*;

public class ImcTEMP extends ImcExpr {

	public final Temp temp;

	public ImcTEMP(Temp temp) {
		this.temp = temp;
	}

	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}
	
}
