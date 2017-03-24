package compiler.phases.seman.type;

import java.util.*;
import common.logger.*;

/**
 * Describes a record type.
 * 
 * @author sliva
 *
 */
public class SemRecType extends SemType {

	/** The names of all components. */
	private final Vector<String> compNames;

	/** The types of all components. */
	private final Vector<SemType> compTypes;

	/**
	 * Constructs a new representation of a record type.
	 * 
	 * @param compNames
	 *            The names of all components.
	 * @param compTypes
	 *            The types of all components.
	 */
	public SemRecType(Vector<String> compNames, Vector<SemType> compTypes) {
		this.compNames = new Vector<String>(compNames);
		this.compTypes = new Vector<SemType>(compTypes);
	}

	/**
	 * Returns the names of all components.
	 * 
	 * @return The names of all components.
	 */
	public Vector<String> compNames() {
		return new Vector<String>(compNames);
	}

	/**
	 * Returns the types of all components.
	 * 
	 * @return The types of all components.
	 */
	public Vector<SemType> compTypes() {
		return new Vector<SemType>(compTypes);
	}

	/**
	 * Returns the name of the specified component.
	 * 
	 * @param index
	 *            The index of a component.
	 * @return The name of the specified component.
	 */
	public String compName(int index) {
		return compNames.elementAt(index);
	}

	/**
	 * Returns the type of the specified component.
	 * 
	 * @param index
	 *            The index of a component.
	 * @return The type of the specified component,
	 */
	public SemType compType(int index) {
		return compTypes.elementAt(index);
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
		if (!(actThat instanceof SemRecType))
			return false;

		SemRecType recThat = (SemRecType) actThat;
		if (this.compTypes.size() != recThat.compTypes.size())
			return false;
		for (int comp = 0; comp < this.compTypes.size(); comp++)
			if (!this.compTypes.elementAt(comp).matches(recThat.compTypes.elementAt(comp)))
				return false;

		return true;
	}

	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("type");
		logger.addAttribute("label", "REC");
		for (SemType compType : compTypes)
			compType.log(logger);
		logger.endElement();
	}

	@Override
	public String toString() {
		StringBuffer name = new StringBuffer();
		name.append("rec");
		name.append("(");
		for (int comp = 0; comp < compTypes.size(); comp++) {
			if (comp > 0)
				name.append(",");
			name.append(compTypes.elementAt(comp).toString());
		}
		name.append(")");
		return name.toString();
	}

}
