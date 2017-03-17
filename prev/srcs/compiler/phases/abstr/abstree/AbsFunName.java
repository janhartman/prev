package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsFunName extends AbsVarName implements AbsName {

	public final AbsArgs args;

	public AbsFunName(Locatable location, String name, AbsArgs args) {
		super(location, name);
		this.args = args;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsFunName(location, name, args);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
