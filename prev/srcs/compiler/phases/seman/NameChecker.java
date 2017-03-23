package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that traverses (a part of) the AST and checks if all names used are
 * visible where they are used. This visitor uses another visitor, namely
 * {@link NameDefiner}, whenever a declaration is encountered during the AST
 * traversal.
 * 
 * @author sliva
 *
 */
public class NameChecker implements AbsVisitor<Object, Object> {

	/** The symbol table. */
	private final SymbTable symbTable;

	/**
	 * Constructs a new name checker using the specified symbol table.
	 * 
	 * @param symbTable
	 *            The symbol table.
	 */
	public NameChecker(SymbTable symbTable) {
		this.symbTable = symbTable;
	}

	// TODO

}
