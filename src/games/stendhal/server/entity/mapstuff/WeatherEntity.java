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
import games.stendhal.common.constants.Events;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.events.GlobalVisualEffectEvent;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;

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
		setRPClass("weather_entity");
		thunderer = new Thunderer();
		setResistance(0);
	}

	public static void generateRPClass() {
		RPClass clazz = new RPClass("weather_entity");
		clazz.isA("entity");
		clazz.addRPEvent(Events.GLOBAL_VISUAL, Definition.VOLATILE);
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
		// This is called when the lightning strikes, triggering the potential
		// flash. The sound comes a bit later, depending on the distance of the
		// strike.
		@Override
		public void onTurnReached(int currentTurn) {
			if (active) {
				double distance = Rand.rand();
				// Most lightnings happen inside clouds, or otherwise do not
				// create a visible flash.
				if (Rand.rand(3) == 0) {
					int duration = Rand.rand(100) + 50;
					int brightness = (int) (50 * distance);
					addEvent(new GlobalVisualEffectEvent("lightning", duration, brightness));
					notifyWorldAboutChanges();
				}
				int volume = (int) (distance * (THUNDER_MAX_VOLUME - THUNDER_MIN_VOLUME + 1))
						+ THUNDER_MIN_VOLUME;
				// Max 10s delay
				SingletonRepository.getTurnNotifier().notifyInTurns((int) (distance * 30) + 1, new SoundTurnListener(volume));
				startThundering();
			}
		}

		private class SoundTurnListener implements TurnListener {
			private final int volume;

			public SoundTurnListener(int volume) {
				this.volume = volume;
			}

			@Override
			public void onTurnReached(int currentTurn) {
				// Do *not* check activity status - the last sound may arrive
				// after the thunder is over, and every flash should get a sound
				addEvent(new SoundEvent(Rand.rand(THUNDER_SOUNDS), volume, SoundLayer.AMBIENT_SOUND));
				notifyWorldAboutChanges();
			}
		}
	}
}
