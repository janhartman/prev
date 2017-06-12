package compiler.phases.liveness;

import compiler.phases.Phase;
import compiler.phases.asmgen.AsmGen;
import compiler.phases.lincode.CodeFragment;

import java.util.HashMap;

/**
 * @author jan
 */
public class Liveness extends Phase {

    /**
     * The list of all interference graphs.
     */
    public static final HashMap<CodeFragment, InterferenceGraph> graphs = new HashMap<>();

    public Liveness() {
        super("liveness");
    }

    /**
     * Generate the interference graphs.
     */
    public void generate() {

        for (CodeFragment fragment : AsmGen.instrs.keySet()) {
            GraphGenerator generator = new GraphGenerator(AsmGen.instrs.get(fragment), fragment.frame);
            graphs.put(fragment, generator.createGraph());
        }

    }

    public static void reset() {
        graphs.clear();
    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("liveness" + "|all")) {
            for (InterferenceGraph graph : graphs.values()) {
                graph.printAsMatrix();
                System.out.println();
            }
        }

    }
}
