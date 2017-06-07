package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

public class AbsNewExpr extends AbsExpr {

    public final AbsType type;

    public AbsNewExpr(Locatable location, AbsType type) {
        super(location);
        this.type = type;
    }

    public AbsExpr relocate(Locatable location) {
        return new AbsNewExpr(location, type);
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
