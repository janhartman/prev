package compiler.phases.abstr;

import java.util.*;
import common.report.*;

/**
 * An attribute of the abstract syntax tree node.
 * 
 * @author sliva
 *
 * @param <Node>
 *            A node the attribute is associated with.
 * @param <Value>
 *            The value of the attribute.
 */
public class AbsAttribute<Node, Value> {

	/** Mapping of nodes to attribute values. */
	private HashMap<Node, Value> mapping;

	/** Whether this attribute's values can no longer be modified or not. */
	private boolean lock;

	/** Constructs a new attribute. */
	public AbsAttribute() {
		mapping = new HashMap<Node, Value>();
		lock = false;
	}

	/**
	 * Associates an attribute value with the specified abstract syntax tree
	 * node.
	 * 
	 * @param node
	 *            The specified abstract syntax tree node.
	 * @param value
	 *            The attribute value.
	 * @return The value itself.
	 */
	public Value put(Node node, Value value) {
		if (lock)
			throw new Report.InternalError();
		mapping.put(node, value);
		return value;
	}

	/**
	 * Returns an attribute value associated with the specified abstract syntax
	 * tree node.
	 * 
	 * @param node
	 *            The specified abstract syntax tree node.
	 * @return The attribute value.
	 */
	public Value get(Node node) {
		return mapping.get(node);
	}

	/**
	 * Prevents further modification of this attributte's values.
	 */
	public void lock() {
		lock = true;
	}

}
