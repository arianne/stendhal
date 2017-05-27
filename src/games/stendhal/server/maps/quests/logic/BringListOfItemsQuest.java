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
package games.stendhal.server.maps.quests.logic;

import java.util.List;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * a quest which is based on bringing a list of item to an NPC.
 *
 * @author hendrik
 */
public interface BringListOfItemsQuest {

	/**
	 * the NPC which wants the items.
	 *
	 * @return SpeakerNPC
	 */
	SpeakerNPC getNPC();

	List<String> getNeededItems();

	String getSlotName();

	List<String> getTriggerPhraseToEnumerateMissingItems();

	List<String> getAdditionalTriggerPhraseForQuest();

	double getKarmaDiffForQuestResponse();

	String welcomeBeforeStartingQuest();

	String welcomeDuringActiveQuest();

	String welcomeAfterQuestIsCompleted();

	boolean shouldWelcomeAfterQuestIsCompleted();

	String respondToQuest();

	String respondToQuestAfterItHasAlreadyBeenCompleted();

	/**
	 * Note: This needs to include the trigger phrase which displays the list
	 * of missing items.
	 *
	 * @return response
	 */
	String respondToQuestAcception();

	String respondToQuestRefusal();

	String askForMissingItems(List<String> missingItems);

	String firstAskForMissingItems(List<String> missingItems);

	String respondToPlayerSayingHeHasNoItems(List<String> missingItems);

	String askForItemsAfterPlayerSaidHeHasItems();

	String respondToItemBrought();

	String respondToLastItemBrought();

	void rewardPlayer(Player player);

	String respondToOfferOfNotExistingItem(String itemName);

	String respondToOfferOfNotMissingItem();

	String respondToOfferOfNotNeededItem();

}
