package compiler.phases.abstr.abstree;

import common.report.Locatable;
import common.report.Location;
import compiler.phases.abstr.AbsVisitor;

public abstract class AbsTree implements Locatable {

    public final Location location;

    public AbsTree(Locatable location) {
        this.location = location == null ? null : location.location();
    }

    public Location location() {
        return location;
    }

    public abstract <Result, Arg> Result accept(AbsVisitor<Result, Arg> visitor, Arg accArg);

}
