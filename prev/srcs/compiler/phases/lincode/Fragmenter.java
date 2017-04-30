package compiler.phases.lincode;

import java.util.*;

import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;
import compiler.phases.imcgen.code.*;

public class Fragmenter extends AbsFullVisitor<Object, Object> {

	private Stack<Vector<ImcStmt>> stack;
	private ImcExpr globExpr;

	public Fragmenter() {
		this.stack = new Stack<>();
		this.globExpr = null;
	}

	/**
	 * 	expressions
	 */

	// TODO check for any function calls
	public Object visit(AbsArgs node, Object visArg) {
		for (AbsExpr expr : node.args())
			expr.accept(this, visArg);
		return null;
	}

	// TODO
	public Object visit(AbsArrExpr node, Object visArg) {
		node.array.accept(this, visArg);
		node.index.accept(this, visArg);
		return null;
	}

	public Object visit(AbsAtomExpr node, Object visArg) {
		return ImcGen.exprImCode.get(node);
	}

	public Object visit(AbsBinExpr node, Object visArg) {
		ImcBINOP origExpr = (ImcBINOP) ImcGen.exprImCode.get(node);

		if (globExpr == null) {
			globExpr = origExpr;
			stack.add(new Vector<>());
		}
		Vector<ImcStmt> stmts = stack.peek();

		ImcExpr fstExpr = (ImcExpr) node.fstExpr.accept(this, visArg);
		ImcTEMP t1 = new ImcTEMP(new Temp());
		stmts.add(new ImcMOVE(t1, fstExpr));

		ImcExpr sndExpr = (ImcExpr) node.sndExpr.accept(this, visArg);
		if (!(origExpr.sndExpr instanceof ImcCONST)) {
			ImcTEMP t2 = new ImcTEMP(new Temp());
			stmts.add(new ImcMOVE(t2, sndExpr));
			sndExpr = t2;
		}

		ImcBINOP newExpr = new ImcBINOP(origExpr.oper, t1, sndExpr);

		if (globExpr.equals(origExpr)) {
			globExpr = newExpr;
			add();
		}

		return newExpr;
	}

	public Object visit(AbsCastExpr node, Object visArg) {
		return node.expr.accept(this, visArg);
	}

	// TODO check for nested function call
	public Object visit(AbsDelExpr node, Object visArg) {
		ImcCALL freeCall = (ImcCALL) ImcGen.exprImCode.get(node);
		ImcMEM mem = (ImcMEM) node.expr.accept(this, visArg);
		//freeCall.args()
		return null;
	}

	// TODO
	public Object visit(AbsFunName node, Object visArg) {
		node.args.accept(this, visArg);
		return ImcGen.exprImCode.get(node);
	}

	// TODO
	public Object visit(AbsNewExpr node, Object visArg) {
		node.type.accept(this, visArg);
		return null;
	}

	// TODO
	public Object visit(AbsRecExpr node, Object visArg) {
		node.record.accept(this, visArg);
		node.comp.accept(this, visArg);
		return null;
	}

	public Object visit(AbsStmtExpr node, Object visArg) {
		ImcExpr origExpr = ImcGen.exprImCode.get(node);

		if (globExpr == null) {
			globExpr = origExpr;
			stack.add(new Vector<>());
		}

		node.decls.accept(this, visArg);
		node.stmts.accept(this, visArg);
		ImcExpr newExpr = (ImcExpr) node.expr.accept(this, visArg);

		if (globExpr.equals(origExpr)) {
			globExpr = newExpr;
			add();
		}
		return newExpr;
	}

	public Object visit(AbsUnExpr node, Object visArg) {
		ImcExpr origExpr = ImcGen.exprImCode.get(node);

		if (globExpr == null) {
			globExpr = origExpr;
			stack.add(new Vector<>());
		}

		ImcExpr subExpr = (ImcExpr) node.subExpr.accept(this, visArg);

		if (subExpr instanceof ImcCALL) {
			ImcTEMP t = new ImcTEMP(new Temp());
			Vector<ImcStmt> stmts = stack.peek();
			stmts.add(new ImcMOVE(t, subExpr));
			subExpr = t;
		}

		ImcExpr newExpr = subExpr;

		switch(node.oper) {
			case NOT:
				newExpr = new ImcUNOP(ImcUNOP.Oper.NOT, subExpr);
				break;
			case SUB:
				newExpr = new ImcUNOP(ImcUNOP.Oper.NEG, subExpr);
				break;

			// TODO fix if necessary - subExpr might be a temporary variable
			case MEM:
				newExpr = ((ImcMEM) subExpr).addr;
				break;
			case VAL:
				newExpr = new ImcMEM(subExpr);
				break;
		}

		if (globExpr.equals(origExpr)) {
			globExpr = newExpr;
			add();
		}

		return newExpr;
	}

