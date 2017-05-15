package compiler.phases.asmgen;

import common.report.Report;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcGen;
import compiler.phases.imcgen.ImcVisitor;
import compiler.phases.imcgen.code.*;
import compiler.phases.lincode.CodeFragment;
import compiler.phases.lincode.Fragment;
import compiler.phases.lincode.LinCode;

import java.util.Vector;

/**
 * @author jan
 */

public class AsmInstrGenerator implements ImcVisitor<Object, Object> {

    private CodeFragment fragment;

    public AsmInstrGenerator(CodeFragment fragment) {
        this.fragment = fragment;
    }

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

        AsmOPER cjump = new AsmOPER("BNZ `s0,"+node.posLabel.name, uses, defs, jumps);
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

        AsmOPER jump = new AsmOPER("JMP " + node.label.name, uses, defs, jumps);
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
    public Object visit(ImcMOVE node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();

        MoveMatch match = matchMove(node);
        Report.info(match.toString());

        ImcMEM mem;
        ImcBINOP binop;
        ImcCONST constant;
        Temp temp, srcReg, dstReg;
        AsmOPER store, load;
        AsmMOVE move;

        switch (match) {
            case MemLeft:
                mem = (ImcMEM) node.dst;
                dstReg = (Temp) mem.addr.accept(this, visArg);
                srcReg = (Temp) node.src.accept(this, visArg);
                uses.add(srcReg);
                uses.add(dstReg);
                store = new AsmOPER("STO `s0,`s1,0", uses, null, null);
                AsmGen.add(store);
                return store;

            case MemRight:
                mem = (ImcMEM) node.src;
                srcReg = (Temp) mem.addr.accept(this, visArg);
                dstReg = (Temp) node.dst.accept(this, visArg);
                uses.add(srcReg);
                defs.add(dstReg);
                load = new AsmOPER("LDO `d0,`s0,0", uses, defs, null);
                AsmGen.add(load);
                return load;

            // STO with offset
            case MemLeftBinopTempConst:
                srcReg = (Temp) node.src.accept(this, visArg);
                mem = (ImcMEM) node.dst;
                binop = (ImcBINOP) mem.addr;
                constant = (ImcCONST) binop.sndExpr;
                temp = ((ImcTEMP) binop.fstExpr).temp;
                uses.add(srcReg);
                uses.add(temp);
                store = new AsmOPER("STO `s0,`s1," + constant.toString(), uses, null, null);
                AsmGen.add(store);
                return store;

            // LDO with offset
            case MemRightBinopTempConst:
                dstReg = (Temp) node.dst.accept(this, visArg);
                mem = (ImcMEM) node.src;
                binop = (ImcBINOP) mem.addr;
                constant = (ImcCONST) binop.sndExpr;
                temp = ((ImcTEMP) binop.fstExpr).temp;
                uses.add(temp);
                defs.add(dstReg);
                load = new AsmOPER("LDO `d0,`s0," + constant.toString(), uses, defs, null);
                AsmGen.add(load);
                return load;

            // both registers - SET
            case TempTemp:
            case Call:
            case Other:
                uses.add((Temp) node.src.accept(this, visArg));
                defs.add((Temp) node.dst.accept(this, visArg));
                move = new AsmMOVE("SET `d0,`s0", uses, defs, null);
                AsmGen.add(move);
                return move;
        }
        return null;

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
                AsmGen.add(new AsmOPER("OR `d0,`s0,`s1", uses, defs, jumps));
                break;
            case XOR:
                AsmGen.add(new AsmOPER("XOR `d0,`s0,`s1", uses, defs, jumps));
                break;
            case AND:
                AsmGen.add(new AsmOPER("AND `d0,`s0,`s1", uses, defs, jumps));
                break;
            case EQU:
            case NEQ:
            case LTH:
            case GTH:
            case LEQ:
            case GEQ:
                AsmGen.add(new AsmOPER("CMP `d0,`s0,`s1", uses, defs, jumps));
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
                AsmGen.add(new AsmOPER(instr + " `d0,`s0,1", defs, defs, jumps));
                break;
            case ADD:
                AsmGen.add(new AsmOPER("ADD `d0,`s0,`s1", uses, defs, jumps));
                break;
            case SUB:
                AsmGen.add(new AsmOPER("SUB `d0,`s0,`s1", uses, defs, jumps));
                break;
            case MUL:
                AsmGen.add(new AsmOPER("MUL `d0,`s0,`s1", uses, defs, jumps));
                break;
            case DIV:
                // rR
                defs.add(new Temp());
                AsmGen.add(new AsmOPER("DIV `d0,`s0,`s1", uses, defs, jumps));
                break;
            case MOD:
                // use rR instead of the first register (move the value)
                Temp d2 = new Temp();
                defs.add(d2);
                AsmGen.add(new AsmOPER("DIV `d0,`s0,`s1", uses, defs, jumps));
                Vector<Temp> uses2 = new Vector<>();
                Vector<Temp> defs2 = new Vector<>();
                uses2.add(d2);
                defs2.add(d);
                AsmGen.add(new AsmMOVE("GET `d0,rR", uses2, defs2, jumps));
                return d2;
        }
        return d;
    }

    // TODO check and fix
    public Object visit(ImcCALL node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        int offset = 0;
        for (ImcExpr expr : node.args()) {
            Object arg = expr.accept(this, visArg);
            if (arg instanceof Temp) {
                Temp regArg = (Temp) arg;
                Vector<Temp> storeUses = new Vector<>();
                storeUses.add(regArg);
                storeUses.add(ImcGen.SP);
                AsmGen.add(new AsmOPER("STO `s0,`s1," + offset, storeUses, null, null));
                offset += 8;
            }

        }

        Temp rv = null;
        for (Fragment f : LinCode.fragments()) {
            if (f instanceof CodeFragment) {
                CodeFragment frag = (CodeFragment) f;
                if (frag.frame.label.equals(node.label)) {
                    rv = frag.RV;
                }
            }
        }

        // rJ
        defs.add(new Temp());

        // X - size of caller's stack frame
        // calculate with FP - SP ?
        uses.add(new Temp());

        jumps.add(node.label);
        AsmGen.add(new AsmOPER("PUSHJ `s0," + node.label.name, uses, defs, jumps));
        return rv;

    }

    // TODO check if this loads negative numbers
    // TODO always load to register?
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

        AsmGen.add(new AsmOPER("SETL `d0," + Long.toString(val1), uses, defs, jumps));
        uses.add(t);

        AsmGen.add(new AsmOPER("INCML `d0," + Long.toString(val2), uses, defs, jumps));
        AsmGen.add(new AsmOPER("INCMH `d0," + Long.toString(val3), uses, defs, jumps));
        AsmGen.add(new AsmOPER("INCH `d0," + Long.toString(val4), uses, defs, jumps));

        return t;
    }

    // Load the value from memory and return a register. (LDO)
    public Object visit(ImcMEM node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();

        MemMatch match = matchMem(node);

        Temp dstReg = new Temp();
        Temp srcReg;
        AsmOPER load = null;
        defs.add(dstReg);

        Report.info(match.toString());

        switch (match) {
            case AddTempConst:
                ImcBINOP binop = (ImcBINOP) node.addr;
                srcReg = ((ImcTEMP) binop.fstExpr).temp;
                ImcCONST constant = ((ImcCONST) binop.sndExpr);
                uses.add(srcReg);
                load = new AsmOPER("LDO `d0,`s0," + constant.value, uses, defs, null);
                break;

            case AddConstTemp:
            case AddTempTemp:
            case AddConstConst:
            case Other:
                srcReg = (Temp) node.addr.accept(this, visArg);
                uses.add(srcReg);
                load = new AsmOPER("LDO `d0,`s0,0", uses, defs, null);
                break;
        }

        AsmGen.add(load);
        return dstReg;
    }

    // Load the label address into a register and return that register. (LDA)
    public Object visit(ImcNAME node, Object visArg) {
        Vector<Temp> uses = new Vector<>();
        Vector<Temp> defs = new Vector<>();
        Vector<Label> jumps = new Vector<>();

        Temp t = new Temp();
        defs.add(t);

        AsmOPER lda = new AsmOPER("LDA `d0," + node.label.name, uses, defs, jumps);
        AsmGen.add(lda);
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
                AsmGen.add(new AsmOPER("NEG `d0,1,`s0", uses, defs, jumps));
                break;
            case NEG:
                AsmGen.add(new AsmOPER("NEG `d0,`s0", uses, defs, jumps));
                break;
        }

        return d;
    }


    private enum MoveMatch {
        TempTemp,
        MemLeft,
        MemRight,
        MemLeftBinopTempConst,
        MemRightBinopTempConst,
        Call,
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
                case AddTempConst:
                    return MoveMatch.MemLeftBinopTempConst;
                case AddConstTemp:
                    break;
                case AddTempTemp:
                    break;
                case AddConstConst:
                    break;
                case Other:
                    break;
            }

            return MoveMatch.MemLeft;
        }


        if (node.src instanceof ImcMEM) {
            ImcMEM src = (ImcMEM) node.src;

            switch (matchMem(src)) {
                case AddTempConst:
                    return MoveMatch.MemRightBinopTempConst;
                case AddConstTemp:
                    break;
                case AddTempTemp:
                    break;
                case AddConstConst:
                    break;
                case Other:
                    break;
            }

            return MoveMatch.MemRight;
        }

        if (node.src instanceof ImcCALL && node.dst instanceof ImcTEMP) {
            return MoveMatch.Call;
        }

        return MoveMatch.Other;
    }

    private enum MemMatch {
        AddTempConst,
        AddConstTemp,
        AddTempTemp,
        AddConstConst,
        Other
    }

    private MemMatch matchMem(ImcMEM node) {
        if (node.addr instanceof ImcBINOP) {
            ImcBINOP binop = (ImcBINOP) node.addr;

            if (binop.oper == ImcBINOP.Oper.ADD) {
                switch (matchBinop(binop)) {
                    case ConstTemp:
                        return MemMatch.AddConstTemp;
                    case TempConst:
                        return MemMatch.AddTempConst;
                    case TempTemp:
                        return MemMatch.AddTempTemp;
                    case ConstConst:
                        return MemMatch.AddConstConst;
                }
            }
        }

        return MemMatch.Other;
    }

    private enum BinopMatch {
        ConstTemp,
        TempConst,
        TempTemp,
        ConstConst,
        Other
    }

    private BinopMatch matchBinop(ImcBINOP binop) {
        if (binop.fstExpr instanceof ImcCONST) {
            ImcCONST constant = (ImcCONST) binop.fstExpr;
            if (constant.value < 0) {
                return BinopMatch.Other;
            }

            if (binop.sndExpr instanceof ImcTEMP) {
                return BinopMatch.ConstTemp;
            }
            else if (binop.sndExpr instanceof ImcCONST) {
                return BinopMatch.ConstConst;
            }
        }

        if (binop.sndExpr instanceof ImcCONST) {
            ImcCONST constant = (ImcCONST) binop.sndExpr;
            if (constant.value < 0) {
                return BinopMatch.Other;
            }

            if (binop.fstExpr instanceof ImcTEMP) {
                return BinopMatch.TempConst;
            }
        }

        if (binop.fstExpr instanceof ImcTEMP) {
            if (binop.sndExpr instanceof ImcTEMP) {
                return BinopMatch.TempTemp;
            }
        }

        return BinopMatch.Other;

    }

}
