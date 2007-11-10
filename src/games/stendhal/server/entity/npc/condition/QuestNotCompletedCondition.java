package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is this quest not completed?
 */
public class QuestNotCompletedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	public QuestNotCompletedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (!player.isQuestCompleted(questname));
	}
}