package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Opens an examine window on the client
 *
 * @author hendrik
 */
public class ExamineChatAction implements ChatAction {
	private final String image;
	private final String title;
	private final String caption;

	/**
	 * Creates a new ExamineChatAction
	 *
	 * @param image the image to display
	 * @param title the title
	 * @param caption text to display along the image
	 */
	public ExamineChatAction(final String image, final String title, final String caption) {
		this.image = image;
		this.title = title;
		this.caption = caption;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
		player.addEvent(new ExamineEvent("examine/" + image, title, caption));
	}

	@Override
	public String toString() {
		return "ExamineChatAction <" + image + ">";
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ExamineChatAction.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
