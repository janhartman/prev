package compiler.phases.lincode;

import java.util.*;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;
import compiler.phases.imcgen.code.*;

public class Fragmenter extends AbsFullVisitor<Object, Object> {

	public Vector<ImcStmt> globStmts;
	public ImcExpr globExpr;

	public Fragmenter() {
		this.globStmts = new Vector<>();
		this.globExpr = null;
	}

	/**
	 * 	expressions
	 */

	public Object visit(AbsArgs node, Object visArg) {
		for (AbsExpr expr : node.args())
			expr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsArrExpr node, Object visArg) {
		node.array.accept(this, visArg);
		node.index.accept(this, visArg);
		return null;
	}

	public Object visit(AbsAtomExpr node, Object visArg) {
		return null;
	}

	public Object visit(AbsBinExpr node, Object visArg) {
		if (globExpr == null) {
			globExpr = ImcGen.exprImCode.get(node);
		}

		node.fstExpr.accept(this, visArg);
		node.sndExpr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsCastExpr node, Object visArg) {
		node.type.accept(this, visArg);
		node.expr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsDelExpr node, Object visArg) {
		node.expr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsFunName node, Object visArg) {
		node.args.accept(this, visArg);
		return null;
	}

	public Object visit(AbsNewExpr node, Object visArg) {
		node.type.accept(this, visArg);
		return null;
	}

	public Object visit(AbsRecExpr node, Object visArg) {
		node.record.accept(this, visArg);
		node.comp.accept(this, visArg);
		return null;
	}

	public Object visit(AbsStmtExpr node, Object visArg) {
		if (globExpr == null) {
			globExpr = ImcGen.exprImCode.get(node.expr);
		}

		node.decls.accept(this, visArg);
		node.stmts.accept(this, visArg);
		node.expr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsUnExpr node, Object visArg) {
		if (globExpr == null) {
			globExpr = ImcGen.exprImCode.get(node);
			globStmts.add(new ImcESTMT(new ImcCONST(0)));
		}

		node.subExpr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsVarName node, Object visArg) {
		return null;
	}


	/**
		statements
	 */


	public Object visit(AbsAssignStmt node, Object visArg) {
		node.dst.accept(this, visArg);
		node.src.accept(this, visArg);
		return null;
	}

	public Object visit(AbsExprStmt node, Object visArg) {
		node.expr.accept(this, visArg);
		return null;
	}

	public Object visit(AbsIfStmt node, Object visArg) {
		node.cond.accept(this, visArg);
		node.thenBody.accept(this, visArg);
		node.elseBody.accept(this, visArg);
		return null;
	}

	public Object visit(AbsStmts node, Object visArg) {
		for (AbsStmt stmt : node.stmts())
			stmt.accept(this, visArg);
		return null;
	}

	public Object visit(AbsWhileStmt node, Object visArg) {
		node.cond.accept(this, visArg);
		node.body.accept(this, visArg);
		return null;
	}


	/**
		declarations
	 */
	public Object visit(AbsCompDecl node, Object visArg) {
		node.type.accept(this, visArg);
		return null;
	}

	public Object visit(AbsCompDecls node, Object visArg) {
		for (AbsCompDecl compDecl : node.compDecls())
			compDecl.accept(this, visArg);
		return null;
	}

	public Object visit(AbsDecls node, Object visArg) {
		for (AbsDecl decl : node.decls())
			decl.accept(this, visArg);
		return null;
	}

	public Object visit(AbsFunDef node, Object visArg) {
		Frame frame = Frames.frames.get(node);
		Temp RV = new Temp();
		Label begLabel = new Label();
		Label endLabel = new Label();
		ImcExpr value = ImcGen.exprImCode.get(node.value);
		ImcStmt stmt = new ImcMOVE(new ImcTEMP(RV), value);
		{
			Vector<ImcStmt> canStmts = new Vector<ImcStmt>();
			canStmts.add(new ImcLABEL(begLabel));

			canStmts.add(stmt);

			canStmts.add(new ImcJUMP(endLabel));
			canStmts.add(new ImcLABEL(endLabel));
			CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
			LinCode.add(fragment);
		}
		node.value.accept(this, null);
		return null;
	}

	public Object visit(AbsParDecl node, Object visArg) {
		node.type.accept(this, visArg);
		return null;
	}

	public Object visit(AbsParDecls node, Object visArg) {
		for (AbsParDecl parDecl : node.parDecls())
			parDecl.accept(this, visArg);
		return null;
	}

	public Object visit(AbsVarDecl node, Object visArg) {
		Access access = Frames.accesses.get(node);
		if (access instanceof AbsAccess) {
			AbsAccess absAccess = (AbsAccess)access;
			DataFragment fragment = new DataFragment(absAccess.label, absAccess.size);
			LinCode.add(fragment);
		}
		return null;
	}

}
