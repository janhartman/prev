package compiler.phases.abstr.abstree;

import common.report.*;

public abstract class AbsExpr extends AbsTree {

	public AbsExpr(Locatable location) {
		super(location);
	}
	
	public abstract AbsExpr relocate(Locatable location);

}
