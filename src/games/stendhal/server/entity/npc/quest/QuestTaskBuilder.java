package games.stendhal.server.entity.npc.quest;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

public abstract class QuestTaskBuilder {

	abstract void simulate(QuestSimulator simulator);

	abstract ChatAction buildStartQuestAction(String questSlot);

	abstract ChatCondition buildQuestCompletedCondition(String questSlot);

	abstract ChatAction buildQuestCompleteAction(String questSlot);

	boolean isCompleted(Player player, String questSlot) {
		return buildQuestCompletedCondition(questSlot).fire(player, null, null);
	}

}
