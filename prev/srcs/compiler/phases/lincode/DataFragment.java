package compiler.phases.lincode;

import common.logger.*;
import compiler.phases.frames.*;

/**
 * A data fragment.
 * 
 * @author sliva
 *
 */
public class DataFragment extends Fragment {

	/** The entry label. */
	public final Label label;

	/** The size of data. */
	public final long size;

	/**
	 * Construct a new data fragment.
	 * 
	 * @param label
	 *            The entry label.
	 * @param size
	 *            The size of data.
	 */
	public DataFragment(Label label, long size) {
		this.label = label;
		this.size = size;
	}
	
	@Override
	public void log(Logger logger) {
		if (logger == null)
			return;
		logger.begElement("imcdata");
		logger.addAttribute("label", label.name);
		logger.addAttribute("size", new Long(size).toString());
		logger.endElement();
	}

}
