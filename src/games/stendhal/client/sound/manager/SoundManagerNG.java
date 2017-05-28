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
package games.stendhal.client.sound.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import games.stendhal.client.sound.facade.AudibleArea;
import games.stendhal.client.sound.facade.InfiniteAudibleArea;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundHandle;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.manager.DeviceEvaluator.Device;
import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.SoundSystemNG;
import games.stendhal.client.sound.system.processors.DirectedSound;
import games.stendhal.client.sound.system.processors.Interruptor;
import games.stendhal.client.sound.system.processors.VolumeAdjustor;
import games.stendhal.common.math.Algebra;

/**
 * New implementation of the sound manager.
 *
 * @author silvio
 */
public class SoundManagerNG {
	private final static Logger logger = Logger.getLogger(SoundManagerNG.class);
	private final static int SOUND_CHANNEL_LIMIT = 50;
	private final static int DIMENSION = 2;
	private final static float[] HEARER_LOOKONG_DIRECTION = { 0.0f, 1.0f };
	private final static InfiniteAudibleArea INFINITE_AUDIBLE_AREA = new InfiniteAudibleArea();
	private final static Time BUFFER_DURATION = new Time(15, Time.Unit.MILLI);
	private final static Time ZERO_DURATION = new Time();

	public final static class Sound implements SoundHandle, Cloneable {
		final AtomicReference<SoundFile> file = new AtomicReference<SoundFile>(
				null);
		final AtomicReference<SoundChannel> channel = new AtomicReference<SoundChannel>(
				null);
		Object object = null;

		@Override
		public Sound clone() {
			Sound sound = new Sound();
			sound.file.set(file.get().clone());
			sound.object = object;
			return sound;
		}

		@SuppressWarnings("unchecked")
		public <T> T getAttachment(Class<T> clazz) {
			if (clazz.isInstance(object)) {
				return (T) object;
			}

			return null;
		}

		public Object getAttachment() {
			return object;
		}

		public boolean isActive() {
			return channel.get() != null && channel.get().isActive();
		}

		public void setAttachment(Object obj) {
			object = obj;
		}
	}

	private final class SoundChannel extends SignalProcessor {
		final float[] mSoundPosition = new float[DIMENSION];
		final AtomicBoolean mAutoRepeat = new AtomicBoolean(false);
		final AtomicBoolean mIsActive = new AtomicBoolean(false);
		final AtomicReference<AudibleArea> mAudibleArea = new AtomicReference<AudibleArea>(
				INFINITE_AUDIBLE_AREA);
		final Interruptor mInterruptor = new Interruptor();
		final DirectedSound mDirectedSound = new DirectedSound();
		final VolumeAdjustor mGlobalVolume = new VolumeAdjustor();
		final SoundSystemNG.Output mOutput;
		Sound mSound = null;

		SoundChannel() {
			mOutput = mSoundSystem.openOutput(0, mInterruptor, this,
					mGlobalVolume, mDirectedSound);
		}

		boolean isActive() {
			return mIsActive.get();
		}

		void setAutoRepeat(boolean repeat) {
			mAutoRepeat.set(repeat);
		}

		void setVolume(float volume) {
			mGlobalVolume.setVolume(volume);
		}

		void startFading(float volume, Time duration) {
			mGlobalVolume.startFading(volume, duration);
		}

		void startFading(Time duration) {
			mGlobalVolume.startFading(duration);
		}

		void setLayer(int layer) { /* mOutput.setLayer(layer); */
		}

		void resumePlayback() {
			mInterruptor.play();
		}

		void close() {
			mSoundSystem.closeOutput(mOutput);
		}

		Sound getSoundObject() {
			return mSound;
		}

		void setAudibleArea(AudibleArea area) {
			if (area == null) {
				area = INFINITE_AUDIBLE_AREA;
			}

			mAudibleArea.set(area);
		}

