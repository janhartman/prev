package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

// ADDED CLASS - ONLY FOR INTERNAL USAGE
public class AbsExprDecl extends AbsExpr {

    public final AbsDecls decls;

    public final AbsExpr expr;

    public AbsExprDecl(Locatable location, AbsDecls decls, AbsExpr expr) {
        super(location);
        this.decls = decls;
        this.expr = expr;
    }

    public AbsExpr relocate(Locatable location) {
        return new AbsExprDecl(location, decls, expr);
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
