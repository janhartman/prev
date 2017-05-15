package compiler.phases.asmgen;

import compiler.phases.Phase;
import compiler.phases.imcgen.code.ImcStmt;
import compiler.phases.lincode.*;

import java.util.LinkedList;

/**
 * Created by Jan on 12. 05. 2017.
 */
public class AsmGen extends Phase {

    /**
     * The list of instructions.
     */
    private static final LinkedList<AsmInstr> instrs = new LinkedList<>();

    public AsmGen() {
        super("asmgen");
    }

    /**
     * Adds a new instruction to the list of instructions.
     *
     * @param instr The new instruction.
     */
    public static void add(AsmInstr instr) {
        instrs.add(instr);
    }

    /**
     * Returns the list of all instructions.
     *
     * @return The list of all instructions.
     */
    private static LinkedList<AsmInstr> instrs() {
        return new LinkedList<>(instrs);
    }

    /**
     * Generate the assembly instructions.
     */
    public void generate() {

        // TODO also add data fragments?
        for (Fragment fragment : LinCode.fragments()) {
            if (fragment instanceof CodeFragment) {
                AsmInstrGenerator asmInstrGenerator = new AsmInstrGenerator((CodeFragment) fragment);

                for (ImcStmt stmt : ((CodeFragment) fragment).stmts()) {
                    stmt.accept(asmInstrGenerator, null);
                }
            }
        }

        instrs.add(new AsmOPER("TRAP 0,Halt,0", null, null, null));
    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("asmgen" + "|all")) {

            for (AsmInstr instr : instrs) {
                System.out.println(instr);
            }
        }
        super.close();
    }
}
