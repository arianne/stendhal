package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.NPCList;

import java.util.List;

/**
 * Abstract class for quests. This is a default implementation of IQuest. 
 *
 * @author hendrik
 */
public class AQuest implements IQuestExperimental {
	protected String name = null;
	protected NPCList npcs = null;
	protected StendhalRPRuleProcessor rules = null;
	protected StendhalRPWorld world = null;


	public void init(String name) {
		this.name = name;
	}

	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();
		this.world = world;
	}

	public void convertOnUpdate(Player player) {
		// do nothing
	}

	public List<String> getHint(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getHistory(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCompleted(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRepeatable(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStarted(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

}
