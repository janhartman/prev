package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsFunDecl extends AbsDecl {
		
	public final AbsParDecls parDecls;
	
	public AbsFunDecl(Locatable location, String name, AbsParDecls parDecls, AbsType type) {
		super(location, name, type);
		this.parDecls = parDecls;
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
