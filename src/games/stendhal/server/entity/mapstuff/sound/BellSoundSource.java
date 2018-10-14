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
package games.stendhal.server.entity.mapstuff.sound;

import java.util.Calendar;

import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.events.SoundEvent;

/**
 * A sound event generating object for the churches. Plays bell sound every half
 * an hour.
 */
public class BellSoundSource extends PassiveEntity implements TurnListener {
	/** The played sound */
	private static final String SOUND_FILE = "bell-1";
	/** Radius of the sound */
	private static final int RADIUS = 70;
	/** Volume of the sound */
	private static final int VOLUME = 120;

	/** Time between bell playings in minutes. */
	private static final int TIME_SLICE = MathHelper.MINUTES_IN_ONE_HOUR / 2;

	/** Time between individual bell sounds when playing */
	private static final int TIME_BETWEEN_SOUNDS = 3;

	public BellSoundSource() {
		/*
		 * The client won't add anything but entities to the GameObjects.
		 */
		setRPClass("entity");
		put("type", "entity");
	}

	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		startTimer();
	}

	@Override
	public void onTurnReached(int currentTurn) {
		Calendar now = Calendar.getInstance();
		int minute = now.get(Calendar.MINUTE);
		// Check if the time is closer to even or half hour
		if (Math.abs(30 - minute) % 60 < 15) {
			// half hour, play just once
			new BellPlay(1);
		} else {
			// Full hour. Play up to 12 times
			int bongs = now.get(Calendar.HOUR);
			if (bongs == 0) {
				bongs = 12;
			}
			new BellPlay(bongs);
		}
	}

	/**
	 * Start the timer for the next playing.
	 */
	private void startTimer() {
		/*
		 * Time between playing is not very precise. It depends on the accuracy
		 * of the hourglass of the sacristan. (One minute precision + any turn
		 * time overflows).
		 */
		Calendar now = Calendar.getInstance();
		int timeToNext = TIME_SLICE - (now.get(Calendar.MINUTE) % TIME_SLICE);
		SingletonRepository.getTurnNotifier().notifyInSeconds(Math.max(1, timeToNext * MathHelper.SECONDS_IN_ONE_MINUTE), this);
	}

	/**
	 * A playing of bells.
	 */
	private class BellPlay implements TurnListener {
		/** Remaining number of hitting the bell */
		int times;

		/**
		 * Create a new BellPlay.
		 *
		 * @param times number of times the bell should be played
		 */
		public BellPlay(int times) {
			this.times = times;
			// Start playing immediately
			onTurnReached(0);
		}

		@Override
		public void onTurnReached(int currentTurn) {
			playSound();
			if (times > 0) {
				startTimer();
			} else {
				// Job finished. Return to the main notifier
				BellSoundSource.this.startTimer();
			}
		}

		/**
		 * Create a sound event for one hit of the bell.
		 */
		private void playSound() {
			times--;
			SoundEvent event = new SoundEvent(SOUND_FILE, RADIUS, VOLUME, SoundLayer.AMBIENT_SOUND);
			BellSoundSource.this.addEvent(event);
			BellSoundSource.this.notifyWorldAboutChanges();
		}

		/**
		 * Start the internal timer.
		 */
		private void startTimer() {
			SingletonRepository.getTurnNotifier().notifyInSeconds(TIME_BETWEEN_SOUNDS, this);
		}
	}
}
