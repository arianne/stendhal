package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 * 
 * Less safe for players below level 30
 */
public class ElfRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("elf", 7);
		attackArmy.put("militia elf", 4);
		attackArmy.put("soldier elf", 3);
		attackArmy.put("commander elf", 4);
		attackArmy.put("archmage elf", 3);
		attackArmy.put("mage elf", 6);
		attackArmy.put("archer elf", 6);
		attackArmy.put("nymph", 5);
		attackArmy.put("ent", 3);

		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Less safe for players below level 30.";
	}
}
