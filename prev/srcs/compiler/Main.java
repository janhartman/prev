package compiler;

import java.util.*;
import common.report.*;
import compiler.phases.finphase.FinPhase;
import compiler.phases.lexan.*;
import compiler.phases.regalloc.RegAlloc;
import compiler.phases.synan.*;
import compiler.phases.abstr.*;
import compiler.phases.seman.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;
import compiler.phases.lincode.*;
import compiler.phases.asmgen.*;
import compiler.phases.liveness.*;

/**
 * The compiler.
 * 
 * @author sliva
 *
 */
public class Main {

	/** All valid phases of the compiler. */
	private static final String phases = "lexan|synan|abstr|seman|frames|imcgen|lincode|asmgen|liveness|regalloc";

	/** Values of command line arguments. */
	private static HashMap<String, String> cmdLine = new HashMap<String, String>();

	/**
	 * Returns the value of a command line argument.
	 * 
	 * @param cmdLineArgName
	 *            The name of the command line argument.
	 * @return The value of the specified command line argument or {@code null}
	 *         if the specified command line argument has not been used.
	 */
	public static String cmdLineArgValue(String cmdLineArgName) {
		return cmdLine.get(cmdLineArgName);
	}

	/**
	 * The compiler's {@code main} method.
	 * 
	 * @param argv
	 *            Command line arguments.
	 */
	public static void main(String[] argv) {
		try {
			Report.info("This is PREV compiler:");

			// Scan the command line.
			for (int argc = 0; argc < argv.length; argc++) {
				if (argv[argc].startsWith("--")) {
					// Command-line switch.
					if (argv[argc].matches("--target-phase=(" + phases + "|all)")) {
						if (cmdLine.get("--target-phase") == null) {
							cmdLine.put("--target-phase", argv[argc].replaceFirst("^[^=]*=", ""));
							continue;
						}
					}
					if (argv[argc].matches("--logged-phase=(" + phases + "|all)")) {
						if (cmdLine.get("--logged-phase") == null) {
							cmdLine.put("--logged-phase", argv[argc].replaceFirst("^[^=]*=", ""));
							continue;
						}
					}
					if (argv[argc].matches("--xml=.*")) {
						if (cmdLine.get("--xml") == null) {
							cmdLine.put("--xml", argv[argc].replaceFirst("^[^=]*=", ""));
							continue;
						}
					}
					if (argv[argc].matches("--xsl=.*")) {
						if (cmdLine.get("--xsl") == null) {
							cmdLine.put("--xsl", argv[argc].replaceFirst("^[^=]*=", ""));
							continue;
						}
					}
					if (argv[argc].matches("--dst-file-name=.*")) {
						if (cmdLine.get("--dst-file-name") == null) {
							cmdLine.put("--dst-file-name", argv[argc].replaceFirst("^[^=]*=", ""));
							continue;
						}
					}
					Report.warning("Command line argument '" + argv[argc] + "' ignored.");
				} else {
					// Source file name.
					if (cmdLine.get("--src-file-name") == null) {
						cmdLine.put("--src-file-name", argv[argc]);
					} else {
						Report.warning("Source file '" + argv[argc] + "' ignored.");
					}
				}
			}
			if (cmdLine.get("--src-file-name") == null) {
				throw new Report.Error("Source file not specified.");
			}
			if (cmdLine.get("--dst-file-name") == null) {
				cmdLine.put("--dst-file-name", cmdLine.get("--src-file-name").replaceFirst("\\.[^./]*$", "") + ".mms");
			}
			if (cmdLine.get("--target-phase") == null) {
				cmdLine.put("--target-phase", phases.replaceFirst("^.*\\|", ""));
			}

			// Compile phase by phase.
			do {

				int begWarnings = Report.numOfWarnings();

				// Lexical analysis.
				if (cmdLine.get("--target-phase").equals("lexan")) {
					try (LexAn lexan = new LexAn()) {
						while (lexan.lexer().token != Term.EOF) {
						}
					}
					break;
				}

				// Syntax analysis.
				try (SynAn synAn = new SynAn()) {
					synAn.parser();
				}
				if (cmdLine.get("--target-phase").equals("synan"))
					break;

				// Abstract syntax.
				try (Abstr abstr = new Abstr()) {
					abstr.fromDerTree(SynAn.derTree());
				}
				if (cmdLine.get("--target-phase").equals("abstr"))
					break;

				// Semantic analysis.
				try (SemAn semAn = new SemAn()) {
					Abstr.absTree().accept(new NameChecker(new SymbTable()), null);
					Abstr.absTree().accept(new AddrChecker(), null);
					Abstr.absTree().accept(new TypeChecker(), null);
				}
				if (cmdLine.get("--target-phase").equals("seman"))
					break;

				// Frames.
				try (Frames frames = new Frames()) {
					Abstr.absTree().accept(new FrameEvaluator(), null);
				}
				if (cmdLine.get("--target-phase").equals("frames"))
					break;

				// Intermediate code generation.
				try (ImcGen imCode = new ImcGen()) {
					Abstr.absTree().accept(new ImcExprGenerator(), new Stack<Frame>());
				}
				if (cmdLine.get("--target-phase").equals("imcgen"))
					break;
				
				// Linear intermediate code.
				try (LinCode linCode = new LinCode()) {
					Abstr.absTree().accept(new Fragmenter(), null);
				}
				new Interpreter().execute();

				if (cmdLine.get("--target-phase").equals("lincode"))
					break;

				// Assembly code generation.
				try (AsmGen asmGen = new AsmGen()) {
					asmGen.generate();
				}
				if (cmdLine.get("--target-phase").equals("asmgen"))
					break;

				// repeat until register allocation succeeds
				boolean regAllocFailed = false;
				int count = 0;
				do {
					// Liveness analysis.
					try (Liveness liveness = new Liveness()) {
						Liveness.reset();
                        liveness.generate();
                    }
					if (cmdLine.get("--target-phase").equals("liveness"))
                        break;

					// Register allocation.
					try (RegAlloc regAlloc = new RegAlloc()) {
						RegAlloc.reset();
                        regAllocFailed = regAlloc.allocate();
                    }

					count++;
                    if (regAllocFailed && count > 2) {
						Report.info("Registry allocation failed - breaking loop");
						break;
					}

				} while (regAllocFailed);

				//Report.info("Allocated " + count + " times.");

				if (cmdLine.get("--target-phase").equals("regalloc"))
					break;

				try (FinPhase finPhase = new FinPhase()) {
					finPhase.finishCompilation();
				}

				int endWarnings = Report.numOfWarnings();
				if (begWarnings != endWarnings)
					throw new Report.Error("Compilation stopped.");

			} while (false);

			Report.info("Done.");
		} catch (Report.Error __) {
		}
	}

}
