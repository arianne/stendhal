package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * says the specified text, it works just like the normal parameter of add
 */
public class SayTextAction implements ChatAction {

	private final String text;

	/**
	 * Creates a new SayTextAction.
	 * 
	 * @param text text to say
	 */
	public SayTextAction(String text) {
		this.text = text;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		raiser.say(text);
	}

	@Override
	public String toString() {
		return "SetSayText";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				SayTextAction.class);
	}
}
