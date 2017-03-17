package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsCastExpr extends AbsExpr {

	public final AbsType type;

	public final AbsExpr expr;

	public AbsCastExpr(Locatable location, AbsType type, AbsExpr expr) {
		super(location);
		this.type = type;
		this.expr = expr;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsCastExpr(location, type, expr);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
