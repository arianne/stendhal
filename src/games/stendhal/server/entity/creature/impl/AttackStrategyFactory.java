package games.stendhal.server.entity.creature.impl;

import java.util.Map;

public class AttackStrategyFactory {
	final private static AttackStrategy handtoHand = new HandToHand();
	final private static AttackStrategy archer = new RangeAttack();
	public static AttackStrategy get(Map<String, String> aiProfiles){
		
		if (!aiProfiles.containsKey("archer")){
			return handtoHand;
		}
		return archer;
	}
}
