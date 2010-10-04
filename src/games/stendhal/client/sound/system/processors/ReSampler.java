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

import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.client.sound.system.Time;

/**
 *
 * @author silvio
 */
public class ReSampler extends SignalProcessor
{
    private int     mNumChannels;
    private int     mDelayInSamples;
    private float[] mBuffer;
    private boolean mStarted = false;

    ReSampler(Time delay, int channels, int sampleRate)
    {
        assert delay.getInSamples(sampleRate) < Integer.MAX_VALUE: "delay time is to long";

        mDelayInSamples = (int)delay.getInSamples(sampleRate);
        mBuffer         = new float[(int)(mDelayInSamples * channels)];
    }

    public synchronized void start()
    {
        mStarted = true;
    }
    
    @Override
    protected void modify(float[] data, int samples, int channels, int rate)
    {
        
        super.propagate(data, samples, channels, rate);
    }
}
