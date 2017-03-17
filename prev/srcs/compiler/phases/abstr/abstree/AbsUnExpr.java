package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsUnExpr extends AbsExpr {

	public enum Oper {
		NOT, ADD, SUB, MEM, VAL,
	}

	public final Oper oper;

	public final AbsExpr subExpr;

	public AbsUnExpr(Locatable location, Oper oper, AbsExpr subExpr) {
		super(location);
		this.oper = oper;
		this.subExpr = subExpr;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsUnExpr(location, oper, subExpr);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
