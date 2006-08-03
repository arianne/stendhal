package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;

import java.util.List;

/** 
 * This is an experimental interface to help me think about
 * a quest system.
 */
public interface IQuestExperimental extends IQuest{

	/**
	 * Initialize the quest on server startup.
	 *
	 * @param name Name of this quest
	 */
	public void init(String name);


	/**
	 * adds the quest to the game world (e.g. by placing SpeakerNPCs there)
	 *
	 * @param world StendhalRPWorld
	 * @param rules StendhalRPRuleProcessor
	 */
	public void addToWorld(StendhalRPWorld world, StendhalRPRuleProcessor rules);

	/**
	 * Adjusts the quest states of an old release. This method is
	 * called after a player logs in the first time after an server
	 * update.
	 *
	 * @param player Player
	 */
	public void convertOnUpdate(Player player);

	/**
	 * Was the quest started?
	 *
	 * @param player Player
	 * @return true, if it was started, false otherwise
	 */
	public boolean isStarted(Player player);

	/**
	 * Was the quest completed?<!--sic--> Note: A quest can be completed
	 * without its status beeing "Done" (e. g. rejected, failed). 
	 *
	 * @param player Player
	 * @return true, if it was completed, false otherwise
	 */
	public boolean isCompleted(Player player);

	/**
	 * May the quest be repeated?
	 *
	 * @param player Player
	 * @return true, if it can be repeated, false otherwise
	 */
	public boolean isRepeatable(Player player);

	/**
	 * Gets a list of history item-names.
	 * The texts will be looked up in quest.xml
	 *
	 * @param player Player
	 * @return list of history item-names
	 */
	public List<String> getHistory(Player player);

	/**
	 * Gets a list of possible hint-names. The hint system will ensure
	 * that the same hint is not displayed twice. This class creates
	 * a list of useful hints (without hints about already completed parts). 
	 * The texts will be looked up in quest.xml
	 *
	 * @param player Player
	 * @return list of history item-names
	 */
	public List<String> getHint(Player player);
}
