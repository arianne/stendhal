package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Not safe for players below level 5
 */
public class AnimalRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("monkey", 2);
		attackArmy.put("grass snake", 2);
		attackArmy.put("beaver", 2);
		attackArmy.put("tiger", 2);
		attackArmy.put("lion", 3);
		attackArmy.put("panda", 2);
		attackArmy.put("penguin", 4);
		attackArmy.put("caiman", 3);
		attackArmy.put("babybear", 2);
		attackArmy.put("black bear", 1);
		attackArmy.put("elephant", 3);
		attackArmy.put("crocodile", 2);

		return attackArmy;
	}
}
