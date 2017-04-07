package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes type {@code void}.
 * 
 * @author sliva
 *
 */
public class SemVoidType extends SemType {

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
		return true;
	}

	@Override
	public boolean matches(SemType that) {
		return (that.actualType() instanceof SemVoidType);
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "VOID");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "void";
	}
	
	@Override
	public long size() {
		return 0;
	}

}
