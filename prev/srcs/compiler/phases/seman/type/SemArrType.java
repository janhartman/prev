package compiler.phases.seman.type;

import common.logger.*;

/**
 * Describes an array type.
 * 
 * @author sliva
 * 
 */
public class SemArrType extends SemType {

	/** The number of elements in an array. */
	public final long len;

	/** The type of an array element. */
	public final SemType elemType;

	/**
	 * Constructs a new representation of an array type.
	 * 
	 * @param len
	 *            The number of elements in an array.
	 * @param elemType
	 *            The type of an array element.
	 */
	public SemArrType(long len, SemType elemType) {
		this.len = len;
		this.elemType = elemType;
	}

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
		SemType actThat = that.actualType();
		if (!(actThat instanceof SemArrType))
			return false;

		SemArrType arrThat = (SemArrType) actThat;
		if (this.len != arrThat.len)
			return false;
		if (!this.elemType.matches(arrThat.elemType))
			return false;

		return true;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "ARR(" + len + ")");
		logger.addAttribute("len", Long.toString(len));
		elemType.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		return "arr(" + len + "," + elemType.toString() + ")";
	}

}
