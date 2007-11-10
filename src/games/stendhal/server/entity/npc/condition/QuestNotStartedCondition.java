package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest not started yet?
 */
public class QuestNotStartedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	public QuestNotStartedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (!player.hasQuest(questname));
	}
}

