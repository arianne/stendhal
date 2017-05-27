/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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

	public TPP_Phase INACTIVE = TPP_Phase.TPP_INACTIVE;
	public TPP_Phase INVASION = TPP_Phase.TPP_INVASION;
	public TPP_Phase AWAITING = TPP_Phase.TPP_AWAITING;
	public TPP_Phase OUTGOING = TPP_Phase.TPP_OUTGOING;
	public TPP_Phase CHILDRENS = TPP_Phase.TPP_CHILDRENS;
	public TPP_Phase FINAL = TPP_Phase.TPP_FINAL;

	final String QUEST_SLOT = "the_pied_piper";

	final String INACTIVE_TIME_MAX = "QUEST_INACTIVE_TIME_MAX";
	final String INACTIVE_TIME_MIN = "QUEST_INACTIVE_TIME_MIN";
	final String INVASION_TIME_MIN = "QUEST_INVASION_TIME_MIN";
	final String INVASION_TIME_MAX = "QUEST_INVASION_TIME_MAX";
	final String AWAITING_TIME_MIN = "QUEST_AWAITING_TIME_MIN";
	final String AWAITING_TIME_MAX = "QUEST_AWAITING_TIME_MAX";
	final String OUTGOING_TIME_MIN = "QUEST_OUTGOING_TIME_MIN";
	final String OUTGOING_TIME_MAX = "QUEST_OUTGOING_TIME_MAX";
	final String CHILDRENS_TIME_MIN = "QUEST_CHILDRENS_TIME_MIN";
	final String CHILDRENS_TIME_MAX = "QUEST_CHILDRENS_TIME_MAX";
	final String FINAL_TIME_MIN = "QUEST_FINAL_TIME_MIN";
	final String FINAL_TIME_MAX = "QUEST_FINAL_TIME_MAX";
	final String SHOUT_TIME = "QUEST_SHOUT_TIME";

	/**
	 * List of game zones, where rats will appears.
	 *
	 * TODO: add other Ados buildings here, and improve summonRats() function
	 *       to avoid placing rats inside closed areas within houses.
	 */
	public final List<String> RAT_ZONES = Arrays.asList(
// can't be used because NPC can block creature
//			"int_ados_bank",
			"int_ados_bar",
//			"int_ados_bar_1",
			"int_ados_barracks_0",
			"int_ados_barracks_1",
			"int_ados_bakery",
			"int_ados_carolines_house_0",
			"int_ados_church_0",
			"int_ados_church_1",
//			"int_ados_felinas_house",
			"int_ados_fishermans_hut_north",
			"int_ados_goldsmith",
//			"int_ados_haunted_house",
			"int_ados_library",
			"int_ados_meat_market",
			"int_ados_ross_house",
			"int_ados_sewing_room",
//			"int_ados_storage",
//			"int_ados_tavern_0",
			"int_ados_town_hall",
			"int_ados_town_hall_1",
			"int_ados_town_hall_2",
			"int_ados_town_hall_3",
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
