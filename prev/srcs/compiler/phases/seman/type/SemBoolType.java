package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes type {@code bool}.
 * 
 * @author sliva
 *
 */
public class SemBoolType extends SemType {

	@Override
	public boolean assignable() {
		return true;
	}

	@Override
	public boolean sendable() {
		return true;
	}

	@Override
	public boolean recvable() {
		return true;
	}

	@Override
	public boolean matches(SemType that) {
		return that.actualType() instanceof SemBoolType;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "BOOL");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "bool";
	}

}
