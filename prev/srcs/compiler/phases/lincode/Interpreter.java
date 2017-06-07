package compiler.phases.lincode;

import common.report.Report;
import compiler.phases.frames.Label;
import compiler.phases.frames.Temp;
import compiler.phases.imcgen.ImcVisitor;
import compiler.phases.imcgen.code.*;

import java.util.HashMap;
import java.util.Stack;

public class Interpreter {

    enum DEBUG {
        NONE, FULL
    }

    public DEBUG debug = DEBUG.NONE;

    // Static variables: labels to addresses.
    private HashMap<Label, Long> addr = new HashMap<Label, Long>();

    // Memory of octabytes.
    private HashMap<Long, Long> mem = new HashMap<Long, Long>();

    // Temporaries.
    private Stack<HashMap<Temp, Long>> tmp = new Stack<HashMap<Temp, Long>>();

    private long SP;

    private long HP;

    public void execute() {

        HP = 8;
        // FP = 0x10000000;
        SP = 0x10000000;

        // Put variables into the memory.
        for (Fragment fragment : LinCode.fragments())
            if (fragment instanceof DataFragment) {
                DataFragment frag = (DataFragment) fragment;
                addr.put(frag.label, HP);
                HP += frag.size;
            }

        // Execute the initial fragment.
        execute(fndCodeFragment(new Label("")));
    }

    public void execute(CodeFragment codeFragment) {
        tmp.push(new HashMap<Temp, Long>());

        if (debug == DEBUG.FULL)
            System.out.println("ENTER: " + codeFragment.frame.label.name);
        if (debug == DEBUG.FULL) {
            // System.out.println("FP=" + new Long(FP));
            System.out.println("SP=" + new Long(SP));
        }

        // Create a stack frame.
        memST(SP - codeFragment.frame.locsSize - 8, tmpLD(codeFragment.FP));
        tmpST(codeFragment.FP, SP);
        SP = SP - codeFragment.frame.size;

        if (debug == DEBUG.FULL) {
            System.out.println("FP=" + tmpLD(codeFragment.FP));
            System.out.println("SP=" + new Long(SP));
        }

        // Execute.
        int PC = 0;
        do {
            ImcStmt stmt = codeFragment.stmts().get(PC++);
            if (stmt instanceof ImcLABEL && ((ImcLABEL) stmt).label.name.equals(codeFragment.endLabel.name))
                break;
            Label label = stmt.accept(new CodeInterpreter(), null);
            if (label != null) {
                if (label.name.equals(codeFragment.endLabel.name))
                    break;
                for (PC = 0; PC < codeFragment.stmts().size(); PC++) {
                    ImcStmt next = codeFragment.stmts().get(PC);
                    if (next instanceof ImcLABEL)
                        if (((ImcLABEL) next).label.name.equals(label.name))
                            break;
                }
                if (PC == codeFragment.stmts().size())
                    throw new Report.Error("INTERPRETER: Label " + label.name + " not found.");
            }
        } while (true);

        // Return the result.
        if (debug == DEBUG.FULL) {
            System.out.println("RV=" + tmpLD(codeFragment.RV));
        }
        memST(tmpLD(codeFragment.FP), tmpLD(codeFragment.RV));

        // Destroy the stack frame.
        SP = SP + codeFragment.frame.size;
        tmpST(codeFragment.FP, memLD(SP - codeFragment.frame.locsSize - 8));

        if (debug == DEBUG.FULL)
            System.out.println("LEAVE: " + codeFragment.frame.label.name);
        if (debug == DEBUG.FULL) {
            System.out.println("SP=" + new Long(SP));
        }

        tmp.pop();
    }

    private CodeFragment fndCodeFragment(Label label) {
        for (Fragment fragment : LinCode.fragments())
            if (fragment instanceof CodeFragment) {
                CodeFragment codeFrag = (CodeFragment) fragment;
                if (codeFrag.frame.label.name.equals(label.name))
                    return codeFrag;
            }
        throw new Report.Error("INTERPRETER: No initial code fragment.");
    }

    private void memST(long addr, long value) {
        mem.put(addr, value);
    }

    private long memLD(long addr) {
        Long value = mem.get(addr);
        if (value == null)
            return 0;
        else
            return value;
    }

    private void tmpST(Temp temp, long value) {
        if (debug == DEBUG.FULL)
            System.out.println(value + " -> " + "T" + new Long(temp.temp));
        tmp.peek().put(temp, value);
    }

