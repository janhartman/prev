package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsVarName extends AbsExpr implements AbsName {

	public final String name;

	public AbsVarName(Locatable location, String name) {
		super(location);
		this.name = name;
	}
	
	public AbsExpr relocate(Locatable location) {
		return new AbsVarName(location, name);
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
