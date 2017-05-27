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
package games.stendhal.server.maps.quests.houses;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/** House owners are offered the chance to buy a spare key when the seller greets them. Others are just greeted with their name. */
final class HouseSellerGreetingAction extends HouseChatAction implements ChatAction {
	protected HouseSellerGreetingAction(final String questslot) {
		super(questslot);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String reply = "";

		if (HouseUtilities.playerOwnsHouse(player)) {
			reply = " At the cost of "
				+ HouseChatAction.COST_OF_SPARE_KEY
				+ " money you can purchase a spare key for your house. Do you want to buy one now?";
			raiser.setCurrentState(ConversationStates.QUESTION_1);
		} else if (player.hasQuest(questslot)) {
			// the player has lost the house. clear the slot so that he can buy a new one if he wants
			player.removeQuest(questslot);
		}

		raiser.say("Hello, " + player.getTitle() + "." + reply);
	}

}
