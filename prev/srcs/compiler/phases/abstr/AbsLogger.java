package compiler.phases.abstr;

import java.util.*;
import common.logger.*;
import compiler.phases.abstr.abstree.*;

/**
 * The visitor that produces the log of the abstract syntax tree.
 * 
 * @author sliva
 *
 */
public class AbsLogger implements AbsVisitor<Object, Object> {

	/** A list of subvisitors for logging results of the subsequent phases. */
	private final LinkedList<AbsVisitor<Object, Object>> subvisitors;

	/** The logger the log should be written to. */
	private final Logger logger;

	/**
	 * Construct a new visitor with a logger the log should be written to.
	 * 
	 * @param logger
	 *            The logger the log should be written to.
	 */
	public AbsLogger(Logger logger) {
		this.logger = logger;
		this.subvisitors = new LinkedList<AbsVisitor<Object, Object>>();
	}

	/**
	 * Adds a new subvisitor to this visitor.
	 * 
	 * @param subvisitor
	 *            The subvisitor.
	 * @return This visitor.
	 */
	public AbsVisitor<Object, Object> addSubvisitor(AbsVisitor<Object, Object> subvisitor) {
		subvisitors.addLast(subvisitor);
		return this;
	}

	@Override
	public Object visit(AbsArgs args, Object visArg) {
		if (logger == null)
			return null;
		if (!args.args().isEmpty()) {
			logger.begElement("node");
			logger.addAttribute("label", "Args");
			for (AbsExpr arg : args.args()) {
				arg.accept(this, null);
			}
			args.location.log(logger);
			for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
				args.accept(subvisitor, null);
			}
			logger.endElement();
		}
		return null;
	}

	@Override
	public Object visit(AbsArrExpr arrExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "ArrExpr");
		arrExpr.array.accept(this, visArg);
		arrExpr.index.accept(this, visArg);
		arrExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			arrExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsArrType arrType, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "ArrType");
		arrType.len.accept(this, null);
		arrType.elemType.accept(this, null);
		arrType.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			arrType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsAssignStmt assignStmt, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "AssignStmt");
		assignStmt.dst.accept(this, null);
		assignStmt.src.accept(this, null);
		assignStmt.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			assignStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsAtomExpr atomExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "AtomExpr");
		logger.addAttribute("spec", atomExpr.type.toString());
		logger.addAttribute("lexeme", atomExpr.expr);
		atomExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			atomExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsAtomType atomType, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "AtomType");
		logger.addAttribute("spec", atomType.type.toString());
		atomType.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			atomType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsBinExpr binExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "BinExpr");
		logger.addAttribute("spec", binExpr.oper.toString());
		binExpr.fstExpr.accept(this, null);
		binExpr.sndExpr.accept(this, null);
		binExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			binExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsCastExpr castExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "CastExpr");
		castExpr.type.accept(this, null);
		castExpr.expr.accept(this, null);
		castExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			castExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsCompDecl compDecl, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "CompDecl");
		logger.addAttribute("lexeme", compDecl.name);
		compDecl.type.accept(this, visArg);
		compDecl.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			compDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsCompDecls compDecls, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "CompDecls");
		for (AbsCompDecl compDecl : compDecls.compDecls())
			compDecl.accept(this, visArg);
		compDecls.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			compDecls.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsDecls decls, Object visArg) {
		if (logger == null)
			return null;
		if (!decls.decls().isEmpty()) {
			logger.begElement("node");
			logger.addAttribute("label", "Decls");
			for (AbsDecl decl : decls.decls()) {
				decl.accept(this, null);
			}
			decls.location.log(logger);
			for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
				decls.accept(subvisitor, null);
			}
			logger.endElement();
		}
		return null;
	}

	@Override
	public Object visit(AbsDelExpr delExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "DelExpr");
		delExpr.expr.accept(this, null);
		delExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			delExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsExprStmt exprStmt, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "ExprStmt");
		exprStmt.expr.accept(this, null);
		exprStmt.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			exprStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsFunDecl funDecl, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "FunDecl");
		logger.addAttribute("lexeme", funDecl.name);
		funDecl.parDecls.accept(this, null);
		funDecl.type.accept(this, null);
		funDecl.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			funDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsFunDef funDef, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "FunDef");
		logger.addAttribute("lexeme", funDef.name);
		funDef.parDecls.accept(this, null);
		funDef.type.accept(this, null);
		funDef.value.accept(this, null);
		funDef.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			funDef.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsFunName funName, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "FunName");
		logger.addAttribute("lexeme", funName.name);
		funName.args.accept(this, null);
		funName.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			funName.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsIfStmt ifStmt, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "IfStmt");
		ifStmt.cond.accept(this, null);
		ifStmt.thenBody.accept(this, null);
		ifStmt.elseBody.accept(this, null);
		ifStmt.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			ifStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsNewExpr newExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "NewExpr");
		newExpr.type.accept(this, null);
		newExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			newExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsParDecl parDecl, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "ParDecl");
		logger.addAttribute("lexeme", parDecl.name);
		parDecl.type.accept(this, null);
		parDecl.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			parDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsParDecls parDecls, Object visArg) {
		if (logger == null)
			return null;
		if (!parDecls.parDecls().isEmpty()) {
			logger.begElement("node");
			logger.addAttribute("label", "ParDecls");
			for (AbsParDecl parDecl : parDecls.parDecls()) {
				parDecl.accept(this, null);
			}
			parDecls.location.log(logger);
			for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
				parDecls.accept(subvisitor, null);
			}
			logger.endElement();
		}
		return null;
	}

	@Override
	public Object visit(AbsPtrType ptrType, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "PtrType");
		ptrType.subType.accept(this, null);
		ptrType.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			ptrType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsRecExpr recExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "RecExpr");
		recExpr.record.accept(this, visArg);
		recExpr.comp.accept(this, visArg);
		recExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			recExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsRecType recType, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "RecType");
		recType.compDecls.accept(this, visArg);
		recType.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			recType.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsStmtExpr stmtExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "StmtExpr");
		stmtExpr.decls.accept(this, null);
		stmtExpr.stmts.accept(this, null);
		stmtExpr.expr.accept(this, null);
		stmtExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			stmtExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsStmts stmts, Object visArg) {
		if (logger == null)
			return null;
		if (!stmts.stmts().isEmpty()) {
			logger.begElement("node");
			logger.addAttribute("label", "Stmts");
			for (AbsStmt stmt : stmts.stmts()) {
				stmt.accept(this, null);
			}
			stmts.location.log(logger);
			for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
				stmts.accept(subvisitor, null);
			}
			logger.endElement();
		}
		return null;
	}

	@Override
	public Object visit(AbsTypeDecl typeDecl, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "TypeDecl");
		logger.addAttribute("lexeme", typeDecl.name);
		typeDecl.type.accept(this, null);
		typeDecl.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			typeDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsTypeName typeName, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "TypeName");
		logger.addAttribute("lexeme", typeName.name);
		typeName.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			typeName.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsUnExpr unExpr, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "UnExpr");
		logger.addAttribute("spec", unExpr.oper.toString());
		unExpr.subExpr.accept(this, null);
		unExpr.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			unExpr.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsVarDecl varDecl, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "VarDecl");
		logger.addAttribute("lexeme", varDecl.name);
		varDecl.type.accept(this, null);
		varDecl.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			varDecl.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsVarName varName, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "VarName");
		logger.addAttribute("lexeme", varName.name);
		varName.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			varName.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(AbsWhileStmt whileStmt, Object visArg) {
		if (logger == null)
			return null;
		logger.begElement("node");
		logger.addAttribute("label", "WhileStmt");
		whileStmt.cond.accept(this, null);
		whileStmt.body.accept(this, null);
		whileStmt.location.log(logger);
		for (AbsVisitor<Object, Object> subvisitor : subvisitors) {
			whileStmt.accept(subvisitor, null);
		}
		logger.endElement();
		return null;
	}

}
