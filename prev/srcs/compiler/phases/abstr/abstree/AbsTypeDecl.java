package compiler.phases.abstr.abstree;

import common.report.Locatable;
import compiler.phases.abstr.AbsVisitor;

public class AbsTypeDecl extends AbsDecl {

    public AbsTypeDecl(Locatable location, String name, AbsType type) {
        super(location, name, type);
    }

    @Override
    public <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg) {
        return visitor.visit(this, accArg);
    }

}
