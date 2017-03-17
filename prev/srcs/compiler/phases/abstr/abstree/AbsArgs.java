package compiler.phases.abstr.abstree;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;

public class AbsArgs extends AbsTree {

	private final Vector<AbsExpr> args;

	public AbsArgs(Locatable location, Vector<AbsExpr> args) {
		super(location);
		this.args = new Vector<AbsExpr>(args);
	}

	public Vector<AbsExpr> args() {
		return new Vector<AbsExpr>(args);
	}

	public AbsExpr arg(int index) {
		return args.elementAt(index);
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
