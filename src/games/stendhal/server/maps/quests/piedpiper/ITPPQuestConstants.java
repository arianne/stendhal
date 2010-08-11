package games.stendhal.server.maps.quests.piedpiper;

import java.util.Arrays;
import java.util.List;

public interface ITPPQuestConstants {
	
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
