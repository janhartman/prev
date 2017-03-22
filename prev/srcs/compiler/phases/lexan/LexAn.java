package compiler.phases.lexan;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import common.report.*;
import compiler.phases.*;

/**
 * Lexical analysis.
 *
 * @author sliva
 *
 */
public class LexAn extends Phase {

	/** The name of the source file. */
	private final String srcFileName;

	/** The source file reader. */
	private final BufferedReader srcFile;

	/** The current line. */
	private int line;

	/** The current column. */
	private int column;

	/** The list of keywords. */
	private final List<String> keywords = Arrays.asList(new String []{"arr", "bool", "char", "del", "do", "else", "end", "fun", "if", "int", "new", "ptr", "rec", "then", "typ", "var", "void", "where", "while"});

	/** The list of symbols. */
	private final List<String> symbols = Arrays.asList(new String []{"!", "|", "^", "&", "<", ">", "+", "-", "*", "/", "%", "$", "@", "=", ".", ",", ":", ";", "[", "]", "(", ")", "{", "}"});

	/** The list of constants. */
	private final List<String> constants = Arrays.asList(new String []{"none", "true", "false", "void", "null"});


	/**
	 * Constructs a new lexical analysis phase.
	 */
	public LexAn() {
		super("lexan");
		this.srcFileName = compiler.Main.cmdLineArgValue("--src-file-name");
		this.line = 1;
		this.column = 1;

		try {
			this.srcFile = new BufferedReader(new FileReader(srcFileName));
		} catch (IOException ___) {
			throw new Report.Error("Cannot open source file '" + srcFileName + "'.");
		}
	}

	/**
	 * The lexer.
	 *
	 * This method returns the next symbol from the source file. To perform the
	 * lexical analysis of the entire source file, this method must be called
	 * until it returns EOF. This method calls {@link #lexify()}, logs its
	 * result if requested, and returns it.
	 *
	 * @return The next symbol from the source file.
	 */
	public Symbol lexer() {
		Symbol symb = lexify();
		symb.log(logger);
		return symb;
	}

	@Override
	public void close() {
		try {
			srcFile.close();
		} catch (IOException ___) {
			Report.warning("Cannot close source file '" + this.srcFileName + "'.");
		}
		super.close();
	}

	// --- LEXER ---

