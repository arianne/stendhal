package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miguel
 */
public class DrowAttackRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dark_elf", 30);
		attackArmy.put("dark_elf_archer", 10);
		attackArmy.put("dark_elf_elite_archer", 5);
		attackArmy.put("dark_elf_captain", 7);
		attackArmy.put("dark_elf_knight", 3);
		attackArmy.put("dark_elf_general", 1);
		attackArmy.put("dark_elf_wizard", 2);
		attackArmy.put("dark_elf_viceroy", 1);
		attackArmy.put("dark_elf_sacerdotist", 3);
		attackArmy.put("dark_elf_matronmother", 1);

		return attackArmy;
	}
}
