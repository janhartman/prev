package compiler.phases.regalloc;

import compiler.phases.Phase;
import compiler.phases.asmgen.AsmGen;
import compiler.phases.asmgen.AsmInstr;
import compiler.phases.frames.Temp;
import compiler.phases.lincode.CodeFragment;
import compiler.phases.liveness.Liveness;

import java.util.HashMap;

/**
 * @author jan
 */
public class RegAlloc extends Phase {

    /**
     * The temp -> register mapping.
     */
    public static final HashMap<CodeFragment, HashMap<Temp, Integer>> registers = new HashMap<>();

    /**
     * The number of registers.
     */
    public static final int K = 2;

    public RegAlloc() {
        super("regalloc");
    }

    /**
     * Allocate registers to temporary values.
     */
    public boolean allocate() {
        boolean spill = false;

        for (CodeFragment fragment : Liveness.graphs.keySet()) {
            Allocator allocator = new Allocator(Liveness.graphs.get(fragment), AsmGen.instrs.get(fragment), fragment.frame);
            allocator.simplify();
            registers.put(fragment, allocator.mapping());
            spill = spill || allocator.spilled();
        }

        return spill;
    }

    public static void reset() {
        registers.clear();
    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("regalloc" + "|all")) {
            for (CodeFragment frag : AsmGen.instrs.keySet()) {
                System.out.println("% " + frag.frame.label.name);

                for (AsmInstr instr : AsmGen.instrs.get(frag)) {
                    System.out.println(instr.toString(registers.get(frag)));
                }
                System.out.println();
            }
        }

    }

}
