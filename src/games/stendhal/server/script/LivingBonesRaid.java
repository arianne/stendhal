package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gummipferd
 */
public class LivingBonesRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("skeleton", 7);
		attackArmy.put("tiny skelly", 5);
		attackArmy.put("warrior skeleton", 7);
		attackArmy.put("elder skeleton", 4);
		attackArmy.put("demon skeleton", 3);
		attackArmy.put("bone dragon", 3);
		attackArmy.put("fallen warrior", 5);
		attackArmy.put("fallen priest", 3);
		attackArmy.put("fallen high priest", 2);
		attackArmy.put("lich", 1);
		attackArmy.put("dead lich", 1);
		attackArmy.put("high lich", 1);

		return attackArmy;
	}
}
