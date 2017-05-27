package compiler.phases.regalloc;

import compiler.phases.Phase;
import compiler.phases.asmgen.AsmGen;
import compiler.phases.asmgen.AsmInstr;
import compiler.phases.frames.Temp;
import compiler.phases.lincode.CodeFragment;
import compiler.phases.liveness.InterferenceGraph;
import compiler.phases.liveness.Liveness;

import java.util.HashMap;

/**
 * @author jan
 */
public class RegAlloc extends Phase {

    /**
     * The temp -> register mapping.
     */
    public static final HashMap<Temp, Integer> registers = new HashMap<>();

    /**
     * The number of registers.
     */
    public static final int K = 8;

    public RegAlloc() {
        super("regalloc");
    }

    /**
     * Allocate registers to temporary values.
     */
    public void allocate() {

        for (InterferenceGraph graph : Liveness.graphs) {
            Allocator allocator = new Allocator(graph);
            allocator.simplify();
        }

    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("regalloc" + "|all")) {
            for (CodeFragment frag : AsmGen.instrs.keySet()) {
                System.out.println("% " + frag.frame.label.name);

                for (AsmInstr instr : AsmGen.instrs.get(frag)) {
                    System.out.println(instr.toString(registers));
                }
                System.out.println();
            }
        }

    }

}
