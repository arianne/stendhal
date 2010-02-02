/* $Id$ */
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
package games.stendhal.client.soundreview;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.log4j.Logger;

/**
 * This is an audio clip.
 * 
 * @author mtotz
 */
@Deprecated
public class AudioClip {

	/** the logger. */
	private static final Logger logger = Logger.getLogger(AudioClip.class);

	/** the data stream. */
	private final byte[] audioData;

	/** length of the clip. */
	private int length;

	/** volume for this clip. */
	private final int volume;

	/** the mixer. */
	private final Mixer mixer;

	/** need this for a nice toString(). */
	private final AudioFormat format;

	/** is the data supported. */
	private boolean supported;

	private Clip line;

	/**
	 * creates the audio clip.
	 * 
	 * @param mixer
	 *            the Mixer instance to be used
	 * @param audioData
	 *            the audio data
	 * @param volume
	 *            the loudness 0..100
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public AudioClip(final Mixer mixer, final byte[] audioData, final int volume)
			throws UnsupportedAudioFileException, IOException {
		this.volume = volume;
		this.mixer = mixer;

		AudioInputStream audioInputStream;
		audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(
				audioData));

		this.audioData = audioData;
		format = audioInputStream.getFormat();

		if (!mixer.isLineSupported(new DataLine.Info(Clip.class,
				audioInputStream.getFormat()))) {
			logger.warn("format is not supported ("
					+ audioInputStream.getFormat() + ")");
			supported = false;
			return;
		}

		supported = true;

		final float frameRate = audioInputStream.getFormat().getFrameRate();
		final long frames = audioInputStream.getFrameLength();

		if ((frameRate != AudioSystem.NOT_SPECIFIED)
				&& (frames != AudioSystem.NOT_SPECIFIED)) {
			length = (int) (frames / frameRate * 1000);
		} else {
			length = 0;
		}

	}

	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return Returns the volume.
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * opens the given line with the encapsulated audio data.
	 * @return the supported open line or null
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public Clip openLine() throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		if (supported) {
			final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(
					audioData));

			final DataLine.Info info = new DataLine.Info(Clip.class,
					audioInputStream.getFormat());
			if (!mixer.isLineSupported(info)) {
				return null;
			}
			line = (Clip) mixer.getLine(info);
			try {
				line.open(audioInputStream);
				return line;
			} catch (final LineUnavailableException e) {
				logger.debug("audioclip cannot be played, no free lines available");
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getClass().getName());
		stringBuilder.append(": ");
		if (!supported) {
			stringBuilder.append("(format not supported by ");
		
			stringBuilder.append(mixer.getMixerInfo().getDescription());
			stringBuilder.append(") ");
			stringBuilder.append(format);
		} 
		return stringBuilder.toString();
	}

	public void stop() {
		line.close();
	}

}