	/**
	 * Performs the lexical analysis of the source file.
	 *
	 * This method returns the next symbol from the source file. To perform the
	 * lexical analysis of the entire source file, this method must be called
	 * until it returns EOF.
	 *
	 * @return The next symbol from the source file or EOF if no symbol is
	 *         available any more.
	 */
	private Symbol lexify() {

		// the current character
		char c;

		// the current lexeme
		String lexeme = "";

		// the current term (will be determined as soon as possible)
		Term term = null;

		// the current location (updated as needed)
		int begLine = this.line;
		int begColumn = this.column;
		int endLine = begLine;
		int endColumn = begColumn;

		// preceding character was a quote
		boolean quote = false;

		// a quoted character has occurred
		boolean quotedChar = false;

		// can the word be only an identifier
		boolean onlyIdentifier = false;

		// if a keyword / constant has been encountered
		boolean keywordOrConstant = false;

		// break loop when a complete symbol is read
		while (true) {
			try {

				// read a character
				// reset BufferedReader if we read a character too much by reset()
				int i = srcFile.read();
				c = (char) i;
				lexeme += c;


				// EOF
				if (i == -1) {
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "EOF");
					if (term == null) {
						term = Term.EOF;
						lexeme = "";
					}
					else {
						endColumn--;
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
					}
					break;
				}

				// character is enclosed in quotes
				else if (quote && !quotedChar) {
					if (c >= 32 && c <= 126) {
						//Report.info(new Location(begLine, begColumn, endLine, endColumn), "quoted char " + c);
						quotedChar = true;
					}
					else {
						throw new Report.Error(new Location(endLine, endColumn), "Character " + c + " with code " + i + " cannot be enclosed in quotes");
					}
				}

				// letter
				else if (Character.isLetter(c) && c < 123) {
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "letter " + c);

					if (term == null) {
						term = Term.IDENTIFIER;
					}
					else {
						if (!onlyIdentifier && term == Term.IDENTIFIER) {

							//check if lexeme is keyword or constant
							if (keywords.contains(lexeme)) {
								switch (lexeme) {
									case "arr":
										term = Term.ARR;
										break;
									case "bool":
										term = Term.BOOL;
										break;
									case "char":
										term = Term.CHAR;
										break;
									case "del":
										term = Term.DEL;
										break;
									case "do":
										term = Term.DO;
										break;
									case "else":
										term = Term.ELSE;
										break;
									case "end":
										term = Term.END;
										break;
									case "fun":
										term = Term.FUN;
										break;
									case "if":
										term = Term.IF;
										break;
									case "int":
										term = Term.INT;
										break;
									case "new":
										term = Term.NEW;
										break;
									case "ptr":
										term = Term.PTR;
										break;
									case "rec":
										term = Term.REC;
										break;
									case "then":
										term = Term.THEN;
										break;
									case "typ":
										term = Term.TYP;
										break;
									case "var":
										term = Term.VAR;
										break;
									case "void":
										term = Term.VOID;
										break;
									case "where":
										term = Term.WHERE;
										break;
									case "while":
										term = Term.WHILE;
										break;
								}
								keywordOrConstant = true;
							}
							else if (constants.contains(lexeme)) {
								switch (lexeme) {
									case "none":
										term = Term.VOIDCONST;
										break;
									case "true":
									case "false":
										term = Term.BOOLCONST;
										break;
									case "null":
										term = Term.PTRCONST;
										break;
								}
								keywordOrConstant = true;
							}

						}
						// lexeme was previously identified as keyword or constant, but is now an identifier
						else if (keywordOrConstant) {
							term = Term.IDENTIFIER;
							keywordOrConstant = false;
							onlyIdentifier = true;
						}
						else if (term == Term.IDENTIFIER) {

						}
						else {
							endColumn--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							srcFile.reset();
							break;
						}
					}

				}

				// digit
				else if (Character.isDigit(c)){
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "digit " + c);
					if (term == null) {
						term = Term.INTCONST;
					}
					else {
						if (term == Term.IDENTIFIER) {
							onlyIdentifier = true;
						}

						else if (term == Term.INTCONST) {
							term = Term.INTCONST;
						}

						else if (keywordOrConstant) {
							keywordOrConstant = false;
							onlyIdentifier = true;
							term = Term.IDENTIFIER;
						}

						else {
							endColumn--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							srcFile.reset();
							break;
						}

					}
				}

