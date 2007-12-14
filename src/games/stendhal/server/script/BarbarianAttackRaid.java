package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

public class BarbarianAttackRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("barbarian", 30);
		attackArmy.put("barbarian_wolf", 15);
		attackArmy.put("barbarian_elite", 12);
		attackArmy.put("barbarian_priest", 7);
		attackArmy.put("barbarian_chaman", 5);
		attackArmy.put("barbarian_leader", 3);
		attackArmy.put("barbarian_king", 1);

		return attackArmy;
	}

}
