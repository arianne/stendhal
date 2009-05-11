package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Not safe for players below level 150
 */
public class DragonRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("red dragon", 2);
		attackArmy.put("green dragon", 2);
		attackArmy.put("bone dragon", 3);
		attackArmy.put("twinheaded dragon", 2);
		attackArmy.put("blue dragon", 3);
		attackArmy.put("chaos red dragonrider", 2);
		attackArmy.put("chaos green dragonrider", 2);
		attackArmy.put("flying golden dragon", 2);
		attackArmy.put("black dragon", 1);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
