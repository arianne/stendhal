package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Was this quest started but not completed?
 */
public class QuestActiveCondition extends SpeakerNPC.ChatCondition {

	private String questname;

	/**
	 * Creates a new QuestActiveCondition
	 *
	 * @param questname name of quest slot
	 */
	public QuestActiveCondition(String questname) {
		this.questname = questname;
	}

	@Override
	public boolean fire(Player player, String text, SpeakerNPC engine) {
		return (player.hasQuest(questname) 
						&& !player.isQuestCompleted(questname));
	}


	@Override
	public String toString() {
		return "QuestActive<" + questname + ">";
	}
}