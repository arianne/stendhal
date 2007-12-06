package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player naked? (e. g. not wearing anything on his/her body)
 */
public class NakedCondition extends SpeakerNPC.ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		Outfit outfit = player.getOutfit();
		return outfit.isNaked();
	}

	@Override
	public String toString() {
		return "naked?";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, NakedCondition.class);
	}
}