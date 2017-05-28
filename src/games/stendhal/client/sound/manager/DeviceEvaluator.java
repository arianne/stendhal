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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author silvio
 */
public class DeviceEvaluator
{
	public final static class Device
	{
		private final String mName;
		private final String mDescription;
		private final Mixer  mMixer;
		private int          mRating;

		private Device(String name, String description, Mixer mixer)
		{
			mName        = name;
			mDescription = description;
			mMixer       = mixer;
			mRating      = 0;
		}

		public String getName       () { return mName;        }
		public String getDescription() { return mDescription; }
		public int    getRating     () { return mRating;      }

		public <T> int getMaxLines(Class<T> lineClass, AudioFormat audioFormat)
		{
			return mMixer.getMaxLines(new DataLine.Info(lineClass, audioFormat));
		}

		@SuppressWarnings("unchecked")
		public <T extends Line> T getLine(Class<T> lineClass, AudioFormat audioFormat)
		{
			try
            {
				DataLine.Info info = new DataLine.Info(lineClass, audioFormat, AudioSystem.NOT_SPECIFIED);
                return (T)mMixer.getLine(info);
            }
            catch(LineUnavailableException e) { }
			catch(IllegalArgumentException e) { }
			catch(SecurityException        e) { }

			return null;
		}
	}

	private final ArrayList<Device> mDevices;

	public DeviceEvaluator()
	{
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();

		mDevices = new ArrayList<Device>(mixerInfos.length);

		for(Mixer.Info info: mixerInfos) {
			mDevices.add(new Device(info.getName(), info.getDescription(), AudioSystem.getMixer(info)));
		}
	}

	public void setRating(Pattern namePattern, Pattern descriptionPattern, int rating)
	{
		for(Device device: mDevices) {
			if (namePattern != null) {
				if (namePattern.matcher(device.mName).matches()) {
					device.mRating = rating;
					continue;
				}

				if (descriptionPattern != null && namePattern.matcher(device.mDescription).matches()) {
					device.mRating = rating;
					continue;
				}
			}
		}
	}

	public List<Device> createDeviceList(AudioFormat audioFormat)
	{
		@SuppressWarnings("unchecked")
		ArrayList<Device> list = (ArrayList<Device>)mDevices.clone();
		sortDeviceList(list, audioFormat);

		final DataLine.Info  dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		ListIterator<Device> iterator     = list.listIterator();

		while(iterator.hasNext())
		{
			Device device = iterator.next();

			if(device.mMixer.getMaxLines(dataLineInfo) == 0) {
				iterator.remove();
			}
		}

		return list;
	}

	private static void sortDeviceList(List<Device> devices, AudioFormat audioFormat)
    {
        final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

        Collections.sort(devices, new Comparator<Device>()
        {
            @Override
			public int compare(Device device1, Device device2)
            {
				int numLines1 = device1.mMixer.getMaxLines(dataLineInfo);
				int numLines2 = device2.mMixer.getMaxLines(dataLineInfo);

				if(numLines2 == 0) {
					return -1;
				}

				if(device1.mRating == device2.mRating)
				{
					if(numLines1 == AudioSystem.NOT_SPECIFIED || numLines1 > numLines2) {
						return -1;
					}
				}
				else
				{
					if(device1.mRating > device2.mRating) {
						return -1;
					}
				}

				return 1;
            }
        });
    }

	public static void main(String[] args)
	{
		AudioFormat     format    = new AudioFormat(44100, 16, 2, true, false);
		DeviceEvaluator evaluator = new DeviceEvaluator();
		evaluator.setRating(Pattern.compile(".*PulseAudio.*")             , null, 1);
		evaluator.setRating(Pattern.compile(".*Java Sound Audio Engine.*"), null,-1);

		List<DeviceEvaluator.Device> list = evaluator.createDeviceList(format);

		for(Device device: list) {
			System.out.println(device.mName + " num lines " + device.getMaxLines(SourceDataLine.class, format));
		}
	}
}
