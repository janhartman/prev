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

	private DerNode parseExpr() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr);
		switch (currSymb.token) {

			// expr → { stmtmulti : exprwhere } expr0
			case LBRACE:
				currSymb = skip(node);
				node.add(parseStmtMulti());
				currSymb = skip(node);
				node.add(parseExprWhere());
				currSymb = skip(node);
				node.add(parseExpr0());
				currSymb = skip(node);
				break;

			// expr → literal
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
				currSymb = skip(node);
				break;

			// expr → expr1 expr0
			case LBRACKET:
			case NEW:
			case DEL:
			case LPARENTHESIS:

			// unary
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				parseExpr1();
				parseExpr0();
				break;

			// expr → idenexprmulti expr0
			case IDENTIFIER:
				parseIdenExprMulti();
				parseExpr0();
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr");
		}
		return node;
	}

	private DerNode parseType() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Type);

		switch (currSymb.token) {

			// type → identifier
			case IDENTIFIER:
			case BOOL:
			case VOID:
			case CHAR:
			case INT:
				currSymb = skip(node);
				break;

			// type → ptr type
			case PTR:
				currSymb = skip(node);
				node.add(parseType());
				break;

			// type → arr [ expr ] type
			case ARR:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseType());
				break;

			// type → rec ( identypemulti )
			case REC:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseIdenTypeMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseType");

		}
		return node;
	}

	private DerNode parseStmt() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Stmt);

		switch (currSymb.token) {

			// stmt → expr stmt0
			case LBRACE:
			case LBRACKET:
			case LPARENTHESIS:
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr());
				node.add(parseStmt0());
				break;

			// stmt → if expr then stmtmulti stmtelse end
			case IF:
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseStmtMulti());
				node.add(parseStmtElse());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				break;

			// stmt → while expr do stmtmulti end
			case WHILE:
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseStmtMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmt");

		}
		return node;
	}

	private DerNode parseDecl() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Decl);

		switch (currSymb.token) {
			// decl → typ identifier : type
			case TYP:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseType());
				break;

			// decl → var identifier : type
			case VAR:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseType());
				break;

			// decl → fun identifier ( identypemulti ) : type exprassign
			case FUN:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseIdenTypeMulti());
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = skip(node);
				node.add(parseType());
				node.add(parseExprAssign());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseDecl");

		}
		return node;
	}



}
