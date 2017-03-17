package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.*;

public class AbsFunDef extends AbsFunDecl {

	public final AbsExpr value;

	public AbsFunDef(Locatable location, String name, AbsParDecls parDecls, AbsType type, AbsExpr value) {
		super(location, name, parDecls, type);
		this.value = value;
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
