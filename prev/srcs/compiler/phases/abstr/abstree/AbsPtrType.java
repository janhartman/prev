package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsPtrType extends AbsType {

	public final AbsType subType;

	public AbsPtrType(Locatable location, AbsType subType) {
		super(location);
		this.subType = subType;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
