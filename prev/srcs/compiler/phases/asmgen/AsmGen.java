package compiler.phases.asmgen;

import compiler.phases.Phase;
import compiler.phases.imcgen.code.ImcStmt;
import compiler.phases.lincode.*;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author jan
 */
public class AsmGen extends Phase {

    /**
     * The hashmap of instruction lists (divided by fragments).
     */
    public static final HashMap<CodeFragment, LinkedList<AsmInstr>> instrs = new HashMap<>();

    /**
     * The variable that indicates which fragment is currently being processed.
     */
    private static CodeFragment curFrag;

    public AsmGen() {
        super("asmgen");
    }

    /**
     * Adds a new instruction to the list of instructions.
     *
     * @param instr The new instruction.
     */
    public static void add(AsmInstr instr) {
        instrs.get(curFrag).add(instr);
    }


    /**
     * Generate the assembly instructions.
     */
    public void generate() {

        for (Fragment fragment : LinCode.fragments()) {
            if (fragment instanceof CodeFragment) {
                CodeFragment codeFragment = (CodeFragment) fragment;
                AsmInstrGenerator asmInstrGenerator = new AsmInstrGenerator();
                AsmGen.curFrag = codeFragment;
                instrs.put(curFrag, new LinkedList<>());

                for (ImcStmt stmt : codeFragment.stmts()) {
                    stmt.accept(asmInstrGenerator, null);
                }
            }
        }

    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("asmgen" + "|liveness" + "|all")) {

            for (CodeFragment frag : instrs.keySet()) {
                System.out.println("% " + frag.frame.label.name);
                for (AsmInstr instr : instrs.get(frag)) {
                    System.out.println(instr);
                }
                System.out.println();
            }

        }
    }
}
