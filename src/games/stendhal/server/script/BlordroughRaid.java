package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 * 
 * Not safe for players below level 150
 */
public class BlordroughRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("elder giant", 5);
		attackArmy.put("imperial general giant", 5);
		attackArmy.put("blordrough quartermaster", 9);
		attackArmy.put("blordrough corporal", 6);
		attackArmy.put("blordrough storm trooper", 8);
		attackArmy.put("master giant", 2);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
