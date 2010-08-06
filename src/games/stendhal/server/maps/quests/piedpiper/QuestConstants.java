package games.stendhal.server.maps.quests.piedpiper;

import java.util.Arrays;
import java.util.List;

public final class QuestConstants {
	
    // timings unit is second.
	public int QUEST_INACTIVE_TIME_MAX = 1;
	public int QUEST_INACTIVE_TIME_MIN = 1;
	public int QUEST_INVASION_TIME = 1;
	public int QUEST_AWAITING_TIME = 1;
	public int QUEST_SHOUT_TIME = 1;

	/**
	 * function will set timings to either test server or game server.
	 */
	public void adjustTimings() {
		if (System.getProperty("stendhal.testserver") == null) {		
			// game timings
			QUEST_INACTIVE_TIME_MAX = 60 * 60 * 24 * 14;
			QUEST_INACTIVE_TIME_MIN = 60 * 60 * 24 * 7;
			QUEST_INVASION_TIME = 60 * 60 * 2;
			QUEST_AWAITING_TIME = 60 * 1;
			QUEST_SHOUT_TIME = 60 * 10;
			} 
		else {	
			// test timings
			QUEST_INACTIVE_TIME_MAX = 60 * 11;
			QUEST_INACTIVE_TIME_MIN = 60 * 10;
			QUEST_INVASION_TIME = 60 * 20;
			QUEST_AWAITING_TIME = 60 * 10;
			QUEST_SHOUT_TIME = 60 * 2;
			}
	}

	/**
	 * related to quest part.
	 * <ul>
	 * <li> INACTIVE - quest isn't active
	 * <li> INVASION - part I (rats invasion)
	 * <li> AWAITING - part II (pied piper called)
	 * <li> OUTGOING - part III (pied piper killing rats)
	 * <li> CHILDRENS - part IV (pied piper takes childrens away)
	 * <li> FINAL - part V (return childrens back to Ados)
	 * </ul>
	 */

	public enum TPP_Phase {
		TPP_INACTIVE,
		TPP_INVASION,
		TPP_AWAITING ,
		TPP_OUTGOING,
		TPP_CHILDRENS,
		TPP_FINAL
	}
	
	/**
	 * List of game zones, where rats will appears.
	 *
	 * TODO: add other Ados buildings here, and improve summonRats() function
	 *       to avoid placing rats inside closed areas within houses.
	 */
	public final List<String> RAT_ZONES = Arrays.asList(
//			"int_ados_haunted_house",
//			"int_ados_storage",
			"int_ados_barracks_0",
			"int_ados_barracks_1",
			"int_ados_bakery",
			"int_ados_goldsmith",
			"int_ados_bank",
// can't be used because NPC can block creature
//			"int_ados_tavern_0",
			"int_ados_library",
			"int_ados_bar",
			"int_ados_bar_1",
			"int_ados_sewing_room",
			"int_ados_meat_market",
			"int_ados_fishermans_hut_north",
			"int_ados_town_hall",
			"int_ados_town_hall_1",
			"int_ados_town_hall_2",
			"int_ados_town_hall_3",
			"int_ados_ross_house",
			"0_ados_city_n",
			"0_ados_city",
			"0_ados_city_s");

	/**
	 * List of creatures types to create.
	 */
	public final List<String> RAT_TYPES = Arrays.asList(
			"rat",
			"caverat",
			"venomrat",
			"razorrat",
			"giantrat",
			"archrat");

	/**
	 * List of reward moneys quantities for each type of killed rats.
	 */
	public final List<Integer> RAT_REWARDS = Arrays.asList(
			10,
			20,
			100,
			160,
			360,
			800);
}
