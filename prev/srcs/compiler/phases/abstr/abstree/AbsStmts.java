package compiler.phases.abstr.abstree;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;

public class AbsStmts extends AbsTree {

	private final Vector<AbsStmt> stmts;

	public AbsStmts(Locatable location, Vector<AbsStmt> stmts) {
		super(location);
		this.stmts = new Vector<AbsStmt>(stmts);
	}

	public Vector<AbsStmt> stmts() {
		return stmts;
	}
	
	public AbsStmt stmt(int index) {
		return stmts.elementAt(index);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
