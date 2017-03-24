package compiler.phases.seman.type;

import common.logger.*;

/**
 * An abstract class for describing types.
 * 
 * Each PREV type is represented by a tree of nodes where each node is an object
 * of class {@link SemType} or one of its subclasses.
 * 
 * @author sliva
 *
 */
public abstract class SemType implements Loggable {

	/**
	 * Returns the actual representation of {@code this} type.
	 * 
	 * Alongside nodes that describe the actual type a representation of a type
	 * can include nodes describing type synonyms (represented by objects of
	 * class {@link SemNamedType}). This function returns the top-most node
	 * describing the actual type by skipping all top-most synonym nodes.
	 * 
	 * @return The top-most non-synonym node of {@code this} type
	 *         representation.
	 */
	public SemType actualType() {
		return this;
	}

	/**
	 * Specifies whether data of this type can be assigned to a variable of this
	 * type.
	 * 
	 * @return {@code true} if data of this type can be assigned to a variable
	 *         of this type, {@code false} otherwise.
	 */
	public abstract boolean assignable();

	/**
	 * Specifies whether function arguments can be of this type.
	 * 
	 * @return {@code true} if data of this type can be send to a function,
	 *         {@code false} otherwise.
	 */
	public abstract boolean sendable();

	/**
	 * Specifies whether function results can be of this type.
	 * 
	 * @return {@code true} if data of this type can be received from a
	 *         function, {@code false} otherwise.
	 */
	public abstract boolean recvable();

	/**
	 * Checks whether {@code this} type matches {@code that} type.
	 * 
	 * @param that
	 *            A type to be matched.
	 * @return {@code true} if types match, {@code false} otherwise.
	 */
	public abstract boolean matches(SemType that);

	/**
	 * Checks whether the top-most node of the actual representation of
	 * {@code this} type matches the top-most node of the actual representation
	 * of {@code that} type. See {@link #actualType()} to understand what the
	 * actual representation of a type means. This method should be used if (a)
	 * one knows exactly what atomic type is expected (disregarding type
	 * synonyms) or if (b) one needs to test a kind of a non-atomic type
	 * (disregarding its parts).
	 * 
	 * @param that
	 *            A type to be matched.
	 * @return {@code true} if types match, {@code false} otherwise.
	 */
	public boolean isAKindOf(Class<?> that) {
		return this.actualType().getClass() == that;
	}

}
