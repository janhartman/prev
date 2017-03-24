package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes a type error.
 * 
 * Used to describe a type of a phrase whenever a type cannot be determined due
 * to an error in the source program.
 * 
 * @author sliva
 *
 */
public class SemErrorType extends SemType {

	@Override
	public boolean assignable() {
		return false;
	}

	@Override
	public boolean sendable() {
		return false;
	}

	@Override
	public boolean recvable() {
		return false;
	}

	@Override
	public boolean matches(SemType that) {
		return that.actualType() instanceof SemErrorType;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "ERROR");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "?";
	}

}
