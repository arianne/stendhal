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
package games.stendhal.client.entity;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.sound.facade.AudibleCircleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;
import marauroa.common.game.RPObject;

/**
 * LoopedSoundSource is the source of a repeated sound, the player can
 * hear in a limited area.
 */
public class LoopedSoundSource extends InvisibleEntity {

	private String soundName = null;
	private SoundHandle sound = null;
	private SoundGroup group = null;
	private final Time fadingDuration = new Time();
	private int radius;
	private float volume;

	@Override
	public void onChangedAdded(RPObject object, RPObject changes) {
		// stop the current sound
		ClientSingletonRepository.getSound().stop(sound, fadingDuration);

		// udpate
		super.onChangedAdded(object, changes);

		update(changes);
		play();
	}

	/**
	 * updates the attributes based on the RPObject values sent from the server.
	 *
	 * @param object
	 *            object to read from
	 */
	private void update(final RPObject object) {

		boolean streaming = false;

		if (object.has("radius")) {
			radius = object.getInt("radius");
		}
		if (object.has("volume")) {
			volume = Numeric.intToFloat(object.getInt("volume"), 100.0f);
		}
		if (object.has("layer")) {
			final int idx = object.getInt("layer");
			SoundLayer layer;

			if (idx < SoundLayer.values().length) {
				layer = SoundLayer.values()[idx];
				group = ClientSingletonRepository.getSound().getGroup(layer.groupName);
			} else {
				layer = null;
				group = null;
			}

			fadingDuration.set(100, Time.Unit.MILLI);

			if (layer == SoundLayer.BACKGROUND_MUSIC) {
				streaming = true;
				fadingDuration.set(3, Time.Unit.SEC);
				group.enableStreaming();
			}
		}
		if (object.has("sound")) {
			soundName = object.get("sound");
			group.loadSound(soundName, soundName + ".ogg", SoundFileType.OGG, streaming);
		}
	}

	/**
	 * Plays the sound.
	 */
	private void play() {
		AudibleCircleArea area = new AudibleCircleArea(Algebra.vecf((float) x, (float) y), radius / 2.0f, radius);
		sound = group.play(soundName, volume, 0, area, fadingDuration, true, true);
	}

	/**
	 * Release this entity. This should clean anything that isn't automatically
	 * released (such as unregister callbacks, cancel external operations, etc).
	 *
	 * @see #initialize(RPObject)
	 */
	@Override
	public void release() {
		super.release();
		ClientSingletonRepository.getSound().stop(sound, fadingDuration);
	}
}
