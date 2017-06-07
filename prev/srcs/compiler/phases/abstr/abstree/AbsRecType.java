package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

public class AbsRecType extends AbsType {

    public final AbsCompDecls compDecls;

    public AbsRecType(Locatable location, AbsCompDecls compDecls) {
        super(location);
        this.compDecls = compDecls;
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
