package compiler.phases.lexan;

import common.logger.*;
import common.report.*;

/**
 * A symbol recognized by a lexer and passed to the parser.
 * 
 * @author sliva
 *
 */
public class Symbol implements Locatable, Loggable {

	/** The token. */
	public final Term token;

	/** The lexeme. */
	public final String lexeme;

	/** The location within a source file. */
	private Location location;

	/**
	 * Constructs a new symbol.
	 * 
	 * @param token The token.
	 * @param lexeme The lexeme.
	 * @param location The location within a source file.
	 */
	public Symbol(Term token, String lexeme, Locatable location) {
		this.token = token;
		this.lexeme = lexeme;
		this.location = location.location();
	}

	@Override
	public Location location() {
		return location;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("term");
		logger.addAttribute("token", token.toString());
		logger.addAttribute("lexeme", lexeme);
		location.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		return lexeme;
	}

}
