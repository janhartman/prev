package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes a pointer type.
 * 
 * @author sliva
 *
 */
public class SemPtrType extends SemType {

	/** The type of a data a pointer points to. */
	public final SemType subType;

	/**
	 * Constructs a new represnetation of a pointer type.
	 * 
	 * @param subType
	 *            The type of a data a pointer points to.
	 */
	public SemPtrType(SemType subType) {
		this.subType = subType;
	}

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
		SemType actThat = that.actualType();
		if (!(actThat instanceof SemPtrType))
			return false;

		SemPtrType ptrThat = (SemPtrType) actThat;
		if ((this.subType == null) || (ptrThat.subType == null))
			return true;
		if (!this.subType.matches(ptrThat.subType))
			return false;

		return true;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "PTR");
		if (subType != null)
			subType.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		if (subType == null)
			return "ptr()";
		else
			return "ptr(" + subType.toString() + ")";
	}

	@Override
	public long size() {
		return 8;
	}

}
