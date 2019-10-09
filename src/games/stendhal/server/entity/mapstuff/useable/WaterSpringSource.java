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

import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.RPClass;

/**
 * A water spring source is a spot where a player can fill an empty flask with spring water.
 *
 * Filling an empty flask takes 10 seconds + randomized 4 seconds; during this time, the player keep standing next to
 * the water spring.
 *
 * @author Vanessa Julius (based on WellSource by kymara)
 *
 */
public class WaterSpringSource extends PlayerActivityEntity {
	/**
	 * The reward
	 */
	private static final String[] items = { "water" };

	/**
	 * The chance that filling flask is successful.
	 */
	private static final double FINDING_PROBABILITY = 0.50;

	/**
	 * How long it takes to fill the bottle (in seconds).
	 */
	private static final int DURATION = 10;

	/**
	 * Sound effects
	 */
	private final String startSound = "liquid-fill-01";
	private final String successSound = "cork-pop-1";
	private final String failSound = "glass-break-2";
	private final int SOUND_RADIUS = 20;

	/**
	 * Create a water spring source.
	 */
	public WaterSpringSource() {
		put("class", "source");
		put("name", "water_source");
		setMenu("Fill|Use");
		setDescription("You see some bubbles in the water. Seems like you found a spring.");
		setResistance(0);
	}

	/**
	 * source name.
	 */
	@Override
	public String getName() {
		return("water spring");
	}

	//
	// WaterSpringSource
	//

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("water_source");
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
		return DURATION + Rand.rand(5);
	}

	/**
	 * Decides if the activity can be done.
	 *
	 * @return <code>true</code> if successful.
	 */
	@Override
	protected boolean isPrepared(final Player player) {
        /*
        * The player can fill an empty flask at the spring.
        * Check they have it before they can start filling it up.
		*/
		if (player.isEquipped("flask")) {
			return true;
		} else {
			player.sendPrivateText("You need a flask to fill some water up.");
			return false;
		}
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
        * We want to use up quite a bit karma at once, so scale it after
		*/
		double karma = player.useKarma(FINDING_PROBABILITY*10);

		// if player karma is > 0 it will always return at least 20% of FINDING_PROBABILITY*10, or karma, whichever is smaller
		// so we can safely say if the karma returned is <= 0, the player had <= 0 karma.
		// so we'll penalise these stronger
		if (karma <= 0) {
			karma = karma - FINDING_PROBABILITY*5;
		}
		karma = karma / 10;
		// right hand side could be negative, if karma was negative but thats ok.
		return random <= (FINDING_PROBABILITY + karma) * 100;
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
			final String itemName = items[Rand.rand(items.length)];
			final Item item = SingletonRepository.getEntityManager().getItem(itemName);
			int amount = 1;
			if (itemName.equals("water"))
					 {
				/*
				 * Bound powerful items.
				 */
				item.setBoundTo(player.getName());

			}

            this.addEvent(new SoundEvent(successSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
    		this.notifyWorldAboutChanges();


			player.equipOrPutOnGround(item);
			player.sendPrivateText("You were lucky and filled "
					+ Grammar.quantityplnoun(amount, itemName, "a")+ ".");

		} else {
            this.addEvent(new SoundEvent(failSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
    		this.notifyWorldAboutChanges();

			player.sendPrivateText("Oh no! You spilled the water and let the flask fall into it. Now it's broken.");
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
	    // Play a nice sound effect
        addEvent(new SoundEvent(startSound, SOUND_RADIUS, 100, SoundLayer.AMBIENT_SOUND));
        notifyWorldAboutChanges();

		// remove flask from player as they try to fill it.
		player.drop("flask");
		player.sendPrivateText("You started to fill fresh spring water into an empty flask. It will hopefully not slip out of your hand!");
	}
}