				// symbol
				else if (symbols.contains(c + "")){
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "symbol " + c );
					if (term == null) {
						boolean isTwoPartSymbol = false;
						switch (c) {
							case '!':
								isTwoPartSymbol = true;
								term = Term.NOT;
								break;
							case '|':
								term = Term.IOR;
								break;
							case '^':
								term = Term.XOR;
								break;
							case '&':
								term = Term.AND;
								break;
							case '=':
								isTwoPartSymbol = true;
								term = Term.ASSIGN;
								break;
							case '<':
								isTwoPartSymbol = true;
								term = Term.LTH;
								break;
							case '>':
								isTwoPartSymbol = true;
								term = Term.GTH;
								break;
							case '+':
								term = Term.ADD;
								break;
							case '-':
								term = Term.SUB;
								break;
							case '*':
								term = Term.MUL;
								break;
							case '/':
								term = Term.DIV;
								break;
							case '%':
								term = Term.MOD;
								break;
							case '$':
								term = Term.MEM;
								break;
							case '@':
								term = Term.VAL;
								break;
							case '.':
								term = Term.DOT;
								break;
							case ',':
								term = Term.COMMA;
								break;
							case ':':
								term = Term.COLON;
								break;
							case ';':
								term = Term.SEMIC;
								break;
							case '[':
								term = Term.LBRACKET;
								break;
							case ']':
								term = Term.RBRACKET;
								break;
							case '(':
								term = Term.LPARENTHESIS;
								break;
							case ')':
								term = Term.RPARENTHESIS;
								break;
							case '{':
								term = Term.LBRACE;
								break;
							case '}':
								term = Term.RBRACE;
								break;
						}

						if (!isTwoPartSymbol) {
							break;
						}

					}
					else {
						if ((term == Term.ASSIGN || term == Term.NOT || term == Term.LTH || term == Term.GTH) && lexeme.length() == 2 && c == '=')  {
							if (term == Term.ASSIGN) {
								term = Term.EQU;
								break;
							}
							else if (term == Term.NOT){
								term = Term.NEQ;
								break;
							}
							else if (term == Term.LTH) {
								term = Term.LEQ;
								break;
							}
							else if (term == Term.GTH) {
								term = Term.GEQ;
								break;
							}
						}
						else {
							endColumn--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							srcFile.reset();
							break;
						}
					}
				}

				// underscore
				else if (c == '_') {
					if (term == null) {
						term = Term.IDENTIFIER;
					}
					else {
						if (term == Term.IDENTIFIER) {
							onlyIdentifier = true;
						}
						
						else if (keywordOrConstant) {
							onlyIdentifier = true;
							keywordOrConstant = false;
							term = Term.IDENTIFIER;
						}

						else {
							endColumn--;
							lexeme = lexeme.substring(0, lexeme.length()-1);
							srcFile.reset();
							break;
						}
					}
				}

				// quote
				else if (c == '\'') {
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "quote");

					if (term == null) {
						term = Term.CHARCONST;
						quotedChar = false;
						quote = !quote;
					}

					else if (term == Term.CHARCONST) {
						quote = false;
						// wrong - only one character can be enclosed in quotes
						if (lexeme.length() > 3) {
							throw new Report.Error(new Location(endLine, endColumn), "Only one character can be enclosed in quotes at a time");
						} else {
							break;
						}
					}
					else {
						endColumn--;
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
						break;
					}


				}

				// whitespace
				else if (c == '\n' || c == '\t' || c == ' ' || c == '\r'){
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "whitespace " + (int) c);

					if (term == null) {
						if (c == '\n') {
							begLine = ++endLine;
							begColumn = 1;
							endColumn = 0;
						}
						else {
							begColumn = endColumn + 1;
						}
						lexeme = "";

					}
					else {
						endColumn--;
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
						break;
					}
				}

				// comment
				else if (c == '#') {
					//Report.info(new Location(begLine, begColumn, endLine, endColumn), "comment" );

					if (term == null) {

						// discard current line
						int j;
						while ((j = srcFile.read()) != '\n') {
							if (j > 127) {
								throw new Report.Error(new Location(endLine, endColumn), "Character " + (char)j + " with code " + j + " cannot be in a source file");
							}
							endColumn++;
						}

						begLine = ++endLine;
						begColumn = 1;
						endColumn = 0;
						lexeme = "";
					}

					else {
						endColumn--;
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
						break;
					}
				}


				// character not allowed
				else {
					throw new Report.Error(new Location(endLine, endColumn), "Character " + c + " with code " + (int)c + " cannot be in a source file.");
				}

				endColumn++;
				srcFile.mark(100);
			}
			catch (IOException ioe) {
				Report.warning("Error reading from source file " + this.srcFileName + " during lexical analysis");
				return null;
			}


		}

		if (quotedChar && quote) {
			throw new Report.Error(new Location(endLine, endColumn), "Unterminated character constant");
		}

		//set line and column for next call of lexify
		//endColumn = begColumn + lexeme.length() - 1;
		this.line = endLine;
		this.column = endColumn + 1;

		// return the symbol (term is set manually, lexeme is concatenated automatically, location updated manually)
		Symbol symbol = new Symbol(term, lexeme, new Location(begLine, begColumn, endLine, endColumn));
		//Report.info("Returning symbol " + symbol.stringify());
		return symbol;

	}

}
