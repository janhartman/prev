package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes type {@code int}.
 * 
 * @author sliva
 *
 */
public class SemIntType extends SemType {

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
		return that.actualType() instanceof SemIntType;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "INT");
		logger.endElement();
	}

	@Override
	public String toString() {
		return "int";
	}
	
	@Override
	public long size() {
		return 8;
	}

}
