package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is player emoting to npc?
 */
public class EmoteCondition implements ChatCondition {
	
	private final String playerAction;

	/**
	 * Creates a new AdminCondition for high level admins. '
	 */
	public EmoteCondition(final String playerAction) {
		this.playerAction = playerAction.trim();
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final String text = sentence.getOriginalText();
		return ((text.startsWith("!me")) && 
				(text.contains(playerAction)) &&
				(text.toLowerCase().contains(entity.getTitle().toLowerCase())));
	}

	@Override
	public String toString() {
		return "EmoteCondition";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				EmoteCondition.class);
	}

}
