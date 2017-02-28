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

		// the previous character
		char cPrev;

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
					Report.info("found EOF at " + new Location(begLine, begColumn, endLine, endColumn));
					if (term == null) {
						term = Term.EOF;

						// TODO: temporary fix so Firefox does not report badly formed xml
						lexeme = "EOF";
						break;
					}
					else {
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();

					}
				}

				// character is enclosed in quotes
				else if (c >= 32 && c <= 126 && quote && !quotedChar) {
					Report.info("found quoted char at " + new Location(begLine, begColumn, endLine, endColumn));
					quotedChar = true;
				}


				// letter
				else if ('A' <= c && 'z' >= c) {
					Report.info("found letter " + c + " at " + new Location(begLine, begColumn, endLine, endColumn));

					if (term == null) {

					}
					else {

					}

				}

				// digit
				else if ('0' <= c && '9' >= c){
					Report.info("found digit " + c + " at " + new Location(begLine, begColumn, endLine, endColumn));
					if (term == null) {

					}
					else {

					}
				}

				// symbol
				else if (symbols.contains(c + "")){
					Report.info("found symbol " + c + " at " + new Location(begLine, begColumn, endLine, endColumn));
					if (term == null) {

					}
					else {

					}
				}

				// quote
				else if (c == '\'') {
					Report.info("found quote at " + new Location(begLine, begColumn, endLine, endColumn));

					if (term == null) {

						// TODO: use char or charconst?
						term = Term.CHARCONST;

						quotedChar = false;
						quote = !quote;
					}

					else {
						if (term.equals(Term.CHARCONST)) {
							// wrong - only one character can be enclosed in quotes
							if (lexeme.length() > 3) {
								Report.warning(lexeme);
								reportAndExit("Only one character can be enclosed in quotes at a time");
							} else {
								break;
							}
						}
						else {
							lexeme = lexeme.substring(0, lexeme.length()-1);
							srcFile.reset();
							break;
						}

					}
				}

				// whitespace
				else if (c == '\n' || c == '\t' || c == ' ' || c == '\r'){
					Report.info("found whitespace " + (int) c + " at " + new Location(begLine, begColumn, endLine, endColumn));

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
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
						break;
					}
				}

				// comment
				else if (c == '#') {
					Report.info("found comment at " + new Location(begLine, begColumn, endLine, endColumn));

					if (term == null) {

						// discard current line
						while (srcFile.read() != '\n') {}

						begLine = ++endLine;
						begColumn = 1;
						endColumn = 0;
						lexeme = "";
					}

					else {
						lexeme = lexeme.substring(0, lexeme.length()-1);
						srcFile.reset();
						break;
					}
				}


				// character not allowed
				else {
					reportAndExit("Character " + (int)c + " cannot be in a source file.");
					return null;
				}


				cPrev = c;
				endColumn++;
				srcFile.mark(100);
			}
			catch (IOException ioe) {
				Report.warning("Error reading from source file " + this.srcFileName + " during lexical analysis");
				return null;
			}


		}

		this.line = endLine;
		this.column = endColumn + 1;

		// return the symbol (term is set manually, lexeme is concatenated automatically, location updated manually)
		return new Symbol(term, lexeme, new Location(begLine, begColumn, endLine, endColumn));

	}

	private static void reportAndExit(String msg) {
		Report.warning(msg);
		System.exit(1);
	}

}
