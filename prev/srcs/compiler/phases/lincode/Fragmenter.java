package compiler.phases.lincode;

import java.util.*;
import compiler.phases.abstr.*;
import compiler.phases.abstr.abstree.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;
import compiler.phases.imcgen.code.*;

public class Fragmenter extends AbsFullVisitor<Object, Object> {

	@Override
	public Object visit(AbsFunDef funDef, Object visArg) {
		Frame frame = Frames.frames.get(funDef);
		Temp RV = new Temp();
		Label begLabel = new Label();
		Label endLabel = new Label();
		ImcStmt stmt = new ImcMOVE(new ImcTEMP(RV), ImcGen.exprImCode.get(funDef.value));
		{
			Vector<ImcStmt> canStmts = new Vector<ImcStmt>();

			// TODO

			CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
			LinCode.add(fragment);
		}
		funDef.value.accept(this, null);
		return null;
	}
	
	@Override
	public Object visit(AbsVarDecl varDecl, Object visArg) {
		Access access = Frames.accesses.get(varDecl);
		if (access instanceof AbsAccess) {
			AbsAccess absAccess = (AbsAccess)access;
			DataFragment fragment = new DataFragment(absAccess.label, absAccess.size);
			LinCode.add(fragment);
		}
		return null;
	}
	
}
