package compiler.phases.imcgen;

import compiler.phases.Phase;
import compiler.phases.abstr.AbsAttribute;
import compiler.phases.abstr.AbsLogger;
import compiler.phases.abstr.Abstr;
import compiler.phases.abstr.abstree.AbsExpr;
import compiler.phases.abstr.abstree.AbsStmt;
import compiler.phases.frames.FrmLogger;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.code.ImcExpr;
import compiler.phases.imcgen.code.ImcStmt;
import compiler.phases.seman.SemLogger;

/**
 * Intermediate code generation.
 *
 * @author sliva
 */
public class ImcGen extends Phase {

    /**
     * Intermediate code of expressions.
     */
    public static final AbsAttribute<AbsExpr, ImcExpr> exprImCode = new AbsAttribute<AbsExpr, ImcExpr>();

    /**
     * Intermediate code of statements.
     */
    public static final AbsAttribute<AbsStmt, ImcStmt> stmtImCode = new AbsAttribute<AbsStmt, ImcStmt>();

    public static final Temp FP = new Temp();
    public static final Temp SP = new Temp();

    /**
     * Constructs a new phase for computing frames and accesses.
     */
    public ImcGen() {
        super("imcgen");
    }

    @Override
    public void close() {
        exprImCode.lock();
        stmtImCode.lock();
        Abstr.absTree().accept(new AbsLogger(logger).addSubvisitor(new SemLogger(logger))
                .addSubvisitor(new FrmLogger(logger)).addSubvisitor(new ImcGenLogger(logger)), null);
        super.close();
    }

}
