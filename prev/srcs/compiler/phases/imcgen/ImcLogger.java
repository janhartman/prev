package compiler.phases.imcgen;

import common.logger.*;
import compiler.phases.imcgen.code.*;

public class ImcLogger implements ImcVisitor<Object, Logger> {

	@Override
	public Object visit(ImcBINOP binOp, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "BINOP");
		logger.addAttribute("value", binOp.oper.toString());
		binOp.fstExpr.accept(this, logger);
		binOp.sndExpr.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcCALL call, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "CALL");
		logger.addAttribute("value", call.label.name);
		for (ImcExpr arg : call.args())
			arg.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcCJUMP cjump, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "CJUMP");
		logger.addAttribute("value", cjump.posLabel.name + ":" + cjump.negLabel.name);
		cjump.cond.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcCONST constant, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "CONST");
		logger.addAttribute("value", new Long(constant.value).toString());
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcESTMT eStmt, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "ESTMT");
		eStmt.expr.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcJUMP jump, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "JUMP");
		logger.addAttribute("value", jump.label.name);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcLABEL label, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "LABEL");
		logger.addAttribute("value", label.label.name);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcMEM mem, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "MEM");
		mem.addr.accept(this, logger);
		logger.endElement();

		return null;
	}

	@Override
	public Object visit(ImcMOVE move, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "MOVE");
		move.dst.accept(this, logger);
		move.src.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcNAME name, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "NAME");
		logger.addAttribute("value", name.label.name);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcSEXPR sExpr, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "SEXPR");
		sExpr.stmt.accept(this, logger);
		sExpr.expr.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcSTMTS stmts, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "STMTS");
		for (ImcStmt stmt:stmts.stmts())
			stmt.accept(this, logger);
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcTEMP temp, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "TEMP");
		logger.addAttribute("value", new Long(temp.temp.temp).toString());
		logger.endElement();
		return null;
	}

	@Override
	public Object visit(ImcUNOP unOp, Logger logger) {
		if (logger == null)
			return null;
		logger.begElement("imc");
		logger.addAttribute("name", "UNOP");
		logger.addAttribute("value", unOp.oper.toString());
		unOp.subExpr.accept(this, logger);
		logger.endElement();
		return null;
	}

}
