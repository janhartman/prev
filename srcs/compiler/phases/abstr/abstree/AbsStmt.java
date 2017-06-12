package compiler.phases.abstr.abstree;

import common.report.Locatable;

public abstract class AbsStmt extends AbsTree {

    public AbsStmt(Locatable location) {
        super(location);
    }

}
