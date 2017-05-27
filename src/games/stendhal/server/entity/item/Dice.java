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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.player.Player;

public class Dice extends Item {

	private static final int NUMBER_OF_DICE = 3;

	private int[] topFaces;

	private CroupierNPC croupierNPC;

	public Dice(final Map<String, String> attributes) {
		super("dice", "misc", "dice", attributes);
		randomize();
	}

	public Dice() {
		this((Map<String, String>) null);
	}

	/**
	 * copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public Dice(final Dice item) {
		super(item);
		randomize();
	}

	public void setCroupierNPC(final CroupierNPC croupierNPC) {
		this.croupierNPC = croupierNPC;
		setInfoString(croupierNPC.getName());
	}

	/**
	 * When the player gets the dice, then disconnects and reconnects, the
	 * CroupierNPC is lost. That's why we store the croupier's name in the
	 * item's infostring. This method will read out that infostring and set the
	 * croupier to the NPC with that name.
	 *
	 * I tried to do this in the constructor, but somehow it didn't work: the
	 * item somehow seems to not have an infostring while the constructor is
	 * running.
	 */
	private void updateCroupierNPC() {
		if (croupierNPC == null) {
			final String name = getInfoString();

			if (name != null) {
				croupierNPC = (CroupierNPC) SingletonRepository.getNPCList().get(name);
			}
		}
	}

	/**
	 * Get a list of the top faces as a readable string.
	 *
	 * @return list of top faces
	 */
	private String getTopFacesString() {
		final List<String> topFacesStrings = new LinkedList<String>();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			topFacesStrings.add(Integer.toString(topFaces[i]));
		}
		return Grammar.enumerateCollection(topFacesStrings);
	}

	/**
	 * Get the sum of the thrown dice.
	 *
	 * @return sum of the set of dices
	 */
	public int getSum() {
		int result = 0;
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			result += topFaces[i];
		}
		return result;
	}

	/**
	 * Player throws the dice, get the new values on the faces.
	 *
	 * @param player player throwing the dice
	 */
	private void randomize(final Player player) {
		topFaces = new int[NUMBER_OF_DICE];
		double karma = player.useKarma(2);
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			int topFace = Rand.roll1D6();
			// if the player has no karma or bad karma, then re-roll a 6
			if (topFace == 6 && karma <= 0) {
				topFace = Rand.roll1D6();
			}
			topFaces[i] = topFace;
		}
		// if the player has no karma or bad karma, don't give them the booby prize
		if (getSum() == NUMBER_OF_DICE && karma <= 0) {
			// topFaces must have been 1,1,1...1, so just change one of them.
			topFaces[0] = 2;
		}
	}

	/**
	 * A new die needs values on the faces
	 */
	private void randomize() {
		topFaces = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			int topFace = Rand.roll1D6();
			topFaces[i] = topFace;
		}
	}

	@Override
	public void onPutOnGround(final Player player) {
		super.onPutOnGround(player);
		randomize(player);
		updateCroupierNPC();
		if (croupierNPC != null) {
			croupierNPC.onThrown(this, player);
		}
	}

	@Override
	public String describe() {
		return "You see a set of dice. The top faces are "
				+ getTopFacesString() + ".";
	}
}
