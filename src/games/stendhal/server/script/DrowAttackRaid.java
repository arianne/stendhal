package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miguel
 */
public class DrowAttackRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dark elf", 30);
		attackArmy.put("dark elf archer", 10);
		attackArmy.put("dark elf elite archer", 5);
		attackArmy.put("dark elf captain", 7);
		attackArmy.put("dark elf knight", 3);
		attackArmy.put("dark elf general", 1);
		attackArmy.put("dark elf wizard", 2);
		attackArmy.put("dark elf viceroy", 1);
		attackArmy.put("dark elf sacerdotist", 3);
		attackArmy.put("dark elf matronmother", 1);

		return attackArmy;
	}
}
