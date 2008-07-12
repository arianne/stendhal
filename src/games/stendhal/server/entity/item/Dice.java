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

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.CroupierNPC;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dice extends Item {

	private static final int NUMBER_OF_DICE = 3;

	private int[] topFaces;

	private CroupierNPC croupierNPC;

	public Dice(final Map<String, String> attributes) {
		super("dice", "misc", "dice", attributes);
		randomize(null);
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
		randomize(null);
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

	public int[] getTopFaces() {
		return topFaces;
	}

	public String getTopFacesString() {
		final List<String> topFacesStrings = new LinkedList<String>();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			topFacesStrings.add(Integer.toString(topFaces[i]));
		}
		return Grammar.enumerateCollection(topFacesStrings);
	}

	public int getSum() {
		int result = 0;
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			result += topFaces[i];
		}
		return result;
	}

	private void randomize(final Player player) {
		topFaces = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			final int topFace = Rand.roll1D6();
			topFaces[i] = topFace;
		}
	}

	@Override
	public void onPutOnGround(final Player player) {
		super.onPutOnGround(player);
		zoneFix(player);
		randomize(player);
		updateCroupierNPC();
		if (croupierNPC != null) {
			croupierNPC.onThrown(this, player);
		}
	}

	// TODO: Find a real fix for this: In Marauroa 1.0 items had a zone even
	// when in player's bags. So the above code in onPutOnGroupd (onThrown)
	// was able to verify the zone before the item is actually put onto the
	// ground and the zone is updated. Bagged items do not have a zone in
	// Marauroa 1.0 so this does not work anymore. We need to change the order
	// in which the dropping of items is implemented.
	private void zoneFix(final Player player) {
		if (getZone() == null) {
			this.onAdded(player.getZone());
		}
	}

	@Override
	public String describe() {
		return "You see a set of dice. The top faces are "
				+ getTopFacesString() + ".";
	}
}
