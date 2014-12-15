/***************************************************************************
 *                (C) Copyright 2003-2014 - Faiumoni e.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.events.SoundEvent;

/**
 * Entity that manages weather properties that are better handled as events than
 * as zone attribute changes.
 */
public class WeatherEntity extends Entity {
	/** Mean delay between lightning strikes. */
	private static final int THUNDER_DELAY = 20;
	/** Minimum volume of thunder sounds. */
	private static final int THUNDER_MIN_VOLUME = 50;
	/** Maximum volume of thunder sounds. */
	private static final int THUNDER_MAX_VOLUME = 200;
	/** Available thunder sound effects. */
	private static final String[] THUNDER_SOUNDS = {
		"weather/thunder-01", "weather/thunder-02", "weather/thunder-03",
		"weather/thunder-04", "weather/thunder-05", "weather/thunder-06",
		"weather/thunder-07", "weather/thunder-08",
	};
	
	/** <code>true</code> if the thundering effect is active. */
	private boolean active;
	/** Turn listener for triggering lightnings. */
	private final TurnListener thunderer;
	
	/**
	 * Create a weather entity.
	 */
	public WeatherEntity() {
		setRPClass("entity");
		thunderer = new Thunderer();
	}
	
	/**
	 * Set the thunder state for the zone. An active thunder state creates
	 * lightnings at random intervals.
	 * 
	 * @param thunder <code>true</code> if the thundering is active, otherwise
	 * 	<code>false</code>
	 */
	public void setThunder(boolean thunder) {
		if (active != thunder) {
			if (thunder) {
				startThundering();
			} else {
				SingletonRepository.getTurnNotifier().dontNotify(thunderer);
			}
		}
		active = thunder;
	}
	
	/**
	 * Schedule the next lightning.
	 */
	private void startThundering() {
		int delay = Rand.randExponential(THUNDER_DELAY) + 1;
		SingletonRepository.getTurnNotifier().notifyInSeconds(delay, thunderer);
	}
	
	/** Turn listener for creating lightnings. */
	private class Thunderer implements TurnListener {
		@Override
		public void onTurnReached(int currentTurn) {
			if (active) {
				int volume = Rand.rand(THUNDER_MAX_VOLUME - THUNDER_MIN_VOLUME + 1)
						+ THUNDER_MIN_VOLUME;
				addEvent(new SoundEvent(Rand.rand(THUNDER_SOUNDS), volume, SoundLayer.AMBIENT_SOUND));
				notifyWorldAboutChanges();
				startThundering();
			}
		}
	}
}
