package compiler.phases.abstr.abstree;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;

public class AbsParDecls extends AbsTree {

	private final Vector<AbsParDecl> parDecls;

	public AbsParDecls(Locatable location, Vector<AbsParDecl> parDecls) {
		super(location);
		this.parDecls = new Vector<AbsParDecl>(parDecls);
	}

	public Vector<AbsParDecl> parDecls() {
		return new Vector<AbsParDecl>(parDecls);
	}

	public AbsParDecl parDecl(int index) {
		return parDecls.elementAt(index);
	}
	
	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
