package games.stendhal.server.entity.creature.impl;

import java.util.Map;

public class AttackStrategyFactory {
	private static final AttackStrategy handtoHand = new HandToHand();
	private static final AttackStrategy archer = new RangeAttack();
	public static AttackStrategy get(Map<String, String> aiProfiles) {
		
		if (!aiProfiles.containsKey("archer")) {
			return handtoHand;
		}
		return archer;
	}
}
