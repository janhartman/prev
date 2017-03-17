package compiler.phases.abstr.abstree;

import common.report.*;
import compiler.phases.abstr.*;

public abstract class AbsTree implements Locatable {

	public final Location location;

	public AbsTree(Locatable location) {
		this.location = location == null ? null : location.location();
	}

	public Location location() {
		return location;
	}
	
	public abstract <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg);

}
