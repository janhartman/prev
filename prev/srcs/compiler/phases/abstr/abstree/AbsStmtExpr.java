package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsStmtExpr extends AbsExpr {

	public final AbsDecls decls;

	public final AbsStmts stmts;

	public final AbsExpr expr;

	public AbsStmtExpr(Locatable location, AbsDecls decls, AbsStmts stmts, AbsExpr expr) {
		super(location);
		this.decls = decls;
		this.stmts = stmts;
		this.expr = expr;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsStmtExpr(location, decls, stmts, expr);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
