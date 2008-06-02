package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 */
public class StrikeFromTheWoodsRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("elf", 7);
		attackArmy.put("soldier elf", 7);
		attackArmy.put("commander elf", 4);
		attackArmy.put("archmage elf", 3);
		attackArmy.put("archer elf", 12);
		attackArmy.put("nymph", 5);
		attackArmy.put("ent", 3);

		return attackArmy;
	}
}
