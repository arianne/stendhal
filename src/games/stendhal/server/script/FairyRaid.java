package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kymara
 * 
 * Not safe for players below level 20
 */
public class FairyRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("littlefairy", 7);
		attackArmy.put("littlewizard", 2);
		attackArmy.put("littlewitch", 2);
		attackArmy.put("clurichaun", 6);
		attackArmy.put("leprechaun", 7);
		return attackArmy;
	}
	
	@Override
	protected String getInfo() {
		return "Not safe for players below level 20";
	}
}
