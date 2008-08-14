package games.stendhal.server.entity.npc.condition;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Does this trigger contain a number?
 */
public class TextHasNumberCondition implements ChatCondition {
	private int min;
	private int max;

	/**
	 * Creates a new TextHasNumberCondition which checks for a positive integer.
	 */
	public TextHasNumberCondition() {
		this.min = 0;
		this.max = Integer.MAX_VALUE;
	}

	/**
	 * Creates a new TextHasNumberCondition which checks if there is a number 
	 * and if it is in range
	 *
	 * @param min minimal accepted number
	 * @param max maximal accepted number
	 */
	public TextHasNumberCondition(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final Expression number = sentence.getNumeral();

		if (number != null) {
			final int num = number.getAmount();
			if ((num >= min) && (num <= max)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "has number<" + min + ", " + max + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TextHasNumberCondition.class);
	}
}