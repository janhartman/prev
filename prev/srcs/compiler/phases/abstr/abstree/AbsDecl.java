package compiler.phases.abstr.abstree;

import common.report.*;

public abstract class AbsDecl extends AbsTree {

	public final String name;
	
	public final AbsType type;

	public AbsDecl(Locatable location, String name, AbsType type) {
		super(location);
		this.name = name;
		this.type = type;
	}

}
