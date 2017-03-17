package compiler.phases.abstr.abstree;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;

public class AbsCompDecls extends AbsTree {

	private final Vector<AbsCompDecl> compDecls;

	public AbsCompDecls(Locatable location, Vector<AbsCompDecl> compDecls) {
		super(location);
		this.compDecls = new Vector<AbsCompDecl>(compDecls);
	}

	public Vector<AbsCompDecl> compDecls() {
		return new Vector<AbsCompDecl>(compDecls);
	}

	public AbsCompDecl compDecl(int index) {
		return compDecls.elementAt(index);
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
