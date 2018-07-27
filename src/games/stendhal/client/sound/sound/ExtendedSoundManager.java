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
package games.stendhal.client.sound.sound;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;

import org.apache.log4j.Logger;

import games.stendhal.client.MemoryCache;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.facade.AudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.manager.AudioResource;
import games.stendhal.client.sound.manager.DeviceEvaluator;
import games.stendhal.client.sound.manager.SoundManagerNG;
import games.stendhal.common.math.Numeric;

/**
 * this class is the main interface between the game logic and the low level
 * sound system. It is a refinement of the manager.SoundManager class.
 *
 * @author hendrik, silvio
 */
public class ExtendedSoundManager extends SoundManagerNG {
	private static Logger logger = Logger.getLogger(ExtendedSoundManager.class);

	private static class Multiplicator {

		public Multiplicator(float v, Group g) {
			value = v;
			group = g;
		}

		float value;
		Group group;
	}

	public class Group implements SoundGroup {

		private final boolean mEnabled = true;
		private float mVolume = 1.0f;
		private final MemoryCache<String, Sound> mGroupSounds = new MemoryCache<String, Sound>();
		private boolean streaming = false;

		@Override
		public boolean loadSound(String name, String filename, SoundFileType fileType, boolean enableStreaming) {
			try {
				Sound sound = ExtendedSoundManager.this.mSounds.get(name);

				if (sound == null) {
					sound = openSound(new AudioResource(filename), fileType, 256, enableStreaming);

					if (sound != null) {
						ExtendedSoundManager.this.mSounds.put(name, sound);
					}
				}

				if (sound != null) {
					mGroupSounds.put(name, sound);
				}

				return sound != null;
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
			return false;
		}

		@Override
		public float getVolume() {
			return mVolume;
		}

		@Override
		public void changeVolume(float volume) {
			mVolume = volume;

			for (Sound sound : getActiveSounds()) {
				Multiplicator multiplicator = sound.getAttachment(Multiplicator.class);

				if (multiplicator != null && multiplicator.group == this) {
					ExtendedSoundManager.this.changeVolume(sound, (mMasterVolume * mVolume * multiplicator.value));
				}
			}
		}

		/**
		 * enables streaming of the music data for this group.
		 */
		@Override
		public void enableStreaming() {
			streaming = true;
		}

		@Override
		public Sound play(String soundName, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
			return play(soundName, 1.0f, layerLevel, area, fadeInDuration, autoRepeat, clone);
		}

		@Override
		public Sound play(String soundName, float volume, int layerLevel, AudibleArea area, Time fadeInDuration, boolean autoRepeat, boolean clone) {
			if (soundName == null) {
				return null;
			}
			try {

				if (mEnabled) {
					Sound sound = mGroupSounds.get(soundName);
					if (sound == null) {
						loadSound(soundName, soundName + ".ogg", SoundFileType.OGG, this.streaming);
						sound = mGroupSounds.get(soundName);
					}

					if (sound != null) {
						if (clone) {
							sound = sound.clone();
						}

						sound.setAttachment(new Multiplicator(volume, this));
						ExtendedSoundManager.this.play(sound, (mMasterVolume * mVolume * volume), layerLevel, area, autoRepeat, fadeInDuration);
					}

					return sound;
				}
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
			return null;
		}
	}

	private static final AudioFormat mAudioFormat;
	private static final DeviceEvaluator mDeviceEvaluator;

	static
	{
		WtWindowManager wm = WtWindowManager.getInstance();
		mDeviceEvaluator = new DeviceEvaluator();
		String userSelected = wm.getProperty("sound.device", "auto");
		if (!userSelected.equals("auto - recommended")) {
			logger.info("User selected sound device: " + userSelected);
			mDeviceEvaluator.setRating(Pattern.compile(Pattern.quote(userSelected)), null, 100);
		}
		mDeviceEvaluator.setRating(Pattern.compile(".*pulseaudio.*", Pattern.CASE_INSENSITIVE), null, 2);
		mDeviceEvaluator.setRating(Pattern.compile(".*plughw.0.0.*"), null, 1);
		mDeviceEvaluator.setRating(Pattern.compile(".*Java Sound Audio Engine.*"), null, -1);

		mAudioFormat = new AudioFormat(44100, 16, 2, true, false);
	}

	private final MemoryCache<String, Sound> mSounds = new MemoryCache<String, Sound>();
	private final Map<String, Group> mGroups = new LinkedHashMap<String, Group>();
	private float mMasterVolume = 1.0f;

	ExtendedSoundManager() {
		super(!WtWindowManager.getInstance().getPropertyBoolean("sound.play", true),
				mDeviceEvaluator.createDeviceList(mAudioFormat), mAudioFormat);
		initVolumes();
	}

	private void initVolumes() {
		WtWindowManager config = WtWindowManager.getInstance();

		int volume = config.getPropertyInt("sound.volume.master", 100);
		changeVolume(Numeric.intToFloat(volume, 100.0f));

		this.getGroup("gui").changeVolume(Numeric.intToFloat(config.getPropertyInt("sound.volume.gui", 100), 100.0f));
		this.getGroup("sfx").changeVolume(Numeric.intToFloat(config.getPropertyInt("sound.volume.sfx", 100), 100.0f));
		this.getGroup("creature").changeVolume(Numeric.intToFloat(config.getPropertyInt("sound.volume.creature", 95), 100.0f));
		this.getGroup("ambient").changeVolume(Numeric.intToFloat(config.getPropertyInt("sound.volume.ambient", 80), 100.0f));
		this.getGroup("music").changeVolume(Numeric.intToFloat(config.getPropertyInt("sound.volume.music", 60), 100.0f));
	}

	// The initMute() method is not needed anymore, but preserved
	// if something goes wrong with the muting again.
	/*
	private void initMute() {
		WtWindowManager config = WtWindowManager.getInstance();
		boolean play = Boolean.parseBoolean(config.getProperty("sound.play", "true"));
		mute(!play, false, new Time(0, Time.Unit.SEC));
	}*/

	public Group getGroup(String groupName) {
		Group group = mGroups.get(groupName);

		if (group == null) {
			group = new Group();
			mGroups.put(groupName, group);
		}

		return group;
	}

	public Collection<String> getGroupNames() {
		return mGroups.keySet();
	}

	public float getVolume() {
		return mMasterVolume;
	}

	public void changeVolume(float volume) {
		mMasterVolume = volume;

		for (Sound sound : getActiveSounds()) {
			Multiplicator multiplicator = sound.getAttachment(Multiplicator.class);

			if (multiplicator != null) {
				super.changeVolume(sound, (mMasterVolume * multiplicator.group.mVolume * multiplicator.value));
			}
		}
	}

}
