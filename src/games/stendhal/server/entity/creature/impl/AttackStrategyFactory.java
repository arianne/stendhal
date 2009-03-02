package games.stendhal.server.entity.creature.impl;

import java.util.Map;

public class AttackStrategyFactory {
	private static final AttackStrategy HAND_TO_HAND = new HandToHand();
	private static final AttackStrategy ARCHER = new RangeAttack();
	private static final AttackStrategy COWARD = new Coward();
	private static final AttackStrategy STUPID_COWARD = new StupidCoward();
	private static final AttackStrategy GANDHI = new Gandhi();

	
	public static AttackStrategy get(final Map<String, String> aiProfiles) {
		
		if (aiProfiles.containsKey("archer")) {
			return ARCHER;
		} else if (aiProfiles.containsKey("coward")) {
			return COWARD;
		} else if (aiProfiles.containsKey("gandhi")) {
			return GANDHI;
		} else if (aiProfiles.containsKey("stupid coward")) {
			return STUPID_COWARD;
		}

		return HAND_TO_HAND;
	}
}