	public Object visit(AbsVarName node, Object visArg) {
		return ImcGen.exprImCode.get(node);
	}


	/**
		statements
	 */

	// TODO handle arr/rec
	public Object visit(AbsAssignStmt node, Object visArg) {
		ImcExpr dst = (ImcExpr) node.dst.accept(this, visArg);
		ImcExpr src = (ImcExpr) node.src.accept(this, visArg);

		if (src instanceof ImcCALL) {
			ImcTEMP t = new ImcTEMP(new Temp());
			Vector<ImcStmt> stmts = stack.peek();
			stmts.add(new ImcMOVE(t, src));
			src = t;
		}

		stack.peek().add(new ImcMOVE(dst, src));
		return null;
	}

	public Object visit(AbsExprStmt node, Object visArg) {
		ImcExpr expr = (ImcExpr) node.expr.accept(this, visArg);
		stack.peek().add(new ImcESTMT(expr));
		return null;
	}

	public Object visit(AbsIfStmt node, Object visArg) {
		ImcExpr cond = (ImcExpr) node.cond.accept(this, visArg);
		if (cond instanceof ImcCALL) {
			ImcTEMP t = new ImcTEMP(new Temp());
			stack.peek().add(new ImcMOVE(t, cond));
			cond = t;
		}

		Vector<ImcStmt> stmts = ((ImcSTMTS) ImcGen.stmtImCode.get(node)).stmts();
		Label l1 = ((ImcLABEL) stmts.get(1)).label;
		Label l2 = ((ImcLABEL) stmts.get(3)).label;

		stack.peek().add(new ImcCJUMP(cond, l1, l2));
		stack.peek().add(stmts.get(1));
		node.thenBody.accept(this, visArg);
		stack.peek().add(stmts.get(3));
		node.elseBody.accept(this, visArg);
		return null;
	}

	public Object visit(AbsStmts node, Object visArg) {
		for (AbsStmt stmt : node.stmts())
			stmt.accept(this, visArg);
		return null;
	}

	public Object visit(AbsWhileStmt node, Object visArg) {
		Vector<ImcStmt> stmts = ((ImcSTMTS) ImcGen.stmtImCode.get(node)).stmts();
		stack.peek().add(stmts.get(0));

		ImcExpr cond = (ImcExpr) node.cond.accept(this, visArg);
		Label l1 = ((ImcLABEL) stmts.get(2)).label;
		Label l2 = ((ImcLABEL) stmts.get(5)).label;

		if (cond instanceof ImcCALL) {
			ImcTEMP t = new ImcTEMP(new Temp());
			stack.peek().add(new ImcMOVE(t, cond));
			cond = t;
		}

		stack.peek().add(new ImcCJUMP(cond, l1, l2));
		stack.peek().add(stmts.get(2));
		node.body.accept(this, visArg);
		stack.peek().add(stmts.get(4));
		stack.peek().add(stmts.get(5));
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
		{
			Vector<ImcStmt> canStmts = new Vector<>();
			canStmts.add(new ImcLABEL(begLabel));

			stack.add(canStmts);
			ImcExpr value = (ImcExpr) node.value.accept(this, null);
			canStmts = stack.pop();

			canStmts.add(new ImcMOVE(new ImcTEMP(RV), value));
			canStmts.add(new ImcJUMP(endLabel));
			canStmts.add(new ImcLABEL(endLabel));

			CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
			LinCode.add(fragment);
		}
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


	/**
		Adds the global code fragment (not in a frame, so we make a new bogus one)
	 */
	public void add() {
		Frame frame = new Frame(new Label(""), 0, 0, 0);
		Temp RV = new Temp();
		Label begLabel = new Label();
		Label endLabel = new Label();
		ImcStmt stmt = new ImcMOVE(new ImcTEMP(RV), globExpr);

		Vector<ImcStmt> canStmts = new Vector<>();
		canStmts.add(new ImcLABEL(begLabel));
		canStmts.addAll(stack.peek());
		canStmts.add(stmt);
		canStmts.add(new ImcJUMP(endLabel));
		canStmts.add(new ImcLABEL(endLabel));

		CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
		LinCode.add(fragment);
	}
}
