package compiler.phases.frames;

import common.logger.Logger;
import compiler.phases.abstr.AbsNullVisitor;
import compiler.phases.abstr.abstree.AbsCompDecl;
import compiler.phases.abstr.abstree.AbsFunDef;
import compiler.phases.abstr.abstree.AbsParDecl;
import compiler.phases.abstr.abstree.AbsVarDecl;

public class FrmLogger extends AbsNullVisitor<Object, Object> {

    /**
     * The logger the log should be written to.
     */
    private final Logger logger;

    /**
     * Construct a new visitor with a logger the log should be written to.
     *
     * @param logger The logger the log should be written to.
     */
    public FrmLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Object visit(AbsCompDecl compDecl, Object visArg) {
        Access access = Frames.accesses.get(compDecl);
        if (access != null)
            access.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsFunDef funDef, Object visArg) {
        Frame frame = Frames.frames.get(funDef);
        if (frame != null)
            frame.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsParDecl parDecl, Object visArg) {
        Access access = Frames.accesses.get(parDecl);
        if (access != null)
            access.log(logger);
        return null;
    }

    @Override
    public Object visit(AbsVarDecl varDecl, Object visArg) {
        Access access = Frames.accesses.get(varDecl);
        if (access != null)
            access.log(logger);
        return null;
    }

}
