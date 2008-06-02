package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 */
public class BlordroughsRevengeRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("imp", 7);
		attackArmy.put("imperial demon servant", 5);
		attackArmy.put("imperial demon lord", 5);
		attackArmy.put("blordrough quartermaster", 7);
		attackArmy.put("blordrough corporal", 6);
		attackArmy.put("blordrough storm trooper", 8);
		attackArmy.put("fallen angel", 2);
		return attackArmy;
	}
}
