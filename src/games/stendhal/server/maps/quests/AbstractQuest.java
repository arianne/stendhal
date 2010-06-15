package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for quests. This is a default implementation of IQuest.
 *
 * @author hendrik
 */
abstract class AbstractQuest implements IQuest {

	
	private static final List<String> EMPTY_LIST = new ArrayList<String>();
	
	private QuestInfo questInfo;
	
	public QuestInfo getQuestInfo(Player player) {
		return questInfo;
	}	
	
	
	/** NPCList. */
	protected NPCList npcs = SingletonRepository.getNPCList();

	/** 
	 * The slot-name in !quests. 
	 * @return the slot's name 
	 * 
	 */
	public abstract String getSlotName();
	
	public void addToWorld() {
		// sub classes can implement this method
	}

	public List<String> getHint(final Player player) {
		return EMPTY_LIST;
	}

	// Determines if the player should be given a hint to start the quest. 
	// Not a hard condition about the quest itself. (use level check ChatConditions for that)
	public int getMinLevel() {
		return 0;
	}
	
	public List<String> getHistory(final Player player) {
		// TODO this method should be abstract after all quests are converted
		return EMPTY_LIST;
	}

	public boolean isCompleted(final Player player) {
		return player.hasQuest(getSlotName())
				&& (player.isQuestCompleted(getSlotName())
						|| "rejected".equals(player.getQuest(getSlotName())) 
						|| "failed".equals(player.getQuest(getSlotName())));
	}

	public boolean isRepeatable(final Player player) {
		return false;
	}

	public boolean isStarted(final Player player) {
		return player.hasQuest(getSlotName());
	}

	public abstract String getName();

}
