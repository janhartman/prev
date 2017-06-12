package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

public class AbsExprStmt extends AbsStmt {

    public final AbsExpr expr;

    public AbsExprStmt(Locatable location, AbsExpr expr) {
        super(location);
        this.expr = expr;
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
