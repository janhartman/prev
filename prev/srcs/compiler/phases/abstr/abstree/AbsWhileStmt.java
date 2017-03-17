package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsWhileStmt extends AbsStmt {

	public final AbsExpr cond;

	public final AbsStmts body;

	public AbsWhileStmt(Locatable location, AbsExpr cond, AbsStmts body) {
		super(location);
		this.cond = cond;
		this.body = body;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
