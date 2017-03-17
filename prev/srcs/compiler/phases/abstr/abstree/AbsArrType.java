package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsArrType extends AbsType {

	public final AbsExpr len;

	public final AbsType elemType;

	public AbsArrType(Locatable location, AbsExpr len, AbsType elemType) {
		super(location);
		this.len = len;
		this.elemType = elemType;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
