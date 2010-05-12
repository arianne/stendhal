package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * checks whether the sentence has an error
 */
public class SentenceHasErrorCondition implements ChatCondition {


	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return (sentence.hasError());
	}

	@Override
	public String toString() {
		return "sentenceHasError";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SentenceHasErrorCondition.class);
	}

}
