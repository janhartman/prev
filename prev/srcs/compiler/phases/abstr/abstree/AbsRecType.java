package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsRecType extends AbsType {

	public final AbsCompDecls compDecls;

	public AbsRecType(Locatable location, AbsCompDecls compDecls) {
		super(location);
		this.compDecls = compDecls;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}
	
}
