package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Not safe for players below level 150
 */
public class AngelRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("baby angel", 20);
		attackArmy.put("angel", 5);
		attackArmy.put("dark angel", 2);
		attackArmy.put("archangel", 3);
		// no dark archangel here as archers can still hit you as you run from them
		return attackArmy;
	}
	
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150";
	}
}
