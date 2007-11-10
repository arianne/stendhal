package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest started?
 */
public class QuestStartedCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	/**
	 * Creates a new QuestStartedCondition
	 *
	 * @param questname name of quest slot
	 */
	public QuestStartedCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.hasQuest(questname));
	}
}