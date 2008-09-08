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
public abstract class AbstractQuest implements IQuest {

	/** The internal name of this quest. (e.g used to read quest.xml) */
	protected String name;

	/** 
	 * The slot-name in !quests. 
	 * @return the slot's name 
	 * 
	 */
	public abstract String getSlotName();
	
	/** NPCList. */
	protected NPCList npcs = SingletonRepository.getNPCList();

	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	/**
	 * Inits this quest by specifying the name.
	 * <p>
	 * Make sure to set slotname in the subclasses.
	 *
	 * @param name
	 *            name of quest
	 */
	public void init(final String name) {
		this.name = name;
	}

	public void addToWorld() {
		// sub classes can implement this method
	}

	public List<String> getHint(final Player player) {
		return EMPTY_LIST;
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

	public String getName() {
		return name;
	}

}
