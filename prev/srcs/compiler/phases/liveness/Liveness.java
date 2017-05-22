package compiler.phases.liveness;

import compiler.phases.Phase;
import compiler.phases.asmgen.AsmGen;
import compiler.phases.asmgen.AsmInstr;

import java.util.LinkedList;

/**
 * @author jan
 */
public class Liveness extends Phase {

    /**
     * The list of all interference graphs.
     */
    private static final LinkedList<InterferenceGraph> graphs = new LinkedList<>();

    public Liveness() {
        super("liveness");
    }

    /**
     * Generate the interference graphs.
     */
    public void generate() {
        GraphGenerator generator = new GraphGenerator();

        for (LinkedList<AsmInstr> instrList : AsmGen.instrs.values()) {
            graphs.add(generator.createGraph(instrList));
        }

    }

    @Override
    public void close() {
        String loggedPhase = compiler.Main.cmdLineArgValue("--logged-phase");
        if ((loggedPhase != null) && loggedPhase.matches("liveness" + "|all")) {
            for (InterferenceGraph graph : graphs) {
                System.out.println(graph);
                System.out.println();
            }
        }

    }
}
