package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Not safe for players below level 150
 */
public class ChaosRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("chaos warrior", 4);
		attackArmy.put("chaos soldier", 3);
		attackArmy.put("chaos commander", 4);
		attackArmy.put("chaos lord", 3);
		attackArmy.put("chaos sorcerer", 3);
		attackArmy.put("chaos overlord", 3);
		attackArmy.put("chaos dragonrider", 3);
		attackArmy.put("chaos red dragonrider", 2);
		attackArmy.put("chaos green dragonrider", 2);
		attackArmy.put("black giant", 1);
		attackArmy.put("black dragon", 1);
		return attackArmy;
	}
}
