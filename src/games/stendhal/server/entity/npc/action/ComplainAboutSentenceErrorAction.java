package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * tells the player that the npc did not understand the sentence;
 * use it in combination with SentenceHasErrorCondtion.
 */
public class ComplainAboutSentenceErrorAction implements ChatAction {

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (sentence.hasError()) {
			raiser.say("Sorry, I did not understand you. "
				+ sentence.getErrorString());
		}
	}

	@Override
	public String toString() {
		return "complainSentenceError";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ComplainAboutSentenceErrorAction.class);
	}
}
