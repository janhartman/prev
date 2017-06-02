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
        program.add(" LOC Data_Segment");

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
        program.add("");
    }


    /**
     * Add the initialization code.
     */
    // TODO set SP, FP, HP
    // TODO GREG ?
    private void addInitCode() {
        String[] init = new String[] {
                " LOC #100",
                " SET rG,250",
                " SET rL," + RegAlloc.K,
                " SET $254, ",
                " SET $253, ",
                " SET $252, ",
                " PUSHJ $" + RegAlloc.K + ",_",
                " TRAP 0,Halt,0"
        };

        program.addAll(Arrays.asList(init));
    }


    /**
     * Remove unnecessary SET instructions.
     * @param instrList The list of instructions.
     * @param mapping The mapping from temporaries to registers.
     */

    private void removeSET(LinkedList<AsmInstr> instrList, HashMap<Temp, Integer> mapping) {
        int removedSET = 0;
        int removedExtra = 0;

        for (int i = 0; i < instrList.size(); i++) {
            AsmInstr instr = instrList.get(i);
            if (instr instanceof AsmMOVE) {
                Temp t1 = instr.uses().get(0);
                Temp t2 = instr.defs().get(0);
                if ( mapping.get(t1) != null && mapping.get(t1).equals(mapping.get(t2))) {
                    AsmInstr previous = instrList.get(i-1);
                    AsmInstr next = instrList.get(i+1);

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

        Report.info("Removed " + removedSET + " SET instructions and "+ removedExtra + " extras.");
    }


    /**
     * Add the prologue and epilogue to a function body.
     */
    private void addPrologueEpilogue(LinkedList<AsmInstr> instrList, HashMap<Temp, Integer> mapping, CodeFragment fragment) {
        Frame frame = fragment.frame;
        long oldFPOffset = frame.locsSize + 16;

        String[] prologue = new String[] {
                frame.label.name + " SET $0,$253",
                " SET $253, $254",
                " SETL $1," + frame.size,
                " SUB $254,$254,$1",
                " SETL $1," + oldFPOffset,
                " SUB $1,$253,$1",
                " STO $0,$1,0",
                " GET $0,rJ",
                " STO $0,$1,8"
        };

        String[] epilogue = new String[] {
                " STO $" + mapping.get(fragment.RV) + ",$253,0",
                " SETL $1," + oldFPOffset,
                " SUB $1,$253,$1",
                " LDO $0,$1,8",
                " SET rJ,$0",
                " SET $254,$253",
                " LDO $253,$1,0",
                " POP 0,0"
        };

        program.addAll(Arrays.asList(prologue));

        for (AsmInstr instr : instrList) {

        }

        program.addAll(Arrays.asList(epilogue));
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


    }


    @Override
    public void close() {
        String dstFileName = compiler.Main.cmdLineArgValue("--dst-file-name");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(dstFileName));
            for (String line : program) {
                writer.write(line);
                writer.write("\n");
            }

            writer.close();
        }
        catch (IOException ioe) {
            throw new Report.Error("Cannot open output file for writing");
        }
    }
}
