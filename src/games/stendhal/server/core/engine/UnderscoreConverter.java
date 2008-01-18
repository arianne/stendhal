package games.stendhal.server.core.engine;

import java.util.Iterator;

import marauroa.common.game.Attributes;
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
	 * @param object to transform
	 */
	static void transformNames(RPObject object)	{
		RPClass clazz = object.getRPClass();

		// Transform all names, but don't change player names.
		if (clazz == null || !clazz.getName().equals("player")) {
    		// First rename the object itself, if the name contains an underscore character.
    		transformAttribute(object, "name");
		}

		// Now loop over all contained slots.
		Iterator<RPSlot> it = object.slotsIterator();
		while(it.hasNext()) {
			RPSlot slot = it.next();
			String slotName = slot.getName();

			if (slotName != null) {
    			if (slotName.equals("!kills")) {
    				// Translate the content of the kill list.
    				for(RPObject obj : slot) {
    					transformSlotKeyNames(obj);
    				}
    			}
    			// exclude key slots from the conversion
//    			else if (!slotName.startsWith("!"))
    			else if (!slotName.equals("!visited") &&
    						!slotName.equals("!buddy") &&
    						!slotName.equals("!ignore") &&
    						!slotName.equals("!quests") &&
    						!slotName.equals("!tutorial")) {
    				// Recurse to transform the names of all contained items.
    				for(RPObject obj : slot) {
    					transformNames(obj);
    				}
    			}
			}
		}
	}

	/**
	 * Transform the value for the given key name in 'object'.
	 *
	 * @param object to transform
	 * @param name of the key to transform
	 * @return true if changed
	 */
	static boolean transformAttribute(RPObject object, final String key)	{
		String value = object.get(key);

		String newValue = transform(value);
		if (newValue != value) {
			object.put(key, newValue);
			logger.info("renamed attribute '"+key+"' value '"+value+"' to '"+newValue+"'");
			return true;
		}

		return false;
	}

	/**
	 * Replace all underscore characters by spaces in the key names of a "!kills" slot
	 *
	 * @param object to transform
	 * @return true if changed
	 */
	static boolean transformSlotKeyNames(RPObject object) {
		// create a copy of the attribute map to iterate independently from the original 
		Attributes attributes = new Attributes(object.getRPClass());
		attributes.fill(object);
		int count = 0;

		for(String key : attributes) {
			String newKey = transform(key);

			if (newKey != key) {
				String value = object.get(key);

				object.remove(key);
				object.put(newKey, value);

				logger.info("renamed slot key '"+key+"' to '"+newKey+"'");
				++count;
			}
		}

		return count > 0;
	}

	/**
	 * Replace underscores in the given String by spaces. Additionally correct
	 * some item names to correct grammatical word order.
	 * 
	 * @param str
	 * @return transformed String if str contained an underscore,
	 * 			or unchanged String
	 * 			or null if str was null
	 */
	public static String transform(String str) {
		if (str != null) {
			if (str.indexOf('_') != -1) {
				str = str.replace('_', ' ');
			}

			if (str.equals("key golden")) {
				return "golden key";
			} else if (str.equals("key silver")) {
				return "silver key";
			} else if (str.equals("book black")) {
				return "black book";
			} else if (str.equals("book blue")) {
				return "blue book";
			} else if (str.equals("duergar elder")) {
				return "elder duergar";
			} else if (str.equals("duergar black")) {
				return "black duergar";
			} else if (str.equals("giant elder")) {
				return "elder giant";
			} else if (str.equals("chaos sorceror")) {
				return "chaos sorcerer";
			} else {
				return str;
			}
		} else {
			return null;
		}
	}

}
