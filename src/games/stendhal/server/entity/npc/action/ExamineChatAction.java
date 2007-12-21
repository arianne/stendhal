package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ExamineChatAction extends ChatAction {
	private String image;
	private String title;
	private String alt;

	public ExamineChatAction(String image, String title, String alt) {
		this.image = image;
		this.title = title;
		this.alt = alt;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		player.addEvent(new ExamineEvent(image, title, alt));
	}

	@Override
	public String toString() {
		return "ExamineChatAction <" + image + ">";
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				StartRecordingKillsAction.class);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
