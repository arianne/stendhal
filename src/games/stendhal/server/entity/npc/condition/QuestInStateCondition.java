package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is this quest in this state?
 */
public class QuestInStateCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	private String state;

	public QuestInStateCondition(String questname, String state) {
		this.questname = questname;
		this.state = state;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.hasQuest(questname) && player.getQuest(questname).equals(state));
	}
}