package common.report;

/**
 * Implemented by classes of objects that contain information relating to a part
 * of the source file.
 * 
 * @author sliva
 *
 */
public interface Locatable {

	/**
	 * Returns the location of the part of the source file.
	 * 
	 * @return The location of the part of the source file.
	 */
	public Location location();

}