    private long tmpLD(Temp temp) {
        Long value = tmp.peek().get(temp);
        if (value == null)
            value = new Long(0);
        if (debug == DEBUG.FULL)
            System.out.println("T" + new Long(temp.temp) + " -> " + value);
        return value;
    }

    //

    private class CodeInterpreter implements ImcVisitor<Label, Object> {

        @Override
        public Label visit(ImcCJUMP cjump, Object visArg) {
            Long cond = cjump.cond.accept(new ExprInterpreter(), null);
            return (cond != 0) ? cjump.posLabel : cjump.negLabel;
        }

        @Override
        public Label visit(ImcESTMT estmt, Object visArg) {
            estmt.expr.accept(new ExprInterpreter(), null);
            return null;
        }

        @Override
        public Label visit(ImcJUMP jump, Object visArg) {
            return jump.label;
        }

        @Override
        public Label visit(ImcLABEL label, Object visArg) {
            return null;
        }

        @Override
        public Label visit(ImcMOVE move, Object visArg) {
            if (move.dst instanceof ImcTEMP) {
                Long src = move.src.accept(new ExprInterpreter(), null);
                tmpST(((ImcTEMP) (move.dst)).temp, src);
                return null;
            }
            if (move.dst instanceof ImcMEM) {
                Long dst = ((ImcMEM) (move.dst)).addr.accept(new ExprInterpreter(), null);
                Long src = move.src.accept(new ExprInterpreter(), null);
                memST(dst, src);
                return null;
            }
            throw new Report.InternalError();
        }

    }

    //

    private class ExprInterpreter implements ImcVisitor<Long, Object> {

        public Long visit(ImcBINOP binOp, Object visArg) {
            Long fstExpr = binOp.fstExpr.accept(this, null);
            Long sndExpr = binOp.sndExpr.accept(this, null);
            switch (binOp.oper) {
                case IOR:
                    return new Long(((fstExpr > 0) | (sndExpr > 0)) ? 1 : 0);
                case XOR:
                    return new Long(((fstExpr > 0) ^ (sndExpr > 0)) ? 1 : 0);
                case AND:
                    return new Long(((fstExpr > 0) & (sndExpr > 0)) ? 1 : 0);
                case EQU:
                    return new Long((fstExpr == sndExpr) ? 1 : 0);
                case NEQ:
                    return new Long((fstExpr != sndExpr) ? 1 : 0);
                case GEQ:
                    return new Long((fstExpr >= sndExpr) ? 1 : 0);
                case LEQ:
                    return new Long((fstExpr <= sndExpr) ? 1 : 0);
                case GTH:
                    return new Long((fstExpr > sndExpr) ? 1 : 0);
                case LTH:
                    return new Long((fstExpr < sndExpr) ? 1 : 0);
                case ADD:
                    return fstExpr + sndExpr;
                case SUB:
                    return fstExpr - sndExpr;
                case MUL:
                    return fstExpr * sndExpr;
                case DIV:
                    return fstExpr / sndExpr;
                case MOD:
                    return fstExpr % sndExpr;
            }
            throw new Report.InternalError();
        }

        public Long visit(ImcCALL call, Object visArg) {
            long argOffset = SP;
            for (ImcExpr arg : call.args()) {
                Long value = arg.accept(this, null);
                memST(argOffset, value);
                argOffset += 8;
            }
            if (call.label.name.equals("_printchar")) {
                Long value = memLD(SP + 8);
                System.out.print((char) (value % 256));
                return new Long(0);
            }
            if (call.label.name.equals("_printint")) {
                Long value = memLD(SP + 8);
                System.out.print(value);
                return new Long(0);
            }
            if (call.label.name.equals("_println")) {
                System.out.println();
                return new Long(0);
            }
            execute(fndCodeFragment(call.label));
            return memLD(SP);
        }

        public Long visit(ImcCONST constant, Object visArg) {
            return constant.value;
        }

        public Long visit(ImcMEM mem, Object visArg) {
            Long addr = mem.addr.accept(this, null);
            return memLD(addr);
        }

        public Long visit(ImcNAME name, Object visArg) {
            Long value = addr.get(name.label);
            return value;
        }

        public Long visit(ImcTEMP temp, Object visArg) {
            Long value = tmpLD(temp.temp);
            return value;
        }

        public Long visit(ImcUNOP unOp, Object visArg) {
            Long subExpr = unOp.subExpr.accept(this, null);
            switch (unOp.oper) {
                case NOT:
                    return new Long((subExpr + 1) % 2);
                case NEG:
                    return new Long(-subExpr);
            }
            throw new Report.InternalError();
        }

    }

}
