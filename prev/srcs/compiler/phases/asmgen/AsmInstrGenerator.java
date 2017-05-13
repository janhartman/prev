package compiler.phases.asmgen;

import common.report.Report;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcVisitor;
import compiler.phases.imcgen.code.*;

import java.util.Vector;

/**
 * Created by Jan on 13. 05. 2017.
 */
public class AsmInstrGenerator implements ImcVisitor<AsmInstr, Object> {

    /**
     * statements
     */

    public AsmInstr visit(ImcCJUMP node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        uses.add(((ImcTEMP) node.cond).temp);
        jumps.add(node.posLabel);
        jumps.add(node.negLabel);

        AsmOPER cjump = new AsmOPER("BNZ `s0", uses, defs, jumps);
        AsmGen.add(cjump);
        return cjump;
    }

    public AsmInstr visit(ImcESTMT node, Object visArg) {
        return node.expr.accept(this, visArg);
    }

    public AsmInstr visit(ImcJUMP node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        jumps.add(node.label);

        AsmOPER jump = new AsmOPER("JMP", uses, defs, jumps);
        AsmGen.add(jump);
        return jump;
    }

    public AsmInstr visit(ImcLABEL node, Object visArg) {
        AsmLABEL label = new AsmLABEL(node.label);
        AsmGen.add(label);
        return label;
    }

    public AsmInstr visit(ImcMOVE node, Object visArg) {
        throw new Report.InternalError();
    }


    /**
     * expressions
     */

    public AsmInstr visit(ImcBINOP node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcCALL node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcCONST node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcMEM node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcNAME node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcTEMP node, Object visArg) {
        throw new Report.InternalError();
    }

    public AsmInstr visit(ImcUNOP node, Object visArg) {
        throw new Report.InternalError();
    }


}
