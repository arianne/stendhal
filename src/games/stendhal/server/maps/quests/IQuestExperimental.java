package games.stendhal.server.maps.quests;

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
	
	public boolean isStarted();
	
	public boolean isCompleted();
	
	public boolean isRepeatable();
	
	public List<String> getHistory();
	
	public String getHint();
}
