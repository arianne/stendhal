package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miguel
 * 
 * Not safe for players below level 150
 */
public class DrowRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dark elf", 20);
		attackArmy.put("dark elf archer", 5);
		attackArmy.put("dark elf elite archer", 5);
		attackArmy.put("dark elf captain", 7);
		attackArmy.put("dark elf knight", 3);
		attackArmy.put("dark elf general", 1);
		attackArmy.put("dark elf wizard", 2);
		attackArmy.put("dark elf viceroy", 1);
		attackArmy.put("dark elf sacerdotist", 3);
		attackArmy.put("dark elf matronmother", 1);
		attackArmy.put("dark elf master", 1);
		attackArmy.put("dark elf ranger", 3);
		attackArmy.put("dark elf admiral", 3);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
