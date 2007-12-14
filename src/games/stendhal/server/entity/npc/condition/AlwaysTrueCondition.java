package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This condition returns always true. Use it in a quest file to override
 * behaviour defined in the map file
 */
public class AlwaysTrueCondition extends SpeakerNPC.ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return true;
	}

	@Override
	public String toString() {
		return "true";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				QuestStartedCondition.class);
	}
}