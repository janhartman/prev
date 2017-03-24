package compiler.phases.seman;

import compiler.phases.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.seman.type.*;

/**
 * Semantic analysis.
 * 
 * @author sliva
 *
 */
public class SemAn extends Phase {

	/** The attribute linking the usage of a name to its declaration. */
	private static final AbsAttribute<AbsName, AbsDecl> declAt = new AbsAttribute<AbsName, AbsDecl>();

	private static final AbsAttribute<AbsTypeDecl, SemType> declType = new AbsAttribute<AbsTypeDecl, SemType>();

	private static final AbsAttribute<AbsType, SemType> descType = new AbsAttribute<AbsType, SemType>();

	private static final AbsAttribute<AbsExpr, SemType> isOfType = new AbsAttribute<AbsExpr, SemType>();

	private static final AbsAttribute<AbsRecType, SymbTable> recSymbTable = new AbsAttribute<AbsRecType, SymbTable>();

	public static AbsAttribute<AbsName, AbsDecl> declAt() {
		return declAt;
	}

	public static AbsAttribute<AbsTypeDecl, SemType> declType() {
		return declType;
	}

	public static AbsAttribute<AbsType, SemType> descType() {
		return descType;
	}

	public static AbsAttribute<AbsExpr, SemType> isOfType() {
		return isOfType;
	}

	public static AbsAttribute<AbsRecType, SymbTable> recSymbTable() {
		return recSymbTable;
	}

	/**
	 * Constructs a new semantic analysis phase.
	 */
	public SemAn() {
		super("seman");
	}

	@Override
	public void close() {
		declAt.lock();
		declType.lock();
		descType.lock();
		isOfType.lock();
		recSymbTable.lock();
		Abstr.absTree().accept(new AbsLogger(logger).addSubvisitor(new SemLogger(logger)), null);
		super.close();
	}

}
