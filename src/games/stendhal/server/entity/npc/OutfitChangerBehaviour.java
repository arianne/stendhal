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
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to sell outfits
 * to a player.
 */
public class OutfitChangerBehaviour extends MerchantBehaviour implements TurnListener, LoginListener {

	private static final int NO_CHANGE = -1;

	public static final int NEVER_WEARS_OFF = -1;

	private int endurance;
	
	private String wearOffMessage;
	
	// all available outfit types are predefined here.
	private static Map<String, int[][]> outfitTypes = new HashMap<String, int[][]>();
	static {
		// In each line, there is one possible outfit of this
		// outfit type, in the format: hair, head, dress, base.
		// One of these outfit will be chosen randomly.
		int[][] maleSwimsuits = {
				{NO_CHANGE, NO_CHANGE, 95, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 96, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 97, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 98, NO_CHANGE}
		};
		outfitTypes.put("male_swimsuit", maleSwimsuits);
		
		int[][] femaleSwimsuits = {
				{NO_CHANGE, NO_CHANGE, 91, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 92, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 93, NO_CHANGE},
				{NO_CHANGE, NO_CHANGE, 94, NO_CHANGE}
		};
		outfitTypes.put("female_swimsuit", femaleSwimsuits);
		
		int[][] masks = {
				{NO_CHANGE, 80, NO_CHANGE, NO_CHANGE},
				{NO_CHANGE, 81, NO_CHANGE, NO_CHANGE},
				{NO_CHANGE, 82, NO_CHANGE, NO_CHANGE},
				{NO_CHANGE, 83, NO_CHANGE, NO_CHANGE},
				{NO_CHANGE, 84, NO_CHANGE, NO_CHANGE},
		};
		outfitTypes.put("mask", masks);

		int[][] pizzaDeliveryUniform = {
				{NO_CHANGE, NO_CHANGE, 90, NO_CHANGE},
		};
		outfitTypes.put("pizza_delivery_uniform", pizzaDeliveryUniform);
	}
	
	/**
	 * Creates a new OutfitChangerBehaviour for outfits never wear off
	 * automatically.
	 *
	 * @param priceList list of outfit types and their prices
	 */
	public OutfitChangerBehaviour(Map<String, Integer> priceList) {
		this(priceList, NEVER_WEARS_OFF, null);
	}

	/**
	 * Creates a new OutfitChangerBehaviour for outfits that wear off
	 * automatically after some time.
	 *
	 * @param priceList list of outfit types and their prices
	 * @param endurance the time (in turns) the outfit will stay, or
	 * 					DONT_WEAR_OFF if the outfit should never disappear
	 * 				    automatically.
	 * @param wearOffMessage the message that the player should receive after
	 * 					the outfit has worn off, or null if no message should
	 * 					be sent.
	 */
	public OutfitChangerBehaviour(Map<String, Integer> priceList, int endurance, String wearOffMessage) {
		super(priceList);
		this.endurance = endurance;
		this.wearOffMessage = wearOffMessage;
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
		String outfitType = chosenItem;
		if (player.isEquipped("money", getCharge(player))) {
			player.drop("money", getCharge(player));
			putOnOutfit(player, outfitType);
			return true;
		} else {
			seller.say("Sorry, you don't have enough money!");
			return false;
		}
	}

