package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;

import java.util.List;

/** 
 * This is an experimental interface to help me think about
 * a quest system.
 */
public interface IQuestExperimental extends IQuest{

	public void init();
	
	public String getName();
	
	public String getTitle();
	
	public String getDescription();
	
	public String getGMDescription();
	
	public boolean isStarted(Player player);
	
	public boolean isCompleted(Player player);
	
	public boolean isRepeatable(Player player);
	
	public List<String> getHistory(Player player);
	
	public String getHint(Player player);
}
