package games.stendhal.server.maps.quests.piedpiper;

import java.util.List;

public interface ITPPQuest extends ITPPQuestConstants {
	
	/**
	 * function will change phase to next phase 
	 */
	void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments);
	
	/**
	 * function will reset quest state to default phase (INACTIVE).
	 */
	void phaseToDefaultPhase(List<String> comments);
	
	/**
	 * function return shout message when quest going to next phase 
	 * @return
	 */
	String getSwitchingToNextPhaseMessage();
	
	/**
	 * function return shout message when quest going to default phase
	 * @return
	 */
	String getSwitchingToDefPhaseMessage();
	
	/**
	 * return minimal timeout period for quest phase
	 * @return
	 */
	int getMinTimeOut();
	
	/**
	 * return maximal timeout period for quest phase
	 * @return
	 */
	int getMaxTimeOut();
	
	/**
	 * return quest phase
	 */
	TPP_Phase getPhase();

	/**
	 * function will perform necessary actions at phase start
	 */
	void prepare();
}

