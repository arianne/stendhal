package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Did this trigger have additional parameters?
 */
public class TextHasParameterCondition extends SpeakerNPC.ChatCondition {

	@Override
	public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final String text = sentence.getOriginalText().trim();
		return text.indexOf(' ') > -1;
	}

	@Override
	public String toString() {
		return "parameters?";
	}

	@Override
	public int hashCode() {
		return 858577;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TextHasParameterCondition;
	}
}