package compiler.phases.finphase;

import common.report.Report;
import compiler.phases.Phase;
import compiler.phases.asmgen.*;
import compiler.phases.frames.Frame;
import compiler.phases.frames.Temp;
import compiler.phases.lincode.CodeFragment;
import compiler.phases.lincode.DataFragment;
import compiler.phases.lincode.Fragment;
import compiler.phases.lincode.LinCode;
import compiler.phases.regalloc.RegAlloc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author jan
 */
public class FinPhase extends Phase {

    /** All the lines of the assembly code. **/
    private LinkedList<String> program;

    public FinPhase() {
        super("finphase");
        this.program = new LinkedList<>();
    }

    /**
     * Finish the compilation:
     * - generate assembly code from data fragments
     * - add the init code
     * - remove unnecessary SET instructions
     * - add the prologue and epilogue to each function
     * - add the stdlib functions
     */
    public void finishCompilation() {
        addDataFragments();
        addInitCode();

        for (CodeFragment fragment : AsmGen.instrs.keySet()) {
            LinkedList<AsmInstr> instrList = AsmGen.instrs.get(fragment);
            HashMap<Temp, Integer> mapping = RegAlloc.registers.get(fragment);
            removeSET(instrList, mapping);
            addPrologueEpilogue(instrList, mapping, fragment);
        }

        addStdLib();
    }

    /**
     * Allocate data from data fragments.
     */
    private void addDataFragments() {
        program.add("SP GREG");
        program.add("FP GREG");
        program.add("HP GREG");
        program.add(" LOC Data_Segment");
        program.add(" GREG @");

        for (Fragment fragment : LinCode.fragments()) {
            if (fragment instanceof DataFragment) {
                DataFragment dataFragment = (DataFragment) fragment;
                long size = dataFragment.size;
                StringBuilder allocation = new StringBuilder(dataFragment.label.name + " OCTA 0");

                while (size > 8) {
                    allocation.append(",0");
                    size -= 8;
                }

                program.add(allocation.toString());
            }
        }

        // for printing
        program.add("buf BYTE 0 BYTE 0");

        program.add("");
    }


    /**
     * Remove unnecessary SET instructions.
     *
     * @param instrList The list of instructions.
     * @param mapping   The mapping from temporaries to registers.
     */

    private void removeSET(LinkedList<AsmInstr> instrList, HashMap<Temp, Integer> mapping) {
        int removedSET = 0;
        int removedExtra = 0;

        for (int i = 0; i < instrList.size(); i++) {
            AsmInstr instr = instrList.get(i);
            if (instr instanceof AsmMOVE) {
                Temp t1 = instr.uses().get(0);
                Temp t2 = instr.defs().get(0);
                if (mapping.get(t1) != null && mapping.get(t1).equals(mapping.get(t2))) {
                    AsmInstr previous = instrList.get(i - 1);
                    AsmInstr next = instrList.get(i + 1);

                    instrList.remove(i);
                    i--;
                    removedSET++;

                    if (previous instanceof AsmOPER) {
                        if (((AsmOPER) previous).spill && previous.instr().contains("LDO") && previous.defs().contains(t1)) {
                            instrList.remove(previous);
                            i--;
                            removedExtra++;
                        }
                    }
                    if (next instanceof AsmOPER) {
                        if (((AsmOPER) next).spill && next.instr().contains("STO") && next.uses().contains(t2)) {
                            instrList.remove(next);
                            i--;
                            removedExtra++;
                        }
                    }
                }

            }
        }

        //Report.info("Removed " + removedSET + " SET instructions and " + removedExtra + " extras.");
    }


    /**
     * Add the prologue and epilogue to a function body.
     */
    private void addPrologueEpilogue(LinkedList<AsmInstr> instrList, HashMap<Temp, Integer> mapping, CodeFragment fragment) {
        Frame frame = fragment.frame;
        long oldFPOffset = frame.locsSize + 16;

        String[] prologue = prologue(frame.label.name, frame.size, oldFPOffset);
        String[] epilogue = epilogue(mapping.get(fragment.RV), oldFPOffset);

        program.addAll(Arrays.asList(prologue));

        String label = "";
        for (AsmInstr instr : instrList) {
            if (instr instanceof AsmLABEL) {
                if (! label.equals("")) {
                    program.add(label + " SET $0,$0");
                }
                label = ((AsmLABEL) instr).label().name;
            } else {
                program.add(label + " " + instr.toString(mapping));
                label = "";
            }

        }

        program.addAll(Arrays.asList(epilogue));

        program.add("");
    }


