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
	 * Constructs a new name definer using the specified symbol table.
	 * 
	 * @param symbTable
	 *            The symbol table.
	 */
	public NameDefiner(SymbTable symbTable) {
		this.symbTable = symbTable;
	}

	/**
	 *  just declarations
	 */

	// TODO is it ok to insert type name?
	public Object visit(AbsTypeDecl node, Object visArg) {
		node.type.accept((NameChecker) visArg, null);
		try {
			symbTable.ins(node.name, node);
		}
		catch (SymbTable.CannotInsNameException cine) {
			throw new Report.Error(node.location(), "Type with name " + node.name +" already declared");
		}
		return null;
	}


	public Object visit(AbsVarDecl node, Object visArg) {
		node.type.accept((NameChecker) visArg, null);
		try {
			symbTable.ins(node.name, node);
		}
		catch (SymbTable.CannotInsNameException cine) {
			throw new Report.Error(node.location(), "Variable with name " + node.name +" already declared");
		}
		return null;
	}


	public Object visit(AbsFunDecl node, Object visArg) {
		try {
			symbTable.ins(node.name, node);
		}
		catch (SymbTable.CannotInsNameException cine) {
			throw new Report.Error(node.location(), "Function with name " + node.name +" already declared");
		}

		node.type.accept((NameChecker) visArg, null);

		for (AbsParDecl parDecl : node.parDecls.parDecls()) {
			parDecl.type.accept((NameChecker) visArg, null);
		}

		return null;
	}


	public Object visit(AbsFunDef node, Object visArg) {
		try {
			symbTable.ins(node.name, node);
		}
		catch (SymbTable.CannotInsNameException cine) {
			throw new Report.Error(node.location(), "Function with name " + node.name +" already declared");
		}

		node.type.accept((NameChecker) visArg, null);

		for (AbsParDecl parDecl : node.parDecls.parDecls()) {
			parDecl.type.accept((NameChecker) visArg, null);
		}

		symbTable.newScope();

		node.value.accept((NameChecker) visArg, null);

		for (AbsParDecl parDecl : node.parDecls.parDecls()) {
			try {
				symbTable.ins(parDecl.name, parDecl);
			}
			catch (SymbTable.CannotInsNameException cine) {
				throw new Report.Error(node.location(), "Parameter with name " + node.name +" already declared");
			}
		}

		symbTable.oldScope();

		return null;
	}


	// TODO are these needed?
	// TODO how to deal with components
	public Object visit(AbsCompDecl node, Object visArg) {
		node.type.accept((NameChecker) visArg, null);
		return null;
	}

	public Object visit(AbsCompDecls node, Object visArg) {
		for (AbsCompDecl compDecl : node.compDecls()) {
			compDecl.accept(this, null);
		}
		return null;
	}

	public Object visit(AbsDecls node, Object visArg) {
		for (AbsDecl decl: node.decls()) {
			decl.accept(this, null);
		}
		return null;
	}

	public Object visit(AbsParDecl node, Object visArg) {
		return null;
	}

	public Object visit(AbsParDecls node, Object visArg) {
		return null;
	}

}
