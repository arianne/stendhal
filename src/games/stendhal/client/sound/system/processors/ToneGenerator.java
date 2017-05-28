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
package games.stendhal.client.sound.system.processors;

import java.util.ArrayList;

import games.stendhal.client.sound.system.SignalProcessor;

/**
 * Generates a PCM audio signal consisting of sine waveforms
 * with specified frequency and volume.
 * @author silvio
 */
public class ToneGenerator extends SignalProcessor
{
    public static class Tone
    {
        private float mVolume;
        private float mFrequency;
        private long  mPosition = 0;

        public Tone(float volume, float frequency)
        {
            mVolume    = volume;
            mFrequency = frequency;
        }
    }

    private int                   mSampleRate;
    private int                   mNumChannels;
    private float[]               mSamples;
    private final ArrayList<Tone> mTones = new ArrayList<Tone>();

    public ToneGenerator(int channels, int rate, int numFramesToBuffer)
    {
        mNumChannels = channels;
        mSampleRate  = rate;
        mSamples     = new float[numFramesToBuffer * mNumChannels];
    }

    public synchronized void addTone(Tone tone)
    {
        mTones.add(tone);
    }

    @Override
    protected boolean generate()
    {
        int index = 0;

        for(Tone tone: mTones)
        {
            makeTone(tone, mSamples, (mSamples.length / mNumChannels), mNumChannels, (index != 0));
            ++index;
        }

        super.propagate(mSamples, (mSamples.length / mNumChannels), mNumChannels, mSampleRate);
        return true;
    }

    @Override
    protected synchronized void modify(float[] data, int samples, int channels, int rate)
    {
        for(Tone tone: mTones) {
			makeTone(tone, data, samples, channels, true);
		}

        super.propagate(data, samples, channels, rate);
    }

    private void makeTone(Tone tone, float[] data, int samples, int channels, boolean mixSound)
    {
        final double RAD                      = 2.0 * Math.PI;
        final float  frequencySampleRateRatio = tone.mFrequency / mSampleRate;

        if(mixSound)
        {
            for(int i=0; i<samples; ++i)
            {
                int   index = i * channels;
                float value = (float)Math.sin(RAD * frequencySampleRateRatio * tone.mPosition);

                value *= tone.mVolume;

                for(int c=0; c<channels; ++c)
				 {
					data[index + c] = data[index + c] + value - data[index + c] * value;	//MF: mix using amplitude modulation between the generated tones?
				}

                ++tone.mPosition;
            }
        }
        else
        {
            for(int i=0; i<samples; ++i)
            {
                int   index = i * channels;
                float value = (float)Math.sin(RAD * frequencySampleRateRatio * tone.mPosition);

                value *= tone.mVolume;

                for(int c=0; c<channels; ++c) {
					data[index + c] = value;
				}

                ++tone.mPosition;
            }
        }
    }
}
