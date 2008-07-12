package games.stendhal.server.entity.creature.impl;

import java.util.Map;

public class AttackStrategyFactory {
	private static final AttackStrategy handtoHand = new HandToHand();
	private static final AttackStrategy archer = new RangeAttack();
	private static final AttackStrategy coward = new Coward();
	private static final AttackStrategy stupidcoward = new StupidCoward();
	private static final AttackStrategy gandhi = new Gandhi();
	public static AttackStrategy getGandhi() {
		return gandhi;
	}
	public static AttackStrategy get(final Map<String, String> aiProfiles) {
		
		if (aiProfiles.containsKey("archer")) {
			return archer;
		} else if (aiProfiles.containsKey("coward")) {
			return coward;
		} else if (aiProfiles.containsKey("gandhi")) {
			return gandhi;
		} else if (aiProfiles.containsKey("stupid coward")) {
			return stupidcoward;
		}

		return handtoHand;
	}
}
