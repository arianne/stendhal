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

import games.stendhal.client.sound.Dsp;
import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.system.SignalProcessor;

/**
 * Sound processor stage to re-sample audio signals using another sample rate.
 * @author silvio
 */
public class ReSampler extends SignalProcessor
{
//	private final int     mNumChannels;
    private final int	  mSampleRate;
//	private final int     mDelayInSamples;
//	private final float[] mBuffer;
//	private boolean mStarted = false;

    ReSampler(Time delay, int channels, int sampleRate)
    {
        assert delay.getInSamples(sampleRate) < Integer.MAX_VALUE: "delay time is to long";

//		mNumChannels	= channels;
        mSampleRate		= sampleRate;
//      mDelayInSamples = (int)delay.getInSamples(sampleRate);
//		mBuffer         = new float[(int)(mDelayInSamples * channels)];
    }

//    public synchronized void start()
//    {
//        mStarted = true;
//    }

    /**
     * Modify the PCM audio stream. The audio data is uniform and interleaved.
	 *
     * @param data     the audio data
     * @param frames   the number of sample frames contained in "data"
     * @param channels number of channels
     * @param rate     the sample rate
     */
    @Override
    protected void modify(float[] data, int frames, int channels, int rate)
    {

    	//TODO implement using a pre-allocated buffer to avoid repeated memory allocation and use the delay given in the constructor

    	data = Dsp.convertSampleRate(data, frames, channels, mSampleRate, rate);

        super.propagate(data, data.length, channels, rate);
    }
}
