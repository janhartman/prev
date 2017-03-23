package compiler.phases.seman;

import common.report.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;

/**
 * A visitor that traverses (a part of) the AST and stores all declarations
 * encountered into the symbol table. It is meant to be called from another
 * visitor, namely {@link NameChecker}.
 * 
 * @author sliva
 *
 */
public class NameDefiner implements AbsVisitor<Object, Object> {

	/** The symbol table. */
	private final SymbTable symbTable;

	/**
	 * Constructs a new name checker using the specified symbol table.
	 * 
	 * @param symbTable
	 *            The symbol table.
	 */
	public NameDefiner(SymbTable symbTable) {
		this.symbTable = symbTable;
	}

	// TODO

}
