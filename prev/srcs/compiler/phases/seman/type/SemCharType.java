package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes type {@code char}.
 * 
 * @author sliva
 *
 */
public class SemCharType extends SemType {

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
		return that.actualType() instanceof SemCharType;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "CHAR");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "char";
	}
	
	@Override
	public long size() {
		return 8;
	}

}
