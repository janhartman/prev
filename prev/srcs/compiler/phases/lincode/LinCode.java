package compiler.phases.lincode;

import java.util.*;
import compiler.phases.*;

public class LinCode extends Phase {

	/** The list of fragments. */
	private static final LinkedList<Fragment> fragments = new LinkedList<Fragment>();

	/**
	 * Constructs a new phase for computing linear intermediate code.
	 */
	public LinCode() {
		super("lincode");
	}

	@Override
	public void close() {
		for (Fragment fragment: fragments())
			fragment.log(logger);
		super.close();
	}

	/**
	 * Adds a new fragment to a list of fragments.
	 * 
	 * @param fragment
	 *            The new fragment.
	 */
	public static void add(Fragment fragment) {
		fragments.add(fragment);
	}

	/**
	 * Returns the list of all fragments.
	 * 
	 * @return The list of all fragments.
	 */
	public static LinkedList<Fragment> fragments() {
		return new LinkedList<Fragment>(fragments);
	}

}
