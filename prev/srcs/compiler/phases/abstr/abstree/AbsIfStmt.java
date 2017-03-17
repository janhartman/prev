package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsIfStmt extends AbsStmt {

	public final AbsExpr cond;

	public final AbsStmts thenBody;

	public final AbsStmts elseBody;

	public AbsIfStmt(Locatable location, AbsExpr cond, AbsStmts thenBody, AbsStmts elseBody) {
		super(location);
		this.cond = cond;
		this.thenBody = thenBody;
		this.elseBody = elseBody;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
