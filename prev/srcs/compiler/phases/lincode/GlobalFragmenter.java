package compiler.phases.lincode;

import common.report.Report;
import compiler.phases.frames.Frame;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;
import compiler.phases.imcgen.code.*;

import java.util.Vector;

/**
 * Created by Jan on 28. 04. 2017.
 */
public class GlobalFragmenter {

    public Vector<ImcStmt> stmts;
    public ImcExpr expr;

    public GlobalFragmenter(Vector<ImcStmt> stmts, ImcExpr expr) {
        this.stmts = stmts;
        this.expr = expr;
    }

    public void add() {
        Frame frame = new Frame(new Label(""), 0, 0, 0);
        Temp RV = new Temp();
        Label begLabel = new Label();
        Label endLabel = new Label();
        ImcStmt stmt = new ImcMOVE(new ImcTEMP(RV), expr);
        {
            Vector<ImcStmt> canStmts = new Vector<ImcStmt>();
            canStmts.add(new ImcLABEL(begLabel));
            canStmts.addAll(stmts);
            canStmts.add(stmt);
            canStmts.add(new ImcJUMP(endLabel));
            canStmts.add(new ImcLABEL(endLabel));
            CodeFragment fragment = new CodeFragment(frame, canStmts, ImcGen.FP, RV, begLabel, endLabel);
            LinCode.add(fragment);
        }
    }
}
