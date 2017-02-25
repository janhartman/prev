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


	/** The list of keywords. */
	private final List<String> keywords = Arrays.asList(new String []{"arr", "bool", "char", "del", "do", "else", "end", "fun", "if", "int", "new", "ptr", "rec", "then", "typ", "var", "void", "where", "while"});

	/** The list of symbols. */
	private final List<String> symbols = Arrays.asList(new String []{"!", "|", "^", "&", "<", ">", "+", "-", "*", "/", "%", "$", "@", "=", ".", ",", ":", ";", "[", "]", "(", ")", "{", "}"});


	/**
	 * Constructs a new lexical analysis phase.
	 */
	public LexAn() {
		super("lexan");
		srcFileName = compiler.Main.cmdLineArgValue("--src-file-name");
		try {
			srcFile = new BufferedReader(new FileReader(srcFileName));
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

		char cPrev;

		// the current lexeme
		String lexeme = "";

		// the current term (will be determined as soon as possible)
		Term term = null;

		// the current location (updated as needed)
		int begLine = 1;
		int begColumn = 1;
		int endLine = 1;
		int endColumn = 1;

		// preceding character was a quote
		boolean quote = false;
		boolean quotedChar = false;


		// ugly (done when we read a complete symbol)
		while (true) {
			try {

				// read a character
				// reset BufferedReader if we read a character too much by reset()
				c = (char) srcFile.read();
				lexeme += c;



				// EOF, return immediately
				if (c == -1) {
					if (term == null) {
						term = Term.EOF;
					}
					else {
						lexeme = lexeme.substring(0, lexeme.length()-1);
					}
					break;
				}

				// character is enclosed in quotes
				else if (c >= 32 && c <= 126 && quote && !quotedChar) {
					quotedChar = true;
				}


				// letter
				else if ('A' <= c && 'z' >= c) {


				}

				// digit
				else if ('0' <= c && '9' >= c){

				}

				// whitespace
				else if (c == '\n' || c == '\t' || c == ' ' || c == '\r'){

				}

				// comment
				else if (c == '#') {

					// discard current line
					while (srcFile.read() != '\n') {}
					return lexify();
				}

				// quote
				else if (c == '\'') {

					// wrong - only one character can be enclosed in quotes
					if (quotedChar) {
						if (lexeme.length() > 2) {
							reportAndExit("Only one character can be enclosed in quotes at a time");
						}
						else {
							term = Term.CHARCONST;
							break;
						}
					}

					quotedChar = false;
					quote = !quote;
				}

				// symbol
				else if (symbols.contains(c + "")){

				}


				// character not allowed
				else {
					reportAndExit("Character " + c + " cannot be in a source file.");
					return null;
				}


				cPrev = c;
				srcFile.mark(100);
			}
			catch (IOException ioe) {
				Report.warning("Error reading from source file " + this.srcFileName + " during lexical analysis");
				return null;
			}


		}

		return new Symbol(term, lexeme, new Location(begLine, begColumn, endLine, endColumn);

	}

	private static void reportAndExit(String msg) {
		Report.warning(msg);
		System.exit(1);
	}

}
