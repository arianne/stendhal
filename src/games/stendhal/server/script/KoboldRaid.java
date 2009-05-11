package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Less safe for players below level 10
 */
public class KoboldRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("kobold", 7);
		attackArmy.put("archer kobold", 3);
		attackArmy.put("leader kobold", 7);
		attackArmy.put("soldier kobold", 7);
		attackArmy.put("giant kobold", 2);
		attackArmy.put("veteran kobold", 7);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Less safe for players below level 10.";
	}
}