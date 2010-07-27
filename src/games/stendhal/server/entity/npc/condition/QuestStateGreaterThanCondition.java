package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
/**
 * Condition to check if a player has finished a quest a certain number of times
 * 
 * @author madmetzger
 */
public class QuestStateGreaterThanCondition implements ChatCondition {
	
	/**
	 * how often should the quest be finished to fullfill this condition
	 */
	private final int numberOfTimesFinished;
	
	/**
	 * at which index is the number of finishings stored in the quest slot
	 */
	private final int index;
	
	/**
	 * which quest should be checked
	 */
	private final String questSlot;

	/**
	 * Create a new FinishedQuestGreaterOrEqualThanCondition
	 * @param quest name of the quest slot
	 * @param numberOfTimesFinished how often to finish at least?
	 * @param index index where the number is stored in the quest slot
	 */
	public QuestStateGreaterThanCondition(String quest, int index,
			int numberOfTimesFinished) {
		this.questSlot = quest;
		this.numberOfTimesFinished = numberOfTimesFinished;
		this.index = index;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		if(player.hasQuest(questSlot)) {
			String questState = player.getQuest(questSlot);
			String[] content = questState.split(";");
			int actualNumber = MathHelper.parseIntDefault(content[index], 0);
			return actualNumber > numberOfTimesFinished;
		}
		return false;
	}

}
