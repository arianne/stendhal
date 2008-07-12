package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

import java.util.Map;

public class IdleBehaviourFactory {
	private static final Idlebehaviour nothing = new DoNothingOnIdle();
	
	public static Idlebehaviour get(final Map<String, String> aiProfiles) {
		if (aiProfiles.containsKey("patrolling")) {
			return new Patroller();
		}
		return nothing;
	}

	private static class DoNothingOnIdle implements Idlebehaviour {
		
		
		public void perform(final Creature creature) { 
			// Do nothing 
		}
		
	}
}
