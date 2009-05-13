package games.stendhal.server.entity.creature.impl;

import java.util.Map;

public class IdleBehaviourFactory {
	private static final Idlebehaviour nothing = new StandOnIdle();
	
	public static Idlebehaviour get(final Map<String, String> aiProfiles) {
		if (aiProfiles.containsKey("patrolling")) {
			return new Patroller();
		}
		return nothing;
	}
}
