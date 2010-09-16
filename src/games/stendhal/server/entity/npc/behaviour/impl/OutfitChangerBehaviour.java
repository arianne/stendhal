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

package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to sell outfits to a player.
 */
public class OutfitChangerBehaviour extends MerchantBehaviour implements
		LoginListener {
	protected static final int NEVER_WEARS_OFF = -1;
	public int endurance;

	private final String wearOffMessage;

	// TODO: make this persistent, e.g. by replacing this list with one
	// quest slot reserved for each OutfitChangerBehaviour.
	List<String> namesOfPlayersWithWornOffOutfits;

	// all available outfit types are predefined here.
	private static Map<String, List<Outfit>> outfitTypes = new HashMap<String, List<Outfit>>();
	static {
		// In each line, there is one possible outfit of this
		// outfit type, in the order: hair, head, dress, base.
		// One of these outfit will be chosen randomly.

		// swimsuits for men
		outfitTypes.put("trunks", Arrays.asList(
				new Outfit(null, null, null, 95, null), new Outfit(null, null, null, 96,
						null), new Outfit(null, null, null, 97, null), new Outfit(
								null, null, null, 98, null)));

		// swimsuits for women
		outfitTypes.put("swimsuit", Arrays.asList(new Outfit(null, null, null, 91,
				null), new Outfit(null, null, null, 92, null), new Outfit(null, null, null,
				93, null), new Outfit(null, null, null, 94, null)));

		outfitTypes.put("mask", Arrays.asList(new Outfit(null, 0, 80, null, null),
				new Outfit(null, 0, 81, null, null), new Outfit(null, 0, 82, null, null),
				new Outfit(null, 0, 83, null, null), new Outfit(null, 0, 84, null, null),
				new Outfit(null, 0, 85, null, null)));

		// wedding dress for brides
		// it seems this must be an array as list even though it's only one item
		outfitTypes.put("gown", Arrays.asList(new Outfit(null, null, null, 88, null)));

		// // wedding suit for grooms
		// it seems this must be an array as list even though it's only one item
		outfitTypes.put("suit", Arrays.asList(new Outfit(null, null, null, 87, null)));
	}

	/**
	 * Creates a new OutfitChangerBehaviour for outfits that never wear off
	 * automatically.
	 * 
	 * @param priceList
	 *            list of outfit types and their prices
	 */
	public OutfitChangerBehaviour(final Map<String, Integer> priceList) {
		this(priceList, NEVER_WEARS_OFF, null);
	}

	/**
	 * Creates a new OutfitChangerBehaviour for outfits that wear off
	 * automatically after some time.
	 * 
	 * @param priceList
	 *            list of outfit types and their prices
	 * @param endurance
	 *            the time (in turns) the outfit will stay, or NEVER_WEARS_OFF
	 *            if the outfit should never disappear automatically.
	 * @param wearOffMessage
	 *            the message that the player should receive after the outfit
	 *            has worn off, or null if no message should be sent.
	 */
	public OutfitChangerBehaviour(final Map<String, Integer> priceList,
			final int endurance, final String wearOffMessage) {
		super(priceList);
		this.endurance = endurance;
		this.wearOffMessage = wearOffMessage;
		if (endurance != NEVER_WEARS_OFF) {
			SingletonRepository.getLoginNotifier().addListener(this);
			namesOfPlayersWithWornOffOutfits = new ArrayList<String>();
		}
	}

	/**
	 * Transacts the sale that has been agreed on earlier via setChosenItem()
	 * and setAmount().
	 * 
	 * @param seller
	 *            The NPC who sells
	 * @param player
	 *            The player who buys
	 * @return true iff the transaction was successful, that is when the player
	 *         was able to equip the item(s).
	 */
	@Override
	public boolean transactAgreedDeal(final EventRaiser seller, final Player player) {
		final String outfitType = chosenItemName;
		if (player.getOutfit().getBase() > 80 && player.getOutfit().getBase() < 99) {
			// if the player is wearing a non standard player base  
			// then swimsuits, masks and many other outfits wouldn't look good mixed with it
			seller.say("You already have a magic outfit on which just wouldn't look good with another - could you please put yourself in something more conventional and ask again? Thanks!");
			return false;
		}
		if (player.isEquipped("money", getCharge(player))) {
			player.drop("money", getCharge(player));
			putOnOutfit(player, outfitType);
			return true;
		} else {
			seller.say("Sorry, you don't have enough money!");
			return false;
		}
	}

	protected class OutwearClothes implements TurnListener {
		WeakReference<Player> ref;
		String name;

		public OutwearClothes(final Player player) {
			ref = new WeakReference<Player>(player);
			name = player.getName();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof OutwearClothes) {
				OutwearClothes other = (OutwearClothes) obj;
				return name.equals(other.name);
			} else {
				return false;
			}

		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		public void onTurnReached(final int currentTurn) {
			Player player = ref.get();
			if ((player == null) || player.isDisconnected()) {
				player = SingletonRepository.getRuleProcessor().getPlayer(name);
			}
			if (player != null) {
				onWornOff(player);
			} else {
				// The player has logged out before the outfit wore off.
				// Remove it when the player logs in again.
				namesOfPlayersWithWornOffOutfits.add(name);
			}
		}

	}

	/**
	 * Tries to get back the bought/lent outfit and give the player his original
	 * outfit back. This will only be successful if the player is wearing an
	 * outfit he got here, and if the original outfit has been stored.
	 * 
	 * @param player
	 *            The player.
	 * @param outfitType the outfit to wear
	 */
	public void putOnOutfit(final Player player, final String outfitType) {
		final List<Outfit> possibleNewOutfits = outfitTypes.get(outfitType);
		final Outfit newOutfit = Rand.rand(possibleNewOutfits);
		player.setOutfit(newOutfit.putOver(player.getOutfit()), true);

		if (endurance != NEVER_WEARS_OFF) {
			// restart the wear-off timer if the player was still wearing
			// another temporary outfit.
			SingletonRepository.getTurnNotifier().dontNotify(new OutwearClothes(player));
			// make the costume disappear after some time
			SingletonRepository.getTurnNotifier().notifyInTurns(endurance,
					new OutwearClothes(player));
		}
	}

	/**
	 * Checks whether or not the given player is currently wearing an outfit
	 * that may have been bought/lent from an NPC with this behaviour.
	 * 
	 * @param player
	 *            The player.
	 * @return true iff the player wears an outfit from here.
	 */
	public boolean wearsOutfitFromHere(final Player player) {
		final Outfit currentOutfit = player.getOutfit();

		for (final String outfitType : priceList.keySet()) {
			final List<Outfit> possibleOutfits = outfitTypes.get(outfitType);
			for (final Outfit possibleOutfit : possibleOutfits) {
				if (possibleOutfit.isPartOf(currentOutfit)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tries to get back the bought/lent outfit and give the player his original
	 * outfit back. This will only be successful if the player is wearing an
	 * outfit he got here, and if the original outfit has been stored.
	 * 
	 * @param player
	 *            The player.
	 * @return true iff returning was successful.
	 */
	public boolean returnToOriginalOutfit(final Player player) {
		if (wearsOutfitFromHere(player)) {
			return player.returnToOriginalOutfit();
		}
		return false;
	}

	/**
	 * Puts the outfit off, but only if the player hasn't taken it off himself
	 * already.
	 * @param player who wears the outfit
	 */
	protected void onWornOff(final Player player) {
		if (wearsOutfitFromHere(player)) {
			player.sendPrivateText(wearOffMessage);
			returnToOriginalOutfit(player);
		}
	}

	public void onLoggedIn(final Player player) {
		if (namesOfPlayersWithWornOffOutfits.contains(player.getName())) {
			onWornOff(player);
			namesOfPlayersWithWornOffOutfits.remove(player.getName());
		}
	}
}