    /**
     * Add the initialization code.
     */
    private void addInitCode() {
        String[] init = new String[]{
                " LOC #100",
                "Main PUT rG,250",
                " SETL $" + (RegAlloc.K - 1)+ ",0",
                " SETH $253,24568",
                " SUB $254,$253,8",
                " SETH $252,16384",
                " PUSHJ $" + RegAlloc.K + ",_",
                " TRAP 0,Halt,0",
                ""
        };

        program.addAll(Arrays.asList(init));
    }


    /**
     * Add the standard library functions:
     * - malloc
     * - free
     * - printint
     * - printchar
     * - println
     */
    private void addStdLib() {
        String[] malloc = new String[]{
                "_malloc LDO $0,$254,8",
                " STO $252,$254,8",
                " ADD $252,$252,$0",
                " POP 0,0",
                ""
        };

        String[] free = new String[]{
                "_free POP 0,0",
                ""
        };

        String[] printInt = new String[]{
                "_printint LDO $0,$254,8",
                " SETL $2,1",
                " GET $3,rJ",
                " CMP $1,$0,0",
                " BZ $1,_printzero",
                " BNN $1,_printint_radix",
                " NEG $0,$0",
                " SETL $1,45",
                " STO $1,$254,8",
                " PUSHJ $4,_printchar",
                "_printint_radix CMP $1,$2,$0",
                " BP $1,_printint_print_start",
                " MUL $2,$2,10",
                " JMP _printint_radix",
                "_printint_print_start DIV $2,$2,10",
                "_printint_print CMP $1,$2,0",
                " BNP $1,_printint_end",
                " DIV $0,$0,$2",
                " ADD $0,$0,48",
                " STO $0,$254,8",
                " PUSHJ $4,_printchar",
                " GET $0,rR",
                " DIV $2,$2,10",
                " JMP _printint_print",
                "_printint_end PUT rJ,$3",
                " POP 0,0",
                "_printzero ADD $0,$0,48",
                " STO $0,$254,8",
                " PUSHJ $4,_printchar",
                " JMP _printint_end",
                ""
        };

        String[] printChar = new String[]{
                "_printchar LDO $0,$254,8",
                " STB $0,buf",
                " LDA $255,buf",
                " TRAP 0,Fputs,StdOut",
                " POP 0,0",
                ""
        };

        String[] println = new String[]{
                "_println SETL $0,10",
                " STB $0,buf",
                " LDA $255,buf",
                " TRAP 0,Fputs,StdOut",
                " POP 0,0",
                ""
        };

        program.addAll(Arrays.asList(malloc));
        program.addAll(Arrays.asList(free));
        program.addAll(Arrays.asList(printInt));
        program.addAll(Arrays.asList(printChar));
        program.addAll(Arrays.asList(println));
    }


    // TODO check offset size
    private String[] prologue(String label, long frameSize, long oldFPOffset) {
        return new String[]{
                label + " SET $0,$253",
                " SET $253,$254",
                " SETL $1," + frameSize,
                " SUB $254,$254,$1",
                " SETL $1," + oldFPOffset,
                " SUB $1,$253,$1",
                " STO $0,$1,0",
                " GET $0,rJ",
                " STO $0,$1,8"
        };
    }

    private String[] epilogue(int RVRegister, long oldFPOffset) {
        return new String[]{
                " STO $" + RVRegister + ",$253,0",
                " SETL $1," + oldFPOffset,
                " SUB $1,$253,$1",
                " LDO $0,$1,8",
                " PUT rJ,$0",
                " SET $254,$253",
                " LDO $253,$1,0",
                " POP 0,0"
        };
    }

    @Override
    public void close() {
        String dstFileName = compiler.Main.cmdLineArgValue("--dst-file-name");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dstFileName));
            for (String line : program) {
                writer.write(line);
                writer.newLine();
                //System.out.println(line);
            }

            writer.close();
        } catch (IOException ioe) {
            throw new Report.Error("Cannot open output file for writing");
        }
    }
}
