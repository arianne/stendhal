package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.TriggerList;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Was one of theses trigger phrases said? (Use with a ""-trigger in npc.add)
 */
public class TriggerInListCondition implements ChatCondition {
	private final TriggerList triggers;

	/**
	 * Creates a new TriggerInListCondition.
	 * 
	 * @param trigger
	 *            list of trigger
	 */
	public TriggerInListCondition(final String... trigger) {
		this(Arrays.asList(trigger));
	}

	/**
	 * Creates a new TriggerInListCondition.
	 * 
	 * @param trigger
	 *            list of trigger
	 */
	public TriggerInListCondition(final List<String> trigger) {
		triggers = new TriggerList(trigger);
	}

	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		return triggers.contains(sentence.getTriggerExpression());
	}

	@Override
	public String toString() {
		return "trigger <" + triggers.toString() + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				TriggerInListCondition.class);
	}
}
