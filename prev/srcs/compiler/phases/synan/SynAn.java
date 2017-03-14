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

	public Symbol check(DerNode node, Term token) {
		if (currSymb.token != token)
			throw new Report.Error(currSymb.location(), "Unexpected " + token);
		else
			node.add(new DerLeaf(currSymb));
		return null;

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

			// expr → expr1 expr0
			// literal
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
			case LBRACKET:
			// unary
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr1());
				node.add(parseExpr0());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr");
		}
		return node;
	}

	private DerNode parseExpr0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr0);

		switch (currSymb.token) {

			// expr0 → xior expr1 expr0
			case XOR:
			case IOR:
				node.add(parseExpr1());
				node.add(parseExpr0());
				break;

			// expr0 → ε
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr0");

		}
		return node;
	}

	private DerNode parseExpr1() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr1);

		switch (currSymb.token) {
			// expr1 → expr2 expr10
			// literal
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
			case LBRACKET:

				// unary
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr2());
				node.add(parseExpr10());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr1");

		}
		return node;
	}

	private DerNode parseExpr10() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr10);

		switch (currSymb.token) {

			case AND:
				currSymb = skip(node);
				node.add(parseExpr2());
				node.add(parseExpr10());
				break;

			// expr10 → ε
			case XOR:
			case IOR:
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr10");

		}
		return node;
	}

	private DerNode parseExpr2() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr2);

		switch (currSymb.token) {
			// expr2 → expr3 expr20
			// literal
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
			case LBRACKET:
				// unary
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr3());
				node.add(parseExpr20());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr2");

		}
		return node;
	}

	private DerNode parseExpr20() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr20);

		switch (currSymb.token) {

			// expr20 → cmp expr3 expr20
			case LTH:
			case GTH:
			case LEQ:
			case GEQ:
			case EQU:
			case NEQ:
				currSymb = skip(node);
				node.add(parseExpr3());
				node.add(parseExpr20());
				break;

			// expr0 → ε
			case XOR:
			case IOR:
			case AND:
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr20");

		}
		return node;
	}

	private DerNode parseExpr3() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr3);

		switch (currSymb.token) {
			// expr3 → expr4 expr30
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
			case LBRACKET:
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr4());
				node.add(parseExpr30());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr3");

		}
		return node;
	}

	private DerNode parseExpr30() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr30);

		switch (currSymb.token) {

			// expr30 → plusminus expr4 expr30
			case ADD:
			case SUB:
				currSymb = skip(node);
				node.add(parseExpr4());
				node.add(parseExpr30());
				break;

			// expr30 → ε
			case XOR:
			case IOR:
			case LTH:
			case GTH:
			case LEQ:
			case GEQ:
			case EQU:
			case NEQ:
			case AND:
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr30");

		}
		return node;
	}

	private DerNode parseExpr4() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr4);

		switch (currSymb.token) {
			// expr4 → expr5 expr40
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NEW:
			case DEL:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
			case LBRACKET:
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				node.add(parseExpr5());
				node.add(parseExpr40());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr4");

		}
		return node;
	}

	private DerNode parseExpr40() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr40);

		switch (currSymb.token) {

			// expr40 → multdivmod expr5 expr40
			case MUL:
			case DIV:
			case MOD:
				currSymb = skip(node);
				node.add(parseExpr5());
				node.add(parseExpr40());
				break;

			// expr40 → ε
			case ADD:
			case SUB:
			case XOR:
			case IOR:
			case LTH:
			case GTH:
			case LEQ:
			case GEQ:
			case EQU:
			case NEQ:
			case AND:
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr40");

		}
		return node;
	}

	private DerNode parseExpr5() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr5);

		switch (currSymb.token) {

			// expr5 → expr6
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
				node.add(parseExpr6());
				break;

			// expr5 → [ type ] expr6
			case LBRACKET:
				currSymb = skip(node);
				node.add(parseType());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RBRACKET);
				node.add(parseExpr6());
				break;

			// expr5 → new type
			case NEW:
				currSymb = skip(node);
				node.add(parseType());
				break;

			// expr5 → del expr6
			case DEL:

			// expr5 → unop expr6
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
				currSymb = skip(node);
				node.add(parseExpr6());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr5");

		}
		return node;
	}

	private DerNode parseExpr6() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr6);

		switch (currSymb.token) {

			// expr6 → expr7 expr60
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case IDENTIFIER:
			case LPARENTHESIS:
			case LBRACE:
				node.add(parseExpr7());
				node.add(parseExpr60());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr6");

		}
		return node;
	}

	private DerNode parseExpr60() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr60);

		switch (currSymb.token) {

			// expr60 → dot identifier expr60
			case DOT:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.IDENTIFIER);
				node.add(parseExpr60());
				break;

			// expr60 → [ expr ] expr60
			case LBRACKET:
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RBRACKET);
				node.add(parseExpr60());
				break;

			// expr60 → ε
			case MUL:
			case DIV:
			case MOD:
			case ADD:
			case SUB:
			case XOR:
			case IOR:
			case AND:
			case LTH:
			case GTH:
			case LEQ:
			case GEQ:
			case EQU:
			case NEQ:
			case RBRACKET:
			case RPARENTHESIS:
			case RBRACE:
			case COLON:
			case ASSIGN:
			case THEN:
			case DO:
			case END:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr60");

		}
		return node;
	}

	private DerNode parseExpr7() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Expr7);

		switch (currSymb.token) {
			// expr7 → idenexprmulti
			case IDENTIFIER:
				node.add(parseIdenExprMulti());
				break;

			// expr7 → ( expr )
			case LPARENTHESIS:
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RPARENTHESIS);
				break;

			// expr7 → { stmtmulti : exprwhere }
			case LBRACE:
				currSymb = skip(node);
				node.add(parseStmtMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.COLON);
				node.add(parseExprWhere());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RBRACE);
				break;

			// expr7 → literal
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
				currSymb = skip(node);
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExpr7");

		}
		return node;
	}


	private DerNode parseExprWhere() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.ExprWhere);

		switch (currSymb.token) {

			// exprwhere → expr exprwhere0
			case LBRACE:
			case LPARENTHESIS:
			case LBRACKET:
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
			case NEW:
			case DEL:
			case IDENTIFIER:
				node.add(parseExpr());
				node.add(parseExprWhere0());
				break;


			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExprWhere");

		}
		return node;
	}

	private DerNode parseExprWhere0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.ExprWhere0);

		switch (currSymb.token) {

			// exprwhere0 → ε
			case RBRACE:
				break;

			// exprwhere0 → where declmulti
			case WHERE:
				currSymb = skip(node);
				node.add(parseDeclMulti());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExprWhere0");

		}
		return node;
	}

	private DerNode parseExprMulti() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.ExprMulti);

		switch (currSymb.token) {

			// exprmulti → expr exprmulti0
			case LBRACE:
			case LPARENTHESIS:
			case LBRACKET:
			case BOOLCONST:
			case CHARCONST:
			case INTCONST:
			case PTRCONST:
			case VOIDCONST:
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
			case NEW:
			case DEL:
			case IDENTIFIER:
				node.add(parseExpr());
				node.add(parseExprMulti0());
				break;

			// exprmulti → ε
			case RPARENTHESIS:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExprMulti");

		}
		return node;
	}

	private DerNode parseExprMulti0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.ExprMulti0);

		switch (currSymb.token) {

			// exprmulti0 → , expr exprmulti0
			case COMMA:
				currSymb = skip(node);
				node.add(parseExpr());
				node.add(parseExprMulti0());
				break;

			// exprmulti0 → ε
			case RPARENTHESIS:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExprMulti0");

		}
		return node;
	}

	private DerNode parseExprAssign() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.ExprAssign);

		switch (currSymb.token) {

			// exprassign → ε
			case RBRACE:
				break;

			// exprassign → = expr
			case ASSIGN:
				currSymb = skip(node);
				node.add(parseExpr());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseExprAssign");

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
				currSymb = check(node, Term.LBRACKET);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RBRACKET);
				node.add(parseType());
				break;

			// type → rec ( identypemulti )
			case REC:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.LPARENTHESIS);
				node.add(parseIdenTypeMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RPARENTHESIS);
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
				currSymb = check(node, Term.THEN);
				node.add(parseStmtMulti());
				node.add(parseStmtElse());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.END);
				break;

			// stmt → while expr do stmtmulti end
			case WHILE:
				currSymb = skip(node);
				node.add(parseExpr());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.DO);
				node.add(parseStmtMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.END);
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmt");

		}
		return node;
	}

	private DerNode parseStmt0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.Stmt0);

		switch (currSymb.token) {

			// stmt0 → = expr
			case ASSIGN:
				currSymb = skip(node);
				node.add(parseExpr());
				break;

			// stmt0 → ε
			case END:
			case SEMIC:
			case ELSE:
			case COLON:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmt0");

		}
		return node;
	}

	private DerNode parseStmtMulti() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.StmtMulti);

		switch (currSymb.token) {

			// stmtmulti → stmt stmtmulti0
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
			case NOT:
			case ADD:
			case SUB:
			case MEM:
			case VAL:
			case IF:
			case WHILE:
				node.add(parseStmt());
				node.add(parseStmtMulti0());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmtMulti");

		}
		return node;
	}

	private DerNode parseStmtMulti0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.StmtMulti0);

		switch (currSymb.token) {

			// stmtmulti0 → ε
			case COLON:
			case ELSE:
			case END:
				break;

			// stmtmulti0 → ; stmtmulti
			case SEMIC:
				currSymb = skip(node);
				node.add(parseStmtMulti());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmtMulti0");

		}
		return node;
	}

	private DerNode parseStmtElse() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.StmtElse);

		switch (currSymb.token) {
			// stmtelse → else stmtmulti
			case ELSE:
				currSymb = skip(node);
				node.add(parseStmtMulti());
				break;

			// stmtelse → ε
			case END:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseStmtElse");

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
				currSymb = check(node, Term.IDENTIFIER);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.COLON);
				node.add(parseType());
				break;

			// decl → var identifier : type
			case VAR:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.IDENTIFIER);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.COLON);
				node.add(parseType());
				break;

			// decl → fun identifier ( identypemulti ) : type exprassign
			case FUN:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.IDENTIFIER);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.LPARENTHESIS);
				node.add(parseIdenTypeMulti());
				currSymb = check(node, Term.RPARENTHESIS);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.COLON);
				node.add(parseType());
				node.add(parseExprAssign());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseDecl");

		}
		return node;
	}

	private DerNode parseDeclMulti() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.DeclMulti);

		switch (currSymb.token) {
			// declmulti → decl declmulti0
			case TYP:
			case VAR:
			case FUN:
				node.add(parseDecl());
				node.add(parseDeclMulti0());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseDeclMulti");

		}
		return node;
	}

	private DerNode parseDeclMulti0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.DeclMulti0);

		switch (currSymb.token) {

			// declmulti0 → ; declmulti
			case SEMIC:
				currSymb = skip(node);
				node.add(parseDeclMulti());
				break;

			// declmulti0 → ε
			case RBRACE:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseDeclMulti0");

		}
		return node;
	}

	private DerNode parseIdenTypeMulti() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.IdenTypeMulti);

		switch (currSymb.token) {

			// identypemulti → identifier : type identypemulti0
			case IDENTIFIER:
				currSymb = skip(node);
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.COLON);
				node.add(parseType());
				node.add(parseIdenTypeMulti0());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseIdenTypeMulti");

		}
		return node;
	}

	private DerNode parseIdenTypeMulti0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.IdenTypeMulti0);

		switch (currSymb.token) {
			// identypemulti0 → , identypemulti
			case COMMA:
				currSymb = skip(node);
				node.add(parseIdenTypeMulti());
				break;

			// identypemulti0 → ε
			case RPARENTHESIS:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseIdenTypeMulti0");

		}
		return node;
	}

	private DerNode parseIdenExprMulti() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.IdenExprMulti);

		switch (currSymb.token) {

			// idenexprmulti → identifier idenexprmulti0
			case IDENTIFIER:
				currSymb = skip(node);
				node.add(parseIdenExprMulti0());
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseIdenExprMulti");

		}
		return node;
	}

	private DerNode parseIdenExprMulti0() {
		currSymb = currSymb == null ? lexAn.lexer() : currSymb;
		DerNode node = new DerNode(Nont.IdenExprMulti0);

		switch (currSymb.token) {

			// idenexprmulti0 → ( exprmulti )
			case LPARENTHESIS:
				currSymb = skip(node);
				node.add(parseExprMulti());
				currSymb = currSymb == null ? lexAn.lexer() : currSymb;
				currSymb = check(node, Term.RPARENTHESIS);
				break;

			// idenexprmulti0 → ε
			case IDENTIFIER:
			case COLON:
			case RBRACE:
			case XOR:
			case IOR:
			case RBRACKET:
			case RPARENTHESIS:
			case ASSIGN:
			case END:
			case DO:
			case THEN:
			case WHERE:
			case COMMA:
			case ELSE:
			case SEMIC:
			case EOF:
				break;

			default:
				throw new Report.Error(currSymb.location(), "Unrecognized symbol " + currSymb + " in parseIdenExprMulti0");

		}
		return node;
	}

}
