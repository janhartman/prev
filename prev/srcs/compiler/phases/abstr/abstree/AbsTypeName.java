package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsTypeName extends AbsType implements AbsName {

	public final String name;

	public AbsTypeName(Locatable location, String name) {
		super(location);
		this.name = name;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
