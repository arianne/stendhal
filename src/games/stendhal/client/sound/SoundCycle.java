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

package games.stendhal.client.sound;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.SoundObject;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

import javax.sound.sampled.DataLine;

import marauroa.common.game.RPObject.ID;

import org.apache.log4j.Logger;

/**
 * A sound cycle loops on performing a library sound. After each termination of
 * a sound performance it chooses a timepoint of the next performance at random
 * within the range of the PEROID setting (milliseconds). It can be furthermore
 * subject to probability, and a range for playing volume can be defined.
 * <p>
 * A sound cycle can be GLOBAL or OBJECT-BOUND, depending on whether a game
 * entity has been supplied during creation. Global sounds always play
 * independent from player position.
 */
@Deprecated
class SoundCycle extends Thread implements Cloneable {

	
	private static final Logger logger = Logger.getLogger(SoundCycle.class);

	private byte[] ID_Token;

	Entity entityRef;

	private final String token;

	private final int period;

	private final int volBot;

	private final int volTop;

	private final int chance;

	private DataLine dataline;

	private long waitTime;

	private final int playMax;

	private boolean executing;

	private boolean stopped;

	/**
	 * Creates a sound cycle for a game entity. Depending on whether <code>
	 * entity</code>
	 * is void, this cycle is global or object-bound.
	 * 
	 * @param entity
	 *            the game entity to which this cycle is bound; if <b>null</b>
	 *            then a global cycle is created
	 * @param token
	 *            library sound token
	 * @param period
	 *            milliseconds of maximum delay time between singular sound
	 *            performances
	 * @param volBot
	 *            relative bottom volume in percent, ranges 0..100
	 * @param volTop
	 *            relative top volume in percent, ranges 0..100
	 * @param chance
	 *            percent chance of performance for singular performances
	 */
	public SoundCycle(final Entity entity, final String token, final int period, final int volBot,
			final int volTop, final int chance) {
		super("Stendhal.CycleSound." + token);

		ClipRunner clip;

		if (period < 1000) {
			throw new IllegalArgumentException("illegal sound period");
		}
		if ((volBot < 0) || (volBot > 100) || (volTop < 0) || (volTop > 100)
				|| (volTop < volBot)) {
			throw new IllegalArgumentException("bad volume setting");
		}
		clip = SoundEffectMap.getInstance().getSoundClip(token);
		if (clip == null) {
			logger.debug("sound effect not found for token: " + token);
		}

		if (entity != null) {
			this.ID_Token = entity.ID_Token;
			this.entityRef = entity;
		}
		this.token = token;
		this.period = period;
		this.volBot = volBot;
		this.volTop = volTop;
		this.chance = chance;

		// calculate period minimum
		playMax = (int) clip.maxPlayLength();

		stopped = true;
	} // constructor

	/**
	 * Terminates the soundcycle.
	 */
	public void terminate() {
		IEntity o;
		ID oid;
		String hstr;

		o = null;
		if (entityRef != null) {
			o = entityRef;
		}

		if (o != null) {
			oid = o.getID();
			if (oid == null) {
				hstr = "VOID";
			} else {
				hstr = oid.toString();
			}
		} else {
			hstr = "VOID";
		}

		hstr = "  ** terminating cycle sound: " + token + " / entity=" + hstr;
		logger.debug(hstr);

		if (dataline != null) {
			dataline.stop();
			dataline = null;
		}
		executing = false;
	} // terminate

	/**
	 * Temporarily ceases to perform sound playing. (May be resumed through
	 * method <code>play()</code>.)
	 */
	public void stopPlaying() {
		stopped = true;
	}

	/**
	 * Starts or resumes playing this sound cycle.
	 */
	public void play() {
		String hstr;

		stopped = false;
		if (!isAlive()) {
			hstr = "  ** starting cycle sound: " + token + " / entity=?";
			logger.debug(hstr);

			executing = true;
			try {
				start();
			} catch (final OutOfMemoryError e) {
				// If the number of threads is limmited a OutOfMemoryError is
				// thrown.
				// The soundsystem can create a huge amount of threads, so we
				// catch
				// it and simply ignore it.
				logger.debug(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		IEntity o;

		while (executing) {
			waitTime = Math.max(playMax, Rand.rand(period));
			try {
				sleep(waitTime);
			} catch (final InterruptedException e) {
				// ignore
			}

			if (!executing) {
				return;
			}

			if (stopped) {
				continue;
			}

			// if object bound sound cycle
			if (entityRef != null) {
				o = entityRef;
				if (o != null) {
					logger.debug("- start cyclic sound for entity: "
							+ o.getType());
					dataline = ((SoundObject) o).playSound(token, volBot,
							volTop, chance);
				} else {
					// FIXME: o is never null could be origin for sound dont stop bug
					SoundSystem.stopSoundCycle(ID_Token);
					terminate();
				}
			} else {
				SoundMaster.play(SoundLayer.AMBIENT_SOUND, token);
			}
		}
	} // run

	/**
	 * Returns a full copy of this SoundCycle, which is not running.
	 */
	@Override
	public SoundCycle clone() {
		Entity entity;
		SoundCycle c;

		entity = null;
		if (entityRef != null) {
			entity = entityRef;
		}

		c = new SoundCycle(entity, token, period, volBot, volTop, chance);
		return c;
	}
}