		synchronized void playSound(Sound newSound, float volume, Time time) {
			if (mSound != null) {
				mSound.file.get().disconnect();
				mSound.file.get().restart();
				mSound.channel.set(null);
				mOutput.setIntensity(0.0f);
			}

			if (newSound != null) {
				if (time == null) {
					time = ZERO_DURATION;
				}

				mInterruptor.play();
				mGlobalVolume.setVolume(0.0f);
				mGlobalVolume.startFading(volume, time);
				newSound.channel.set(this);
				newSound.file.get().connectTo(mInterruptor, true);
			}

			mSound = newSound;
			mIsActive.set(newSound != null);
		}

		void stopPlayback(Time time) {
			if (time == null) {
				time = ZERO_DURATION;
			}

			mAutoRepeat.set(false);
			mGlobalVolume.startFading(0.0f, time);
			mInterruptor.stop(time);
		}

		void update() {
			float intensity = mAudibleArea.get().getHearingIntensity(
					mHearerPosition);
			mAudibleArea.get().getClosestPoint(mSoundPosition, mHearerPosition);
			mDirectedSound.setPositions2D(mSoundPosition, mHearerPosition,
					HEARER_LOOKONG_DIRECTION, intensity);
			mOutput.setIntensity(intensity);
		}

		@Override
		protected void finished() {
			if (mAutoRepeat.get()) {
				try {
					mSound.file.get().restart();
				} catch (NullPointerException e) {
					logger.error(e, e);
					logger.error("mSound: " + mSound);
					logger.error("mSound.file: " + mSound.file);
					logger.error("mSound.file.get: " + mSound.file.get());
				}
			} else {
				playSound(null, 0, null);
				super.quit();
			}
		}
	}

	private final Collection<Device> mDevices;
	private final AudioFormat mAudioFormat;
	private final LinkedList<SoundChannel> mChannels = new LinkedList<SoundChannel>();
	private final float[] mHearerPosition = new float[DIMENSION];
	private boolean mMute = false;
	private SoundSystemNG mSoundSystem;

	protected SoundManagerNG(boolean mute, Collection<Device> devices,
			AudioFormat audioFormat) {
		Algebra.mov_Vecf(mHearerPosition, 0.0f);
		mDevices = devices;
		mAudioFormat = audioFormat;

		logger.info("initializing sound system");

		if (devices != null) {
			for (Device device : devices) {
				logger.info("available device: " + device.getName() + " - "
						+ device.getDescription() + " (" + device.getRating()
						+ ")");
			}
		}

		if (mute || devices == null || devices.size() == 0) {
			mSoundSystem = new SoundSystemNG(mAudioFormat, BUFFER_DURATION);
			mMute = true;
		} else {
			mSoundSystem = new SoundSystemNG(getOutputLine(), BUFFER_DURATION);
			mMute = false;
		}

		// start the sound system thread in a separate method to take derived
		// classes into account
		// (not yet needed, but just in case - and to avoid the FindBugs
		// warning)
		startSoundsystem();
	}

	protected final void startSoundsystem() {
		mSoundSystem.setDaemon(true);
		mSoundSystem.start();
	}

	public synchronized Sound openSound(AudioResource AudioResource,
			SoundFileType fileType, int numSamplesPerChunk,
			boolean enableStreaming) {
		Sound sound = null;

		try {
			SoundFile file = new SoundFile(AudioResource, fileType,
					numSamplesPerChunk, enableStreaming);
			sound = new Sound();
			sound.file.set(file);
		} catch (IOException exception) {
			logger.warn(exception);
			return null;
		}

		return sound;
	}

	public synchronized void setHearerPosition(float[] position) {
		Algebra.mov_Vecf(mHearerPosition, position);
	}

	public synchronized void update() {
		for (SoundChannel channel : mChannels) {
			if (channel.isActive()) {
				channel.update();
			}
		}
	}

