/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.entity.npc;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * Represents the behaviour of a NPC who is able to sell masks
 * to a player.
 */
public class MakeupArtistBehaviour extends MerchantBehaviour {
	private String questSlot = null;
	
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(MakeupArtistBehaviour.class);

	/**
	 * Creates a new SellerBehaviour with an empty pricelist.
	 *
	 * @param questSlot quest-slot to store time the mask will stay
	 */
	public MakeupArtistBehaviour(String questSlot) {
		super(new HashMap<String, Integer>());
		this.questSlot = questSlot;
	}

	/**
	 * Creates a new MakeupArtistBehaviour with a pricelist.
	 *
	 * @param questSlot quest-slot to store time the mask will stay
	 * @param world the world
	 */
	public MakeupArtistBehaviour(String questSlot, Map<String, Integer> priceList) {
		super(priceList);
		this.questSlot = questSlot;
	}

	/**
	 * Transacts the sale that has been agreed on earlier via
	 * setChosenItem() and setAmount().
	 *
	 * @param seller The NPC who sells
	 * @param player The player who buys
	 * @return true iff the transaction was successful, that is when the
	 *              player was able to equip the item(s).
	 */
	@Override
	protected boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();
		EntityManager manager = world.getRuleManager().getEntityManager();

		Item item = manager.getItem(chosenItem);
		if (item == null) {
			logger.error("Trying to sell an nonexistant item: " + chosenItem);
			return false;
		}

		// When the user tries to buy several of a non-stackable item,
		// he is forced to buy only one.
		setAmount(1);

		if (player.isEquipped("money", getCharge(player))) {
			player.drop("money", getCharge(player));

			// apply the mask to the outfit
			int outfit = player.getInt("outfit");
			if (!player.has("outfit_org")) {
				player.put("outfit_org", outfit);
			}
			// hair, head, outfit, body
			int randomHead = Rand.rand(5);
			int head = 80 + randomHead;
			outfit = 00 * 1000000 + head * 10000 + (outfit % 10000);
			player.put("outfit", outfit);
			player.setQuest(questSlot, Long.toString(System.currentTimeMillis() + 30 * 60 * 1000));

			return true;
		} else {
			seller.say("Sorry, you don't have enough money!");
			return false;
		}
	}
}
