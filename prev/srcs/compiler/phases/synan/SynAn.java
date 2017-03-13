package compiler.phases.synan;

import common.report.*;
import compiler.phases.*;
import compiler.phases.lexan.*;
import compiler.phases.synan.dertree.*;

/**
 * Syntax analysis.
 * 
 * @author sliva
 *
 */
public class SynAn extends Phase {

	/** The constructed derivation tree. */
	private static DerTree derTree = null;

	/**
	 * Returns the constructed derivation tree.
	 * 
	 * @return The constructed derivation tree.
	 */
	public static DerTree derTree() {
		return derTree;
	}

	/** The lexical analyzer used by this syntax analyzer. */
	private final LexAn lexAn;

	/**
	 * Constructs a new syntax analysis phase.
	 */
	public SynAn() {
		super("synan");
		lexAn = new LexAn();
	}

	/** The lookahead buffer (of length 1). */
	private Symbol currSymb = null;

	/**
	 * Appends the current symbol in the lookahead buffer to the node of the
	 * derivation tree that is currently being expanded by the parser.
	 * 
	 * Hence, the statement {@code currSymb = skip(node);} can be used for (a)
	 * appending the current symbol in the lookahead buffer {@code currSymb} to
	 * the node of the derivation tree and (b) eliminating this symbol from the
	 * lookahead buffer.
	 * 
	 * @param node
	 *            The node of the derivation tree currently being expanded by
	 *            the parser.
	 * @return {@code null}.
	 */
	private Symbol skip(DerNode node) {
		if (currSymb != null)
			node.add(new DerLeaf(currSymb));
		return null;
	}

	/**
	 * The parser.
	 * 
	 * This method returns the derivation tree of the program in the source
	 * file. It calls method {@link #parseSource()} that starts a recursive
	 * descent parser implementation of an LL(1) parsing algorithm.
	 * 
	 * @return The derivation tree.
	 */
	public DerTree parser() {
		derTree = parseSource();
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		if (currSymb.token != Term.EOF)
			throw new Report.Error(currSymb, "Unexpected '" + currSymb + "' at the end of a program.");
		derTree.accept(new DerLogger(logger), null);
		return derTree;
	}

	@Override
	public void close() {
		lexAn.close();
		super.close();
	}

	// --- PARSER ---

	private DerNode parseSource() {
		DerNode node = new DerNode(Nont.Source);
		node.add(parseExpr());
		return node;
	}

}
