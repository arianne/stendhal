package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the current state of this quest
 */
public class SetQuestAction extends SpeakerNPC.ChatAction {

	private String questname;

	private String state;

	public SetQuestAction(String questname, String state) {
		this.questname = questname;
		this.state = state;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		player.setQuest(questname, state);
	}
}