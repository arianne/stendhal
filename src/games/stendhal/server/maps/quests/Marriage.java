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
package games.stendhal.server.maps.quests;

import games.stendhal.server.maps.quests.marriage.MarriageQuestChain;

/**
 * QUEST: Marriage
 * <p>
 * PARTICIPANTS:
 * <li> Sister Benedicta, the nun of Fado Church
 * <li> the Priest of Fado Church
 * <li> Ognir, the Ring Maker in Fado
 * <p>
 * STEPS:
 * <li> The nun explains that when two people are married, they can be together
 * whenever they want
 * <li> When two players wish to become engaged, they tell the nun
 * <li> The nun gives them invitation scrolls for the wedding, marked with the
 * church
 * <li>The players get a wedding ring made to give the other at the wedding
 * <li> They can get dressed into an outfit in the hotel
 * <li> When an engaged player goes to the priest, he knows they are there to be
 * married
 * <li> The marriage rites are performed
 * <li> The players are given rings
 * <li> When they go to the Hotel they choose a lovers room
 * <li> Champagne and fruit baskets is put in their bag (room if possible)
 * <li> They leave the lovers room when desired with another marked scroll
 * 
 * <p>
 * REWARD:
 * <li> Wedding Ring that teleports you to your spouse if worn - 1500 XP in
 * total
 * <li> nice food in the lovers room
 * <p>
 * 
 * REPETITIONS:
 * <li> None.
 * 
 * @author kymara
 */
public class Marriage extends AbstractQuest {
	private static final String QUEST_SLOT = "marriage";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Marriage",
				"Did you find the partner with whom you want to spent your life in Faiumoni with? Then make the next bigger step and tie the knots!",
				false);
		MarriageQuestChain marriage = new MarriageQuestChain();
		marriage.addToWorld();
	}


	@Override
	public String getName() {
		return "Marriage";
	}

}
