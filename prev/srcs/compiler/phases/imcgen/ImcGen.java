package compiler.phases.imcgen;

import compiler.phases.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.code.*;

/**
 * Intermediate code generation.
 * 
 * @author sliva
 *
 */
public class ImcGen extends Phase {

	/** Intermediate code of expressions. */
	public static final AbsAttribute<AbsExpr, ImcExpr> exprImCode = new AbsAttribute<AbsExpr, ImcExpr>();

	/** Intermediate code of statements. */
	public static final AbsAttribute<AbsStmt, ImcStmt> stmtImCode = new AbsAttribute<AbsStmt, ImcStmt>();

	public final Temp FP = new Temp();

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