	public synchronized void play(Sound sound, float volume, int layerLevel,
			AudibleArea area, boolean autoRepeat, Time fadeInDuration) {
		if (sound == null) {
			return;
		}

		if (sound.isActive()) {
			SoundChannel channel = sound.channel.get();
			channel.setAutoRepeat(autoRepeat);
			channel.startFading(1.0f, fadeInDuration);
			channel.setVolume(volume);
			channel.setLayer(layerLevel);
			channel.setAudibleArea(area);
			channel.resumePlayback();
			channel.update();
		} else {
			if (!mMute && mSoundSystem.isRunning()) {
				SoundChannel channel = getInactiveChannel();
				channel.setAutoRepeat(autoRepeat);
				channel.setLayer(layerLevel);
				channel.setAudibleArea(area);
				channel.playSound(sound, volume, fadeInDuration);
				channel.update();

				closeInactiveChannels(SOUND_CHANNEL_LIMIT);
			}
		}
	}

	public synchronized void stop(Sound sound, Time fadeOutDuration) {
		if (sound != null && sound.isActive()) {
			sound.channel.get().stopPlayback(fadeOutDuration);
		}
	}

	public synchronized void changeVolume(Sound sound, float volume) {
		if (sound != null && sound.isActive()) {
			sound.channel.get().setVolume(volume);
		}
	}

	public synchronized void changeLayer(Sound sound, int layerLevel) {
		if (sound != null && sound.isActive()) {
			sound.channel.get().setLayer(layerLevel);
		}
	}

	public synchronized void changeAudibleArea(Sound sound, AudibleArea area) {
		if (sound != null && sound.isActive()) {
			sound.channel.get().setAudibleArea(area);
		}
	}

	public synchronized void mute(boolean turnOffSound, boolean useFading,
			Time delay) {
		if (turnOffSound && !mMute) {
			logger.info("turning off audio");
			mSoundSystem.suspend(delay, true);

			if (useFading) {
				for (SoundChannel channel : mChannels) {
					if (channel.isActive()) {
						channel.startFading(0, delay);
					}
				}
			}
		}

		if (!turnOffSound && mMute) {
			logger.info("turning on audio");
			mSoundSystem.proceed(null, getOutputLine());

			if (useFading) {
				for (SoundChannel channel : mChannels) {
					if (channel.isActive()) {
						channel.startFading(delay);
					}
				}
			}
		}

		mMute = turnOffSound;
	}

	public synchronized Collection<Sound> getActiveSounds() {
		ArrayList<Sound> sounds = new ArrayList<Sound>(mChannels.size());

		for (SoundChannel channel : mChannels) {
			Sound sound = channel.getSoundObject();

			if (sound != null && sound.isActive()) {
				sounds.add(sound);
			}
		}

		sounds.trimToSize();
		return sounds;
	}

	public synchronized void exit() {
		mSoundSystem.exit(null);

		try {
			mSoundSystem.join();
		} catch (InterruptedException exception) {
			logger.warn("joining sound system thread was interrupted: "
					+ exception);
		}
	}

	private void closeInactiveChannels(int leaveNumChannelsOpen) {
		int numChannels = mChannels.size();
		Iterator<SoundChannel> iChannel = mChannels.iterator();

		while (iChannel.hasNext()) {
			if (mChannels.size() <= leaveNumChannelsOpen) {
				break;
			}

			SoundChannel currChannel = iChannel.next();

			if (!currChannel.isActive()) {
				currChannel.close();
				iChannel.remove();
			}
		}

		numChannels -= mChannels.size();

		if (numChannels > 0) {
			logger.debug("close " + numChannels + " inactive sound channels");
		}
	}

	private SoundChannel getInactiveChannel() {
		SoundChannel foundChannel = null;

		for (SoundChannel channel : mChannels) {
			if (!channel.isActive()) {
				foundChannel = channel;
				break;
			}
		}

		if (foundChannel == null) {
			logger.debug("open new sound channel (number " + mChannels.size()
					+ ")");

			foundChannel = new SoundChannel();
			mChannels.add(foundChannel);
		}

		return foundChannel;
	}

	private SourceDataLine getOutputLine() {
		for (Device device : mDevices) {
			SourceDataLine line = device.getLine(SourceDataLine.class,
					mAudioFormat);

			if (line != null) {
				return line;
			}
		}

		return null;
	}

	/**
	 * gets the list of devices
	 *
	 * @return devices
	 */
	public Collection<Device> getDevices() {
		return new LinkedList<Device>(mDevices);
	}
}
