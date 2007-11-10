package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest completed?
 */
public class QuestCompletedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	public QuestCompletedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.isQuestCompleted(questname));
	}
}