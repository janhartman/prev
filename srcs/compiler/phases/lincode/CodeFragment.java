package compiler.phases.lincode;

import common.logger.Logger;
import compiler.phases.frames.Frame;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcLogger;
import compiler.phases.imcgen.code.ImcStmt;

import java.util.Vector;

public class CodeFragment extends Fragment {

    // The stack frame of function.
    public final Frame frame;

    // The linearized intermediate code.
    private final Vector<ImcStmt> stmts;

    // The frame pointer.
    public final Temp FP;

    // The return value.
    public final Temp RV;

    // The label the prologue jumps to.
    public final Label begLabel;

    // The label the epilogue starts with.
    public final Label endLabel;

    public CodeFragment(Frame frame, Vector<ImcStmt> stmts, Temp FP, Temp RV, Label begLabel, Label endLabel) {
        this.frame = frame;
        this.stmts = new Vector<ImcStmt>(stmts);
        this.FP = FP;
        this.RV = RV;
        this.begLabel = begLabel;
        this.endLabel = endLabel;
    }

    public Vector<ImcStmt> stmts() {
        return stmts;
    }

    @Override
    public void log(Logger logger) {
        if (logger == null)
            return;
        logger.begElement("imccode");
        logger.addAttribute("FP", new Long(FP.temp).toString());
        logger.addAttribute("RV", new Long(RV.temp).toString());
        logger.addAttribute("beglabel", begLabel.name);
        logger.addAttribute("endlabel", endLabel.name);
        frame.log(logger);
        for (ImcStmt stmt : stmts) {
            logger.begElement("imclin");
            stmt.accept(new ImcLogger(), logger);
            logger.endElement();
        }
        logger.endElement();
    }

}
