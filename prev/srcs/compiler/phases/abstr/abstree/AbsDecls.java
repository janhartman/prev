package compiler.phases.abstr.abstree;

import java.util.*;
import common.report.*;
import compiler.phases.abstr.*;

public class AbsDecls extends AbsTree {

	private final Vector<AbsDecl> decls;

	public AbsDecls(Locatable location, Vector<AbsDecl> decls) {
		super(location);
		this.decls = new Vector<AbsDecl>(decls);
	}

	public Vector<AbsDecl> decls() {
		return decls;
	}
	
	public AbsDecl decl(int index) {
		return decls.elementAt(index);
	}

	@Override
	public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
