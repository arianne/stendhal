package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Less safe for players below level 50
 */
public class ElementalRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("fire elemental", 7);
		attackArmy.put("water elemental", 7);
		attackArmy.put("ice elemental", 7);
		attackArmy.put("earth elemental", 7);
		attackArmy.put("djinn", 5);
		attackArmy.put("air elemental", 7);
		return attackArmy;
	}
}