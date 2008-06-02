package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 */
public class GreenFloodRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("orc warrior", 7);
		attackArmy.put("orc hunter", 5);
		attackArmy.put("orc chief", 3);
		attackArmy.put("mountain orc", 6);
		attackArmy.put("mountain orc chief", 3);
		attackArmy.put("superogre", 4);
		attackArmy.put("soldier goblin", 7);
		attackArmy.put("cave troll", 2);
   		attackArmy.put("green dragon", 3);  

		return attackArmy;
	}
}
