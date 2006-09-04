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
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class Dice extends Item {
	
	private int NUMBER_OF_DICE = 3;
	
	private int[] topFaces;
	
	public Dice(Map<String, String> attributes) {
		super("dice", "misc", "dice", attributes);
		randomize();
	}

	public Dice(int quantity) {
		super("dice", "misc", "dice", null);
		randomize();
	}
	
	
	private void randomize() {
		topFaces = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			int topFace = Rand.roll1D6();
			topFaces[i] = topFace;
		}
		System.out.println("randomized");
	}
	
	@Override
	public void onPutOnGround(Player player) {
		super.onPutOnGround(player);
		randomize();
		System.out.println("put on ground");
	}
	
	@Override
	public String describe() {
		List<String> topFaceStrings = new LinkedList<String>();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			topFaceStrings.add(Integer.toString(topFaces[i]));
		}
		
		return "You see a set of dice. The top faces are "
				+ SpeakerNPC.enumerateCollection(topFaceStrings)
				+ ".";
	}
}
