package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public class AbsAtomType extends AbsType {

	public enum Type {
		VOID, BOOL, CHAR, INT,
	}

	public final Type type;

	public AbsAtomType(Locatable location, Type type) {
		super(location);
		this.type = type;
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
