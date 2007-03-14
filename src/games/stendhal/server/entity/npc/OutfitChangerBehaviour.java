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
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to sell masks
 * to a player.
 */
public class OutfitChangerBehaviour extends MerchantBehaviour {

	// TODO: integrate server.maps.quests.CostumeParty
	
	private static final int NO_CHANGE = -1;
	//private int endurance;
	
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

	}
	
	/**
	 * Creates a new OutfitChangerBehaviour with an empty pricelist.
	 *
	 * @param endurance the time (in turns) the outfit will stay,
	 * 		  			or -1 if the outfit should never disappear
	 * 				    automatically.
	 */
	public OutfitChangerBehaviour(/*int endurance*/) {
		super(new HashMap<String, Integer>());
		//this.endurance = endurance;
	}

	/**
	 * Creates a new OutfitChangerBehaviour with a pricelist.
	 *
	 * @param endurance the time (in turns) the outfit will stay,
	 * 		  			or -1 if the outfit should never disappear
	 * 				    automatically.
	 * @param priceList list of item names and their prices
	 */
	public OutfitChangerBehaviour(/*int endurance,*/ Map<String, Integer> priceList) {
		super(priceList);
		//this.endurance = endurance;
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

			// apply the mask to the outfit
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

			return true;
		} else {
			seller.say("Sorry, you don't have enough money!");
			return false;
		}
	}
}
