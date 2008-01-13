package games.stendhal.server.core.engine;

import java.util.Iterator;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Converter class to replace underscore characters in compound item and creature names
 * after loading data from the database.
 *
 * @author Martin Fuchs
 */
public class UnderscoreConverter {
	private static Logger logger = Logger.getLogger(UnderscoreConverter.class);

	/**
	 * Transform all item and creature names in the given RPObject.
	 *
	 * @param object
	 */
	static void transformNames(RPObject object)	{
		RPClass clazz = object.getRPClass();

		if (clazz == null || !clazz.getName().equals("player")) {
    		// first rename the object itself, if the name contains an underscore character
    		transformAttribute(object, "name");
		}

		// now loop over all contained slots
		Iterator<RPSlot> it = object.slotsIterator();
		while(it.hasNext()) {
			RPSlot slot = it.next();

			if (slot.getName().equals("!kill")) {
				// translate the content of the kill list
				for(RPObject obj : slot) {
					transformKeyNames(obj);
				}
			} else {
				// transform the name of all contained item names
				for(RPObject obj : slot) {
					transformNames(obj);
				}
			}
		}
	}

	/**
	 * Transform the value for the given key name in 'object'.
	 *
	 * @param object
	 * @param key
	 * @return true if changed
	 */
	static boolean transformAttribute(RPObject object, String key)	{
		String value = object.get(key);

		if (value != null) {
			if (value.contains("_")) {
				String newValue = value.replace("_", " ");
				object.put(key, newValue);
				logger.info("renamed attribute '"+key+"' value '"+value+"' to '"+newValue+"'");
				return true;
			}
		}

		return false;
	}

	/**
	 * Replace all underscore characters by spaces in the key names of a "!kills" slot
	 *
	 * @param object
	 */
	static boolean transformKeyNames(RPObject object) {
		RPObject copy = (RPObject) object.clone();
		int count = 0;

		for(String key : copy) {
			if (key.contains("_")) {
				String newKey = key.replace("_", " ");
				String value = object.get(key);

				object.remove(key);
				object.put(newKey, value);

				logger.info("renamed stacked key '"+key+"' to '"+newKey+"'");
				++count;
			}
		}

		return count > 0;
	}

}
