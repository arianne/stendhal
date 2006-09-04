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
	
	public static interface DiceListener {
		public void onThrown(Dice dice, Player player);
	}
	
	private static int NUMBER_OF_DICE = 3;
	
	private int[] topFaces;
	
	private List<DiceListener> listeners;
	
	public Dice(Map<String, String> attributes) {
		super("dice", "misc", "dice", attributes);
		
		listeners = new LinkedList<DiceListener>();
		randomize(null);
	}

	public Dice(int quantity) {
		super("dice", "misc", "dice", null);
		listeners = new LinkedList<DiceListener>();
		randomize(null);
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
	
	public void addDiceListener(DiceListener listener) {
		listeners.add(listener);
	}
	
	private void randomize(Player player) {
		topFaces = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			int topFace = Rand.roll1D6();
			topFaces[i] = topFace;
		}
		for (DiceListener listener: listeners) {
			listener.onThrown(this, player);
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
