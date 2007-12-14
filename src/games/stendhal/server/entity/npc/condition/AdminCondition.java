package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player an admin?
 */
public class AdminCondition extends SpeakerNPC.ChatCondition {

	private int requiredAdminlevel;

	/**
	 * Creates a new AdminCondition for high level admins. '
	 */
	public AdminCondition() {
		requiredAdminlevel = 5000;
	}

	/**
	 * Creates a new AdminCondition.
	 * 
	 * @param requiredAdminlevel
	 *            minimum admin level '
	 */
	public AdminCondition(int requiredAdminlevel) {
		this.requiredAdminlevel = requiredAdminlevel;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
		return (player.getAdminLevel() >= requiredAdminlevel);
	}

	@Override
	public String toString() {
		return "admin <" + requiredAdminlevel + ">";
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
