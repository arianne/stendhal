package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Less safe for players below level 30
 */
public class DwarfRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dwarf", 7);
		attackArmy.put("dwarf guardian", 6);
		attackArmy.put("elder dwarf", 6);
		attackArmy.put("leader dwarf", 4);
		attackArmy.put("hero dwarf", 5);
		attackArmy.put("duergar", 3);
		attackArmy.put("elder duergar", 3);
		attackArmy.put("duergar axeman", 3);

		return attackArmy;
	}
}
