package games.stendhal.server.entity.creature.impl;

import games.stendhal.server.entity.creature.Creature;

import java.util.Map;

public class IdleBehaviourFactory {
	private static final Idlebehaviour nothing = new DoNothingOnIdle();
	private static final Idlebehaviour patroller = new Patroller();
	public static Idlebehaviour get(Map<String, String> aiProfiles) {
		if (aiProfiles.containsKey("patrolling")){
			return patroller;
		}
		return nothing;
	}

	private static class DoNothingOnIdle implements Idlebehaviour{

		public void perform(Creature creature) {
			// do nothing
			
		}
		
	}
}
