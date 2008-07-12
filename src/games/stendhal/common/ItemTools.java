package games.stendhal.common;

/**
 * Utility functions to handle item names.
 *
 * @author Martin Fuchs
 */
public class ItemTools {

	/**
     * Replace underscores in the given String by spaces.
     * This is used to replace underscore characters in compound item and creature names
     * after loading data from the database.
     * 
     * @param name
     * @return transformed String if name contained an underscore,
     * 			or unchanged String object
     * 			or null if name was null
     */
    public static String itemNameToDisplayName(final String name) {
    	if (name != null) {
    		if (name.indexOf('_') != -1) {
    			return name.replace('_', ' ');
    		}
    	}
    	return name;
    }

}
