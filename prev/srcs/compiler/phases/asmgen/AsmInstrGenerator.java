package compiler.phases.asmgen;

import common.report.Report;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcVisitor;
import compiler.phases.imcgen.code.*;

import java.util.Vector;

/**
 * @author jan
 */

public class AsmInstrGenerator implements ImcVisitor<Object, Object> {

    /**
     * statements
     */

    // BNZ
    public Object visit(ImcCJUMP node, Object visArg) {
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

    public Object visit(ImcESTMT node, Object visArg) {
        return node.expr.accept(this, visArg);
    }

    // JMP
    public Object visit(ImcJUMP node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        jumps.add(node.label);

        AsmOPER jump = new AsmOPER("JMP", uses, defs, jumps);
        AsmGen.add(jump);
        return jump;
    }

    // label
    public Object visit(ImcLABEL node, Object visArg) {
        AsmLABEL label = new AsmLABEL(node.label);
        AsmGen.add(label);
        return label;
    }

    // several options depending on the canonical tree
    // TODO blocks
    public Object visit(ImcMOVE node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        MoveMatch match = matchMove(node);

        switch (match) {

            // both registers - SET
            case TempTemp:
                uses.add(((ImcTEMP) node.src).temp);
                defs.add(((ImcTEMP) node.dst).temp);
                AsmMOVE move = new AsmMOVE("SET `d0, `s0", uses, defs, null);
                AsmGen.add(move);
                return move;

            case MemMem:
                break;
            case MemLeft:
                break;
            case MemRight:
                break;
            case MemLeftName:
                break;
            case MemRightName:
                break;

            // STO with offset
            case MemLeftBinopConstName:


            case MemLeftBinopConstTemp:
                ImcMEM mem = (ImcMEM) node.dst;
                ImcBINOP binop = (ImcBINOP) mem.addr;
                ImcCONST constant = (ImcCONST) ((binop.fstExpr instanceof ImcCONST) ? binop.fstExpr : binop.sndExpr);
                Temp temp = ((ImcTEMP) ((binop.fstExpr instanceof ImcTEMP) ? binop.fstExpr : binop.sndExpr)).temp;
                uses.add(temp);
                AsmOPER store = new AsmOPER("STO `d0, `s0, " + constant.toString(), uses, defs, null);
                AsmGen.add(store);
                return store;

            case MemRightBinopConstName:
                break;
            case MemRightBinopConstTemp:
                break;
            case Other:
                break;
        }

        throw new Report.InternalError();
    }


    /**
     * expressions
     */

    // TODO possible improvement - match with const
    public Object visit(ImcBINOP node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        Temp s1 = (Temp) node.fstExpr.accept(this, visArg);
        Temp s2 = (Temp) node.sndExpr.accept(this, visArg);
        Temp d = new Temp();

        uses.add(s1);
        uses.add(s2);
        defs.add(d);

        switch (node.oper) {
            case IOR:
                AsmGen.instrs().add(new AsmOPER("OR `d0, `s0, `s1", uses, defs, jumps));
                break;
            case XOR:
                AsmGen.instrs().add(new AsmOPER("XOR `d0, `s0, `s1", uses, defs, jumps));
                break;
            case AND:
                AsmGen.instrs().add(new AsmOPER("AND `d0, `s0, `s1", uses, defs, jumps));
                break;
            case EQU:
            case NEQ:
            case LTH:
            case GTH:
            case LEQ:
            case GEQ:
                AsmGen.instrs().add(new AsmOPER("CMP `d0, `s0, `s1", uses, defs, jumps));
                String instr = "";
                switch (node.oper) {
                    case EQU:
                        instr = "ZSZ";
                        break;
                    case NEQ:
                        instr = "ZSNZ";
                        break;
                    case LTH:
                        instr = "ZSN";
                        break;
                    case GTH:
                        instr = "ZSP";
                        break;
                    case LEQ:
                        instr = "ZSNP";
                        break;
                    case GEQ:
                        instr = "ZSNN";
                        break;
                }
                AsmGen.instrs().add(new AsmOPER(instr + " `d0, `s0, 1", defs, defs, jumps));
                break;
            case ADD:
                AsmGen.instrs().add(new AsmOPER("ADD `d0, `s0, `s1", uses, defs, jumps));
                break;
            case SUB:
                AsmGen.instrs().add(new AsmOPER("SUB `d0, `s0, `s1", uses, defs, jumps));
                break;
            case MUL:
                AsmGen.instrs().add(new AsmOPER("MUL `d0, `s0, `s1", uses, defs, jumps));
                break;
            case DIV:
                // rR
                defs.add(new Temp());
                AsmGen.instrs().add(new AsmOPER("DIV `d0, `s0, `s1", uses, defs, jumps));
                break;
            case MOD:
                // use rR instead of the first register (move the value)
                Temp d2 = new Temp();
                defs.add(d2);
                AsmGen.instrs().add(new AsmOPER("DIV `d0, `s0, `s1", uses, defs, jumps));
                Vector<Temp> uses2 = new Vector<>();
                Vector<Temp> defs2 = new Vector<>();
                uses2.add(d2);
                defs2.add(d);
                AsmGen.instrs().add(new AsmMOVE("GET `d0, rR", uses2, defs2, jumps));
                return d2;
        }
        return d;
    }

    // TODO check and fix
    public Object visit(ImcCALL node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        for (ImcExpr arg : node.args()) {
            if (arg instanceof ImcTEMP) {
                uses.add(((ImcTEMP) arg).temp);
            }
            arg.accept(this, visArg);
        }

        jumps.add(node.label);
        AsmOPER pushj = new AsmOPER("PUSHJ `s0, " + node.label, uses, defs, jumps);

        throw new Report.InternalError();
    }

    // TODO check if this loads negative numbers
    public Object visit(ImcCONST node, Object visArg) {
        long value = node.value;

        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();
        Temp t = new Temp();
        defs.add(t);

        long val1 = value & 0x0000000000000FFFF;
        long val2 = (value >> 16) & 0x0000000000000FFFF;
        long val3 = (value >> 32) & 0x0000000000000FFFF;
        long val4 = (value >> 48) & 0x0000000000000FFFF;

        AsmGen.instrs().add(new AsmOPER("SETL `d0, " + Long.toString(val1), uses, defs, jumps));
        uses.add(t);

        AsmGen.instrs().add(new AsmOPER("INCML `d0, " + Long.toString(val2), uses, defs, jumps));
        AsmGen.instrs().add(new AsmOPER("INCML `d0, " + Long.toString(val3), uses, defs, jumps));
        AsmGen.instrs().add(new AsmOPER("INCML `d0, " + Long.toString(val4), uses, defs, jumps));

        return t;
    }

    // Load the value from memory and return a register. (LDO)
    // TODO blocks
    public Object visit(ImcMEM node, Object visArg) {
        switch (matchMem(node)) {
            case Name:
                break;
            case BinopConstName:
                break;
            case BinopConstTemp:
                break;
            case Other:
                break;
        }
        return null;
    }

    // Load the label address into a register and return that register. (LDA)
    public Object visit(ImcNAME node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        Temp t = new Temp();
        defs.add(t);

        AsmOPER lda = new AsmOPER("LDA `d0, " + node.label.name, uses, defs, jumps);
        AsmGen.instrs().add(lda);
        return t;
    }

    public Object visit(ImcTEMP node, Object visArg) {
        return node.temp;
    }

    public Object visit(ImcUNOP node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        Temp s = (Temp) node.subExpr.accept(this, visArg);
        Temp d = new Temp();

        uses.add(s);
        defs.add(d);

        switch (node.oper) {
            case NOT:
                AsmGen.instrs().add(new AsmOPER("NEG `d0, 1, `s0", uses, defs, jumps));
                break;
            case NEG:
                AsmGen.instrs().add(new AsmOPER("NEG `d0, `s0", uses, defs, jumps));
                break;
        }

        return d;
    }


    private enum MoveMatch {
        TempTemp,
        MemMem,
        MemLeft,
        MemRight,
        MemLeftName,
        MemRightName,
        MemLeftBinopConstName,
        MemLeftBinopConstTemp,
        MemRightBinopConstName,
        MemRightBinopConstTemp,
        Other
    }

    // Match a MOVE canonical subtree to the biggest available "paving stone".
    private MoveMatch matchMove(ImcMOVE node) {

        // move - temp : temp
        if (node.dst instanceof ImcTEMP && node.src instanceof ImcTEMP) {
            return MoveMatch.TempTemp;
        }

        // move - mem
        if (node.dst instanceof ImcMEM) {
            ImcMEM dst = (ImcMEM) node.dst;

            switch (matchMem(dst)) {

                case Name:
                    break;

                case BinopConstName:
                    break;

                case BinopConstTemp:
                    break;

                case Other:
                    if (node.src instanceof ImcMEM) {
                        return MoveMatch.MemMem;
                    }
            }

            return MoveMatch.MemLeft;
        }


        if (node.src instanceof ImcMEM) {
            ImcMEM src = (ImcMEM) node.src;

            switch (matchMem(src)) {

                case Name:
                    break;
                case BinopConstName:
                    break;
                case BinopConstTemp:
                    return MoveMatch.MemRightBinopConstTemp;
                case Other:
                    break;
            }

            return MoveMatch.MemRight;
        }

        return MoveMatch.Other;
    }

    private enum MemMatch {
        Name,
        BinopConstName,
        BinopConstTemp,
        Other
    }

    private MemMatch matchMem(ImcMEM node) {
        if (node.addr instanceof ImcBINOP) {
            ImcBINOP binop = (ImcBINOP) node.addr;

            // mem - binop - const : temp / name
            if (binop.fstExpr instanceof ImcCONST) {

                if (binop.sndExpr instanceof ImcTEMP) {
                    return MemMatch.BinopConstTemp;
                }

                if (binop.sndExpr instanceof ImcNAME) {
                    return MemMatch.BinopConstName;
                }

            }

            if (binop.sndExpr instanceof ImcCONST) {
                if (binop.fstExpr instanceof ImcTEMP) {
                    return MemMatch.BinopConstTemp;
                }

                if (binop.fstExpr instanceof ImcNAME) {
                    return MemMatch.BinopConstName;
                }
            }

        }

        // mem - name
        else if (node.addr instanceof ImcNAME) {
            return MemMatch.Name;
        }

        return MemMatch.Other;
    }

}
