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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/** The sale of a spare key has been agreed, player meets conditions,
 * here is the action to simply sell it. */
final class BuySpareKeyChatAction extends HouseChatAction implements ChatAction {


	protected BuySpareKeyChatAction(final String questslot) {
		super(questslot);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (player.isEquipped("money", HouseChatAction.COST_OF_SPARE_KEY)) {

			final String housenumber = player.getQuest(questslot);
			final Item key = SingletonRepository.getEntityManager().getItem(
																			"house key");
			final int number = MathHelper.parseInt(housenumber);
			final HousePortal houseportal = HouseUtilities.getHousePortal(number);

			if (houseportal == null) {
				// something bad happened
				raiser.say("Sorry something bad happened. I'm terribly embarassed.");
				return;
			}

			final int locknumber = houseportal.getLockNumber();
			final String doorId = houseportal.getDoorId();
			((HouseKey) key).setup(doorId, locknumber, player.getName());

			if (player.equipToInventoryOnly(key)) {
				player.drop("money", HouseChatAction.COST_OF_SPARE_KEY);
				raiser.say("Here you go, a spare key to your house. Please remember, only give spare keys to people you #really, #really, trust! Anyone with a spare key can access your chest, and tell anyone that you give a key to, to let you know if they lose it. If that happens, you should #change your locks.");
			} else {
				raiser.say("Sorry, you can't carry more keys!");
			}
		} else {
			raiser.say("You do not have enough money for another key!");
		}
	}
}
