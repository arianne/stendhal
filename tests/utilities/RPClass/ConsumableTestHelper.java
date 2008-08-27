package utilities.RPClass;

import games.stendhal.server.entity.item.ConsumableItem;

import java.util.HashMap;
import java.util.Map;

public class ConsumableTestHelper {

	public static ConsumableItem createEater(final String name) {
		ItemTestHelper.generateRPClasses();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount", "1");
		attributes.put("regen", "1");
		attributes.put("frequency", "1");
		return new ConsumableItem(name, "class", "subclass", attributes);
	}
	
	public static ConsumableItem createImmunizer(final String name) {
		ItemTestHelper.generateRPClasses();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("amount", "1");
		attributes.put("regen", "0");
		attributes.put("frequency", "1");
		return new ConsumableItem(name, "class", "subclass", attributes);
	}

	

}
