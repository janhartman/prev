package compiler.phases.imcgen;

import common.logger.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.imcgen.code.*;

public class ImcGenLogger implements AbsVisitor<Object, Object> {

	/** The logger the log should be written to. */
	private final Logger logger;

	private ImcLogger visitor = new ImcLogger();

	/**
	 * Construct a new visitor with a logger the log should be written to.
	 * 
	 * @param logger
	 *            The logger the log should be written to.
	 */
	public ImcGenLogger(Logger logger) {
		this.logger = logger;
	}

	@Override
	public Object visit(AbsArgs args, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsArrExpr arrExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(arrExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsArrType arrType, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsAssignStmt assignStmt, Object visArg) {
		ImcStmt imc = ImcGen.stmtImCode.get(assignStmt);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsAtomExpr atomExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(atomExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsAtomType atomType, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsBinExpr binExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(binExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsCastExpr castExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(castExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsCompDecl compDecl, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsCompDecls compDecls, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsDecls decls, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsDelExpr delExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(delExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsExprStmt exprStmt, Object visArg) {
		ImcStmt imc = ImcGen.stmtImCode.get(exprStmt);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsFunDecl funDecl, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsFunDef funDef, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsFunName funName, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(funName);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsIfStmt ifStmt, Object visArg) {
		ImcStmt imc = ImcGen.stmtImCode.get(ifStmt);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsNewExpr newExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(newExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsParDecl parDecl, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsParDecls parDecls, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsPtrType ptrType, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsRecExpr recExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(recExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsRecType recType, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsStmtExpr stmtExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(stmtExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsStmts stmts, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsTypeDecl typDecl, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsTypeName typeName, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsUnExpr unExpr, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(unExpr);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsVarDecl varDecl, Object visArg) {
		return null;
	}

	@Override
	public Object visit(AbsVarName varName, Object visArg) {
		ImcExpr imc = ImcGen.exprImCode.get(varName);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

	@Override
	public Object visit(AbsWhileStmt whileStmt, Object visArg) {
		ImcStmt imc = ImcGen.stmtImCode.get(whileStmt);
		if (imc != null)
			imc.accept(visitor, logger);
		return null;
	}

}
