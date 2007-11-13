package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Is the player naked? (e. g. not wearing anything on his/her body)
 */
public class NakedCondition extends SpeakerNPC.ChatCondition {

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return true;
	}

	@Override
	public String toString() {
		return "naked?";
	}

	@Override
	public int hashCode() {
		return 23487126;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, NakedCondition.class);
	}
}