package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

public class AbsAssignStmt extends AbsStmt {

    public final AbsExpr dst;

    public final AbsExpr src;

    public AbsAssignStmt(Locatable location, AbsExpr dst, AbsExpr src) {
        super(location);
        this.dst = dst;
        this.src = src;
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
