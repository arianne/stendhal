package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is the player an admin?
 */
public class AdminCondition implements ChatCondition {

	private final int requiredAdminlevel;

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
	public AdminCondition(final int requiredAdminlevel) {
		this.requiredAdminlevel = requiredAdminlevel;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
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
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				AdminCondition.class);
	}

}
