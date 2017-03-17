package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsDelExpr extends AbsExpr {

	public final AbsExpr expr;

	public AbsDelExpr(Locatable location, AbsExpr expr) {
		super(location);
		this.expr = expr;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsDelExpr(location, expr);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
