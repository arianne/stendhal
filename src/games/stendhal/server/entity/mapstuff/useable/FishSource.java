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

//
//

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ImageEffectEvent;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.RPClass;

/**
 * A fish source is a spot where a player can fish. He needs a fishing rod, time
 * and luck. Before he catches fish he needs to make a license.
 *
 * Fishing takes 5-9 seconds; during this time, the player keep standing next to
 * the fish source. In fact, the player only has to be there when the
 * prospecting action has finished. Therefore, make sure that two fish sources
 * are always at least 8 sec of walking away from each other, so that the player
 * can't fish at several sites simultaneously.
 *
 * Completion of the Fishermans Collector quest increases the chance of catching fish.
 * Some karma is used to decide the outcome.
 *
 * @author dine
 */
public class FishSource extends PlayerActivityEntity {
	/**
	 * The equipment needed.
	 */
	private static final String NEEDED_EQUIPMENT = "fishing rod";

	/**
	 * The name of the item to be caught.
	 */
	private final String itemName;

	/**
	 * Sound effects
	 */
	private final String startSound = "fishing-1";
	//private String successSound;
	//private String failSound;
	private final int SOUND_RADIUS = 20;

	/**
	 * Create a fish source.
	 *
	 * @param itemName
	 *            The name of the item to be caught.
	 */
	public FishSource(final String itemName) {
		this.itemName = itemName;
		put("class", "source");
		put("name", "fish_source");
		setMenu("Fish|Use");
		setDescription("There is something in the water.");
	}

	/**
	 * source name.
	 */
	@Override
	public String getName() {
		return("fish in the water");
	}

	//
	// FishSource
	//

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("fish_source");
		rpclass.isA("entity");
	}

	/**
	 * Calculates the probability that the given player catches a fish. This is
	 * based on the player's fishing skills, however even players with no skills
	 * at all have a 5% probability of success, before the karma effect.
	 *
	 * @param player
	 *            The player,
	 *
	 * @return The probability of success.
	 */
	private double getSuccessProbability(final Player player) {
		double probability = 0.05;

		final String skill = player.getSkill("fishing");

		if (skill != null) {
			probability = Math.max(probability, MathHelper.parseDouble(skill));
		}

		return probability + player.useKarma(0.05);
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
		return 5 + Rand.rand(4);
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

		player.sendPrivateText("You need a fishing rod for fishing.");
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
		return (random <= (getSuccessProbability(player) * 100));
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
			final Item item = SingletonRepository.getEntityManager().getItem(
					itemName);

			// TODO: find a sound for success
			//this.addEvent(new SoundEvent(successSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
			this.notifyWorldAboutChanges();

			player.equipOrPutOnGround(item);
			player.incHarvestedForItem(itemName, 1);
			player.sendPrivateText("You caught a fish.");
		} else {
		    // TODO: find a sound for failure
            //this.addEvent(new SoundEvent(failSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
			this.notifyWorldAboutChanges();

			player.sendPrivateText("You didn't get a fish.");
		}
		notifyWorldAboutChanges();
	}

	/**
	 * Called when the activity has started.
	 *
	 * @param player
	 *            The player starting the activity.
	 */
	@Override
	protected void onStarted(final Player player) {
	    // Play a nice fishing sound
        addEvent(new SoundEvent(startSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
        notifyWorldAboutChanges();

		// some feedback is needed.
		player.sendPrivateText("You have started fishing.");
		addEvent(new ImageEffectEvent("water_splash", true));
		notifyWorldAboutChanges();
	}
}
