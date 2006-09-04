/* $Id$ */
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
package games.stendhal.server.entity.item;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class Dice extends Item {
	
	private static int NUMBER_OF_DICE = 3;
	
	private int[] topFaces;
	
	private CroupierNPC croupierNPC;
	
	public Dice(Map<String, String> attributes) {
		super("dice", "misc", "dice", attributes);
		randomize(null);
	}

	public Dice() {
		super("dice", "misc", "dice", null);
		randomize(null);
	}
	
	public void setCroupierNPC(CroupierNPC croupierNPC) {
		this.croupierNPC = croupierNPC;
		put("infostring", croupierNPC.getName());
	}
	
	/**
	 * When the player gets the dice, then disconnects and reconnects,
	 * the CroupierNPC is lost. That's why we store the croupier's name
	 * in the item's infostring. This method will read out that infostring
	 * and set the croupier to the NPC with that name.
	 * 
	 * I tried to do this in the constructor, but somehow it didn't work:
	 * the item somehow seems to not have an infostring while the constructor
	 * is running.  
	 */
	private void updateCroupierNPC() {
		if (croupierNPC == null && has("infostring")) {
			String name = get("infostring");
			croupierNPC = (CroupierNPC) NPCList.get().get(name); 
		}
	}
	
	public int[] getTopFaces() {
		return topFaces;
	}
	
	public String getTopFacesString() {
		List<String> topFacesStrings = new LinkedList<String>();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			topFacesStrings.add(Integer.toString(topFaces[i]));
		}
		return SpeakerNPC.enumerateCollection(topFacesStrings);
	}
	
	public int getSum() {
		int result = 0;
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			result += topFaces[i];
		}
		return result;
	}
	
	private void randomize(Player player) {
		topFaces = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			int topFace = Rand.roll1D6();
			topFaces[i] = topFace;
		}
		updateCroupierNPC();
		if (croupierNPC != null) {
			croupierNPC.onThrown(this, player);
		}
	}
	
	@Override
	public void onPutOnGround(Player player) {
		super.onPutOnGround(player);
		randomize(player);
	}
	
	@Override
	public String describe() {
		
		return "You see a set of dice. The top faces are "
				+ getTopFacesString()
				+ ".";
	}
}
