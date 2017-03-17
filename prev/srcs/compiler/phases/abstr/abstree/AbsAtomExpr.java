package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsAtomExpr extends AbsExpr {

	public enum Type {
		VOID, BOOL, CHAR, INT, PTR,
	}

	public final Type type;

	public final String expr;

	public AbsAtomExpr(Locatable location, Type type, String expr) {
		super(location);
		this.type = type;
		this.expr = expr;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsAtomExpr(location, type, expr);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
