package compiler.phases.imcgen.code;

import java.util.*;

import compiler.phases.frames.*;
import compiler.phases.imcgen.*;

public class ImcCALL extends ImcExpr {

	public final Label label;
	
	private final Vector<ImcExpr> args;
	
	public ImcCALL(Label label, Vector<ImcExpr> args) {
		this.label = label;
		this.args = new Vector<ImcExpr>(args);
	}
	
	public Vector<ImcExpr> args() {
		return new Vector<ImcExpr>(args);
	}
	
	@Override
	public <Result, Arg> Result accept(ImcVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
