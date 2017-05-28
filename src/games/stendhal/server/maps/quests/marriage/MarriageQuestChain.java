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
package games.stendhal.server.maps.quests.marriage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.player.Player;


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
public class MarriageQuestChain  {
	private static MarriageQuestInfo marriage = new MarriageQuestInfo();

	private static Logger logger = Logger.getLogger(MarriageQuestChain.class);


	public void addToWorld() {
		new Engagement(marriage).addToWorld();
		new MakeRings(marriage).addToWorld();
		new GetOutfits(marriage).addToWorld();
		new Marriage(marriage).addToWorld();
		new Honeymoon(marriage).addToWorld();
		new Divorce(marriage).addToWorld();
	}

	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(marriage.getQuestSlot())) {
			return res;
		}
		final String questState = player.getQuest(marriage.getQuestSlot());
		res.add("Me and " + getSpouseOrNickname(player) + " met Sister Benedicta and confirmed our engagement to marry.");
		res.add("We must each make a wedding ring to give the other, which Ognir will help with.");
		if ("engaged".equals(questState)) {
			return res;
		}
		res.add("Ognir took the gold I had collected and agreed to forge a wedding ring that I can give to " + getSpouseOrNickname(player)  + ".");
		if (questState.startsWith("forging")) {
			return res;
		}
		res.add("I collected my ring from Ognir. He hinted we could get special outfits from Timothy and Tamara in Fado Hotel.");
		if ("engaged_with_ring".equals(questState)) {
	        res.add("Now I just have to make sure that " + getSpouseOrNickname(player) + " makes a ring, and then we can go to church together.");
			return res;
		}
		res.add("I married " + getSpouseOrNickname(player) + " in a lovely ceremony in Fado Church.");
		if ("just_married".equals(questState)) {
			res.add("We have not yet taken our honeymoon, we should ask Linda about that.");
			return res;
		}
		res.add(getSpouseOrNickname(player) + " and I had a great honeymoon in Fado Hotel, helped by Linda.");
		if ("done".equals(questState)) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for player " + player.getTitle() + " in quest state " + questState);
		return debug;
	}

	private String getSpouseOrNickname(final Player player) {
		String spouse = player.getQuest(marriage.getSpouseQuestSlot());
		if(spouse == null) {
			// probably just engaged so we didn't get a spouse yet, just set this to something generic and sickly sweet
			spouse = Rand.rand(Arrays.asList("my dearest", "my love", "my honeypie", "the one I love"));
		}
		return spouse;
	}

}
