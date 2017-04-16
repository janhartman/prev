package compiler;

import java.util.*;
import common.report.*;
import compiler.phases.lexan.*;
import compiler.phases.synan.*;
import compiler.phases.abstr.*;
import compiler.phases.seman.*;
import compiler.phases.frames.*;
import compiler.phases.imcgen.*;

/**
 * The compiler.
 * 
 * @author sliva
 *
 */
public class Main {

	/** All valid phases of the compiler. */
	private static final String phases = "lexan|synan|abstr|seman|frames|imcgen";

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
				cmdLine.put("--dst-file-name", cmdLine.get("--src-file-name").replaceFirst("\\.[^./]*$", "") + ".asm");
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

					compiler.phases.seman.type.SemType typeOfPrg = SemAn.isOfType().get(Abstr.absTree());
					if (!typeOfPrg.isAKindOf(compiler.phases.seman.type.SemIntType.class))
						Report.warning("The program must return a result of type int.");
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
					Abstr.absTree().accept(new ImcExprGenerator(), null);
				}
				if (cmdLine.get("--target-phase").equals("imcgen"))
					break;

				int endWarnings = Report.numOfWarnings();
				if (begWarnings != endWarnings)
					throw new Report.Error("Compilation stopped.");

			} while (false);

			Report.info("Done.");
		} catch (Report.Error __) {
		}
	}

}
