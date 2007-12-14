package games.stendhal.server.maps.quests;

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

	/** The slot-name in !quests. */
	protected String slotName = "XXX";
	// TODO init it in the quest-classes

	/** NPCList. * */
	protected NPCList npcs = NPCList.get();

	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	/**
	 * inits the quest by specifying the name.
	 * <p>
	 * Make sure to set slotname in the subclasses.
	 * 
	 * @param name
	 *            name of quest
	 */
	public void init(final String name) {
		this.name = name;
	}

	/**
	 * inits this quest by specifing the name and quest slot.
	 * 
	 * @param name
	 *            name of quest
	 * @param slotName
	 *            name of quest-slot
	 */
	protected void init(final String name, final String slotName) {
		this.name = name;
		this.slotName = slotName;
	}

	public void addToWorld() {
		// sub classes can implement this method
	}

	public List<String> getHint(final Player player) {
		return EMPTY_LIST;
	}

	public List<String> getHistory(Player player) {
		// TODO this method should be abstract after all quests are converted
		return EMPTY_LIST;
	}

	public boolean isCompleted(final Player player) {
		return player.hasQuest(slotName)
				&& (player.isQuestCompleted(slotName)
						|| player.getQuest(slotName).equals("rejected") || player.getQuest(
						slotName).equals("failed"));
	}

	public boolean isRepeatable(Player player) {
		return false;
	}

	public boolean isStarted(Player player) {
		return player.hasQuest(slotName);
	}

	public String getName() {
		return name;
	}

}
