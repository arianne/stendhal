package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 */
public class SmallForcesRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("gnome", 12);
		attackArmy.put("mage gnome", 7);
		attackArmy.put("infantry gnome", 10);
		attackArmy.put("cavalryman gnome", 10);
		attackArmy.put("dark gargoyle", 2);

		return attackArmy;
	}
}
