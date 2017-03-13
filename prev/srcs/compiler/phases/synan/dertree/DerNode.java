package compiler.phases.synan.dertree;

import java.util.*;
import common.report.*;
import compiler.phases.synan.*;

/**
 * An internal node of the derivation tree.
 * 
 * @author sliva
 *
 */
public class DerNode extends DerTree {

	/** The CFG nonterminal this node represents. */
	public Nont label;

	/** A list of subtrees (from left to right, ordered). */
	private final Vector<DerTree> subtrees;

	/** Location of a part of the program represented by this node. */
	private Location location;

	/**
	 * Constructs a new internal node of the derivation tree. Immediately after
	 * construction, the list of subtrees is empty as no subtrees have been
	 * appended yet.
	 * 
	 * @param label
	 *            The CFG nonterminal this node represents.
	 */
	public DerNode(Nont label) {
		this.label = label;
		this.subtrees = new Vector<DerTree>();
	}

	/**
	 * Add a new subtree to this node. Subtrees are always added from left to
	 * right.
	 * 
	 * @param subtree
	 *            The subtree to be added to this node.
	 * @return This node.
	 */
	public DerNode add(DerTree subtree) {
		subtrees.addElement(subtree);
		Location location = subtree.location();
		this.location = (this.location == null) ? location
				: ((location == null) ? this.location : new Location(this.location, location));
		return this;
	}

	/**
	 * Returns the list of subtrees.
	 * 
	 * @return The list of subtrees.
	 */
	public Vector<DerTree> subtrees() {
		return new Vector<DerTree>(subtrees);
	}

	/**
	 * Returns the specified subtree.
	 * 
	 * @param index
	 *            The index of the subtree (from left to right).
	 * @return The specified subtree.
	 */
	public DerTree subtree(int index) {
		return subtrees.elementAt(index);
	}

	@Override
	public Location location() {
		return location;
	}

	@Override
	public <Result, Arg> Result accept(DerVisitor<Result, Arg> visitor, Arg accArg) {
		return visitor.visit(this, accArg);
	}

}
