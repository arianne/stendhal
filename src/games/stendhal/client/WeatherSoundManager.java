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
package games.stendhal.client;

import java.util.Objects;

import games.stendhal.client.StendhalClient.ZoneChangeListener;
import games.stendhal.client.sound.facade.AudibleArea;
import games.stendhal.client.sound.facade.InfiniteAudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.common.constants.SoundLayer;

/**
 * Manages starting and stopping weather sounds. Needs to listen to zone changes
 * to keep up with the weather.
 */
public class WeatherSoundManager implements ZoneChangeListener {
	/** Sound location relative to the default of all other sounds. */
	private static final String SOUND_LOCATION = "weather/";

	/** Currently playing sound name, or <code>null</code>. */
	private String soundName;
	/** Handle to the current sound, or <code>null</code>. */
	private SoundHandle currentSound;
	/** Duration used to fade the sounds out. */
	private final Time fadeOutDuration = new Time(2, Time.Unit.SEC);
	/** Duration used to fade the sounds in. */
	private final Time fadeInDuration = new Time();

	@Override
	public void onZoneChange(Zone zone) {
		// No real change yet. Ignore.
	}

	@Override
	public void onZoneUpdate(Zone zone) {
		updateWeatherSound(zone.getWeatherName());
	}

	@Override
	public void onZoneChangeCompleted(Zone zone) {
		updateWeatherSound(zone.getWeatherName());
	}

	/**
	 * Update the played weather sound.
	 *
	 * @param sound weather type name
	 */
	private void updateWeatherSound(String sound) {
		if (!Objects.equals(sound,  soundName)) {
			if (currentSound != null) {
				// Stop old weather sound
				ClientSingletonRepository.getSound().stop(currentSound, fadeOutDuration);
				currentSound = null;
			}
			soundName = sound;
			if (sound != null) {
				final String soundPath = SOUND_LOCATION + sound + ".ogg";
				if (this.getClass().getResource("/data/sounds/" + soundPath) != null) {
					// Start a new one
					SoundGroup group = ClientSingletonRepository.getSound().getGroup(SoundLayer.AMBIENT_SOUND.groupName);
					// Should we cache the failed loads so that the sound manager
					// would not print a warning every time?
					if (group.loadSound(sound, soundPath, SoundFileType.OGG, true)) {
						AudibleArea area = new InfiniteAudibleArea();
						currentSound = group.play(sound, 0, area, fadeInDuration, true, false);
					}
				}
			}
		}
	}
}
