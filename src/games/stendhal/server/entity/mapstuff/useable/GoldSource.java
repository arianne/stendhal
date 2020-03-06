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
package games.stendhal.server.entity.mapstuff.useable;

import org.apache.log4j.Logger;

//
//

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPClass;

/**
 * A gold source is a spot where a player can prospect for gold nuggets. He
 * needs a gold pan, time, and luck.
 *
 * Prospecting takes 7-11 seconds; during this time, the player keep standing
 * next to the gold source. In fact, the player only has to be there when the
 * prospecting action has finished. Therefore, make sure that two gold sources
 * are always at least 5 sec of walking away from each other, so that the player
 * can't prospect for gold at several sites simultaneously.
 *
 * Some karma is used to decide if the player was successful at the well or not.
 *
 * @author daniel
 */
public class GoldSource extends PlayerActivityEntity {
	private static final Logger logger = Logger.getLogger(GoldSource.class);

	/**
	 * The equipment needed.
	 */
	private static final String NEEDED_EQUIPMENT = "gold pan";

	/**
	 * The chance that prospecting is successful.
	 */
	private static final double FINDING_PROBABILITY = 0.1;

	/**
	 * The name of the item to be found.
	 */
	private final String itemName;

	/**
	 * Create a gold source.
	 */
	public GoldSource() {
		this("gold nugget");
	}

	/**
	 * source name.
	 */
	@Override
	public String getName() {
		return("gold rich water");
	}

	/**
	 * Create a gold source.
	 *
	 * @param itemName
	 *            The name of the item to be prospected.
	 */
	public GoldSource(final String itemName) {
		this.itemName = itemName;
		put("class", "source");
		put("name", "gold_source");
		setMenu("Prospect|Use");
		setDescription("You see something golden glittering.");
	}

	//
	// GoldSource
	//

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("gold_source");
		rpclass.isA("entity");
	}

	//
	// PlayerActivityEntity
	//

	/**
	 * Get the time it takes to perform this activity.
	 *
	 * @return The time to perform the activity (in seconds).
	 */
	@Override
	protected int getDuration() {
		return 7 + Rand.rand(4);
	}

	/**
	 * Decides if the activity can be done.
	 *
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isPrepared(final Player player) {
		if (player.isEquipped(NEEDED_EQUIPMENT)) {
			return true;
		}

		player.sendPrivateText("You need a gold pan to prospect for gold.");
		return false;
	}

	/**
	 * Decides if the activity was successful.
	 *
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isSuccessful(final Player player) {
		final int random = Rand.roll1D100();
        /*
        * Use some karma to help decide if the outcome is successful
		*/
        return random <= (FINDING_PROBABILITY + player.useKarma(FINDING_PROBABILITY)) * 100;
	}

	/**
	 * Called when the activity has finished.
	 *
	 * @param player
	 *            The player that did the activity.
	 * @param successful
	 *            If the activity was successful.
	 */
	@Override
	protected void onFinished(final Player player, final boolean successful) {
		if (successful) {
			final Item item = SingletonRepository.getEntityManager().getItem(itemName);

			if (item != null) {
			    player.equipOrPutOnGround(item);
			    player.incMinedForItem(item.getName(), item.getQuantity());
			    player.sendPrivateText("You found "
					+ Grammar.a_noun(item.getTitle()) + ".");
			} else {
				logger.error("could not find item: " + itemName);
			}
		} else {
			player.sendPrivateText("You didn't find anything.");
		}
	}

	/**
	 * Called when the activity has started.
	 *
	 * @param player
	 *            The player starting the activity.
	 */
	@Override
	protected void onStarted(final Player player) {
		player.sendPrivateText("You have started to prospect for gold.");
	}
}
