package compiler.phases.frames;

import compiler.phases.Phase;
import compiler.phases.abstr.AbsAttribute;
import compiler.phases.abstr.AbsLogger;
import compiler.phases.abstr.Abstr;
import compiler.phases.abstr.abstree.AbsFunDef;
import compiler.phases.abstr.abstree.AbsVarDecl;
import compiler.phases.seman.SemLogger;

import java.util.LinkedList;

/**
 * Computing frames and accesses.
 *
 * @author sliva
 */
public class Frames extends Phase {

    public static final AbsAttribute<AbsFunDef, Frame> frames = new AbsAttribute<AbsFunDef, Frame>();

    public static final AbsAttribute<AbsVarDecl, Access> accesses = new AbsAttribute<AbsVarDecl, Access>();

    public static final LinkedList<Temp> allTemps = new LinkedList<>();

    /**
     * Constructs a new phase for computing frames and accesses.
     */
    public Frames() {
        super("frames");
    }

    @Override
    public void close() {
        frames.lock();
        accesses.lock();
        Abstr.absTree().accept(
                new AbsLogger(logger).addSubvisitor(new SemLogger(logger)).addSubvisitor(new FrmLogger(logger)), null);
        super.close();
    }

}
