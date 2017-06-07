package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

import java.util.Vector;

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