	/**
	 * Tries to get back the bought/lent outfit and give the player
	 * his original outfit back.
	 * This will only be successful if the player is wearing an outfit
	 * he got here, and if the original outfit has been stored.
	 * @param player The player.
	 * @return true iff returning was successful.
	 */
	public void putOnOutfit(Player player, String outfitType) {
		// apply the outfit to the player
		int oldOutfitIndex = player.getInt("outfit");
		int[][] newOutfitPossibilities = outfitTypes.get(outfitType);
		int[] newOutfitParts = newOutfitPossibilities[Rand.rand(newOutfitPossibilities.length)];
		int newHairIndex = newOutfitParts[0];
		int newHeadIndex = newOutfitParts[1];
		int newDressIndex = newOutfitParts[2];
		int newBaseIndex = newOutfitParts[3];

		// Store old outfit so that it is preselected in "Set Outfit"
		// dialog. Some players cannot remeber the details of their
		// original outfit. Important: If the variable is already set,
		// it must not be changed. This means that the player has choosen
		// another special outfit before. It is removed when the original
		// outfit is restored by the quest-timer or the outfit is changed
		// by the "Set Outfit" dialog. 
		if (!player.has("outfit_org")) {
		 	player.put("outfit_org", player.get("outfit"));
		}

		if (newBaseIndex == NO_CHANGE) {
			newBaseIndex = oldOutfitIndex % 100;
		}
		oldOutfitIndex /= 100;
		if (newDressIndex == NO_CHANGE) {
			newDressIndex = oldOutfitIndex % 100;
		}
		oldOutfitIndex /= 100;
		if (newHeadIndex == NO_CHANGE) {
			newHeadIndex = oldOutfitIndex % 100;
		}
		oldOutfitIndex /= 100;
		if (newHairIndex == NO_CHANGE) {
			newHairIndex = oldOutfitIndex;
		}
		
		// hair, head, outfit, body
		int newOutfitIndex = newHairIndex * 1000000 + newHeadIndex * 10000 + newDressIndex * 100 + newBaseIndex;
		player.put("outfit", newOutfitIndex);
		player.notifyWorldAboutChanges();
		//player.setQuest(questSlot, Long.toString(System.currentTimeMillis() + 30 * 60 * 1000));
		
		if (endurance != NEVER_WEARS_OFF) {
			// make the costume disappear after some time
			TurnNotifier.get().notifyInTurns(endurance, this, player.getName());
		}
	}
	
	/**
	 * Checks whether or not the given player is currently
	 * wearing an outfit that may have been bought/lent from an
	 * NPC with this behaviour.
	 * @param player The player.
	 * @return true iff the player wears an outfit from here.
	 */
	public boolean wearsOutfitFromHere(Player player) {
		int currentOutfitIndex = player.getInt("outfit");
		int[] currentOutfitParts = new int[4];
		
		currentOutfitParts[3] = currentOutfitIndex % 100;
		currentOutfitIndex /= 100;
		currentOutfitParts[2] = currentOutfitIndex % 100;
		currentOutfitIndex /= 100;
		currentOutfitParts[1] = currentOutfitIndex % 100;
		currentOutfitIndex /= 100;
		currentOutfitParts[0] = currentOutfitIndex;
		
		for (String outfitType: priceList.keySet()) { 
			int[][] outfitPossibilities = outfitTypes.get(outfitType);
			for (int[] outfitPossibility: outfitPossibilities) {
				if ((outfitPossibility[0] == NO_CHANGE
								|| outfitPossibility[0] == currentOutfitParts[0])
						&& (outfitPossibility[1] == NO_CHANGE
								|| outfitPossibility[1] == currentOutfitParts[1])
						&& (outfitPossibility[2] == NO_CHANGE
								|| outfitPossibility[2] == currentOutfitParts[2])
						&& (outfitPossibility[3] == NO_CHANGE
								|| outfitPossibility[3] == currentOutfitParts[3])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tries to get back the bought/lent outfit and give the player
	 * his original outfit back.
	 * This will only be successful if the player is wearing an outfit
	 * he got here, and if the original outfit has been stored.
	 * @param player The player.
	 * @return true iff returning was successful.
	 */
	public boolean returnToOriginalOutfit(Player player) {
		if (wearsOutfitFromHere(player) && player.has("outfit_org")) {
			player.put("outfit", player.get("outfit_org"));
			player.remove("outfit_org");
			player.notifyWorldAboutChanges();
			return true;
		}
		return false;
	}
	
	protected void onWornOff(Player player) {
		player.sendPrivateText(wearOffMessage);
		returnToOriginalOutfit(player);
	}

	public void onTurnReached(int currentTurn, String message) {
		String playerName = message;
		Player player = StendhalRPRuleProcessor.get().getPlayer(playerName);
		if (player != null) {
			onWornOff(player);
		} else {
			// The player has logged out before the outfit wore off.
			// Remove it when the player logs in again.
			LoginNotifier.get().notifyOnLogin(playerName, this, null);
		}
	}

	public void onLoggedIn(String playerName, String message) {
		Player player = StendhalRPRuleProcessor.get().getPlayer(playerName);
		onWornOff(player);
	}
}
