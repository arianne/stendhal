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
package games.stendhal.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Checks if all sound files can be played on the current system. For example
 * some sounds can only be played on MS Windows but not on Linux based systems.
 * 
 * @author mtotz
 */
public class CheckSounds {

	private static final boolean TESTPLAY_SAMPLES = false;

	private static class TestLineListener implements LineListener {

		public boolean active = true;

		@Override
		public void update(final LineEvent event) {
			if (event.getType() == LineEvent.Type.STOP) {
				active = false;
			}
		}
	}

	private static String getString(String s, final int width, final char c) {
		StringBuilder sb = new StringBuilder(s);

		while (s.length() < width) {
			sb.append(c);
		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static void main(final String[] args) throws Exception {
		final Properties prop = new Properties();
		loadSoundProperties(prop);

		final Map<String, AudioFormat> formatMap = new TreeMap<String, AudioFormat>();
		final Map<String, String> fileFormatMap = new TreeMap<String, String>();
		final Mixer defaultMixer = TESTPLAY_SAMPLES? AudioSystem.getMixer(null): null;

		// get sound library filepath
		final String soundBase = prop.getProperty("soundbase", "data/sounds");

		// read all load-permitted sounds listed in properties
		// from soundfile into cache map
		for (final Entry<String, String> entry : ((Map<String, String>) (Map<?,?>) prop).entrySet()) {
			if (isValidEntry(entry.getKey(), entry.getValue())) {
				final String name = entry.getKey().substring(4);
				String filename = entry.getValue();
				final int pos = filename.indexOf(',');
				if (pos > -1) {
					filename = filename.substring(0, pos);
				}

				try {
					final InputStream is = CheckSounds.class.getClassLoader().getResourceAsStream(
							soundBase + "/" + filename);
					final AudioInputStream ais = AudioSystem.getAudioInputStream(is);
					final AudioFormat format = ais.getFormat();
					final String formatString = format.toString();

					if (TESTPLAY_SAMPLES) {
						// testplay the sound
						final DataLine.Info info = new DataLine.Info(Clip.class, format);
						if (defaultMixer.isLineSupported(info)) {
							AudioInputStream playStream = ais;
							final AudioFormat defaultFormat = new AudioFormat(
									format.getSampleRate(), 16, 1, false, true);
							if (AudioSystem.isConversionSupported(
									defaultFormat, format)) {
								playStream = AudioSystem.getAudioInputStream(
										defaultFormat, ais);
							} else {
								System.out.println("conversion not supported (to "
										+ defaultFormat + ")");
							}

							System.out.println("testplaying " + name + " "
									+ playStream.getFormat());

							final Clip line = (Clip) defaultMixer.getLine(info);
							line.open(playStream);
							line.loop(2);
							final TestLineListener testListener = new TestLineListener();
							line.addLineListener(testListener);
							while (testListener.active) {
								Thread.yield();
							}
							line.close();
						}
					}

					fileFormatMap.put(name, formatString);
					if (!formatMap.containsKey(formatString)) {
						formatMap.put(formatString, format);
					}
				} catch (final UnsupportedAudioFileException e) {
					System.out.println(name
							+ " cannot be read, the file format is not supported");
				}
			}
		}

		final Mixer.Info[] mixerList = AudioSystem.getMixerInfo();
		final int[] width = new int[mixerList.length];

		System.out.println("\n\n--- Result ---\n");
		System.out.println("installed mixer: ");
		for (int i = 0; i < mixerList.length; i++) {
			final Mixer.Info mixer = mixerList[i];
			width[i] = Math.max(mixer.getName().length(),
					"unsupported".length());
			System.out.println(mixer.getName() + " - " + mixer.getDescription());
		}
		System.out.println("Default: "
				+ AudioSystem.getMixer(null).getMixerInfo().getName());
		System.out.println("\n");

		System.out.println(formatMap.size()
				+ " audio formats\nThe maximum available lines for the format is in brackets.");
		for (int i = 0; i < mixerList.length; i++) {
			System.out.print(getString(mixerList[i].getName(), width[i], ' ')
					+ " | ");
		}
		System.out.println("Format");
		for (int i = 0; i < mixerList.length; i++) {
			System.out.print(getString("", width[i], '-') + "-+-");
		}
		System.out.println("---------------------");

		for (final Map.Entry<String, AudioFormat> it_FM : formatMap.entrySet()) {
			String key = it_FM.getKey();
			final DataLine.Info info = new DataLine.Info(Clip.class, it_FM.getValue());
			for (int i = 0; i < mixerList.length; i++) {
				final Mixer mixer = AudioSystem.getMixer(mixerList[i]);
				final boolean supported = mixer.isLineSupported(info);
				StringBuilder stringBuilder = new StringBuilder();
				if (supported) {
					stringBuilder.append("  ");
				} else {
					stringBuilder.append("un");
				}
				stringBuilder.append("supported (");
				stringBuilder.append(mixer.getMaxLines(info));
				stringBuilder.append(")");
				System.out.print(getString(stringBuilder.toString(),
						width[i], ' ') + " | ");
			}

			System.out.print(key);
			// line not supported by any mixer
			StringBuilder files = new StringBuilder();
			for (final Map.Entry<String, String> itFFM : fileFormatMap.entrySet()) {
				if (key.equals(itFFM.getValue())) {
					files.append(" ").append(itFFM.getValue());
				}
			}
			System.out.print(" (files: " + files.toString() + ")");

			System.out.println();
		}
		System.out.println("done");
	}


	/** expected location of the sound definition file (classloader). */
	private static final String STORE_PROPERTYFILE = "data/sounds/stensounds.properties";

	/**
	 * @param prop
	 *            the Property Object to load to
	 * @throws IOException
	 */
	private static void loadSoundProperties(final Properties prop) throws IOException {
		InputStream in1;

		in1 = CheckSounds.class.getClassLoader().getResourceAsStream(
				STORE_PROPERTYFILE);
		prop.load(in1);
		in1.close();
	}

	/**
	 * A key/value pair is assumed valid if
	 * <ul>
	 * <li>key starts with "sfx." <b>and </b></li>
	 * <li>key does not end with ",x"</li>
	 * <li>or value contains a "."</li>
	 * </ul>.
	 * 
	 * @param key
	 * @param value
	 * @return true, if it is valid, false otherwise
	 */
	private static boolean isValidEntry(final String key, final String value) {
		boolean load;
		int pos1;
		if (key.startsWith("sfx.")) {
			pos1 = value.indexOf(',');
			if (pos1 > -1) {
				load = value.substring(pos1 + 1).charAt(0) != 'x';
			} else {
				load = true;
			}
			load |= value.indexOf('.') != -1;
			return load;
		} else {
			return false;
		}
	}
	// ------------------------------------------------------------------------
	// copied code end
	// ------------------------------------------------------------------------
}
