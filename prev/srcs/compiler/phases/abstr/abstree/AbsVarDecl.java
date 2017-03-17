package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsVarDecl extends AbsDecl {
	
	public AbsVarDecl(Locatable location, String name, AbsType type) {
		super(location, name, type);
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
