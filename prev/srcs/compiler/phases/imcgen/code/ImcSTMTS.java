package compiler.phases.imcgen.code;

import java.util.*;

import compiler.phases.imcgen.*;

public class ImcSTMTS extends ImcStmt {

	private final Vector<ImcStmt> stmts;

	public ImcSTMTS(Vector<ImcStmt> stmts) {
		this.stmts = new Vector<ImcStmt>(stmts);
	}

	public Vector<ImcStmt> stmts() {
		return new Vector<ImcStmt>(stmts);
	}
	
	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
