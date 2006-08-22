package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.NPCList;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for quests. This is a default implementation of IQuest.
 * 
 * @author hendrik
 */
public class AbstractQuest implements IQuest {

	/** The internal name of this quest. (e.g used to read quest.xml) */
	protected String name = null;

	/** The slot-name in !quests */
	protected String slotName = "XXX"; // TODO init it in the quest-classes

	/** NPCList * */
	protected NPCList npcs = null;

	private static final List<String> emptyList = new ArrayList<String>();

	public void init(String name) {
		this.name = name;
	}
	
	public void init(String name, String slotName) {
		this.name = name;
		this.slotName = slotName;
	}

	public void addToWorld() {
		this.npcs = NPCList.get();
	}

	public void convertOnUpdate(Player player) {
		// do nothing
	}

	public List<String> getHint(Player player) {
		return emptyList;
	}

	public List<String> getHistory(Player player) {
		// TODO this method should be abstact after all quests are converted
		return emptyList;
	}

	public boolean isCompleted(Player player) {
		return player.hasQuest(slotName)
				&& (player.isQuestCompleted(slotName)
						|| player.getQuest(slotName).equals("rejected") || player
						.getQuest(slotName).equals("failed"));
	}

	public boolean isRepeatable(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStarted(Player player) {
		return player.hasQuest(slotName);
	}

	public String getName() {
		return name;
	}

}
