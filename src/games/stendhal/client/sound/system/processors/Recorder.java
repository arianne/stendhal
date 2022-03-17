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

import games.stendhal.client.sound.Field;
import games.stendhal.client.sound.system.SignalProcessor;

/**
 * Recorder can be used to record sound signals in memory.
 * @author silvio
 */
public class Recorder extends SignalProcessor
{
	public class Player extends SignalProcessor
	{
		private float[] mOutputBuffer;
		private int     mNumSamplesRead = 0;

		private Player(int outputNumSamples)
		{
			mOutputBuffer = new float[outputNumSamples * mNumChannels];
		}

		public synchronized void    restart           () { mNumSamplesRead = 0;                           }
		public synchronized boolean reachedEndOfStream() { return mNumSamplesRead >= mNumSamplesBuffered; }
		public synchronized int     getNumChannels    () { return mNumChannels;                           }
		public synchronized int     getSampleRate     () { return mSampleRate;                            }
		public synchronized int     getNumSamples     () { return mNumSamplesBuffered / mNumChannels;     }

		@Override
		protected synchronized boolean generate()
		{
			if(reachedEndOfStream())
			{
				super.quit();
				return false;
			}

			int outputBufferSize    = mOutputBuffer.length;
			int numSamplesAvailable = mNumSamplesBuffered - mNumSamplesRead;
			int numSamples          = Math.min(outputBufferSize, numSamplesAvailable);

			System.arraycopy(mData, mNumSamplesRead, mOutputBuffer, 0, numSamples);
			mNumSamplesRead += numSamples;

			super.propagate(mOutputBuffer, (numSamples / mNumChannels), mNumChannels, mSampleRate);
			return true;
		}
	}

    private float[] mData               = null;
    private int     mNumChannels        = 1;
    private int     mSampleRate         = 0;
    private int     mNumSamplesBuffered = 0;

	public Player createPlayer(int outputNumSamplesPerChannel)
	{
		return new Player(outputNumSamplesPerChannel);
	}

    public synchronized void clear()
    {
        mNumSamplesBuffered = 0;
        mData               = null;
    }

    public synchronized int getNumChannels() { return mNumChannels;                           }
    public synchronized int getSampleRate () { return mSampleRate;                            }
    public synchronized int getNumSamples () { return mNumSamplesBuffered / mNumChannels;     }

    public synchronized void trim()
    {
        if(mData.length != mNumSamplesBuffered)
        {
            float[] temp = new float[mNumSamplesBuffered];
            System.arraycopy(mData, 0, temp, 0, mNumSamplesBuffered);
            mData = temp;
        }
    }

    @Override
    protected void finished()
    {
        trim();
        super.quit();
    }

    @Override
    protected synchronized void modify(float[] data, int samples, int channels, int rate)
    {
        if(data == null || samples == 0) {
			return;
		}

        if(mData == null)
        {
            mData        = new float[samples * channels * 3];
            mNumChannels = channels;
            mSampleRate  = rate;
        }

        assert mNumChannels == channels && mSampleRate == rate;

        int remainingBufferSpace = mData.length - mNumSamplesBuffered;
        int requiredBufferSpace  = samples * channels;

        // if the buffer is to small to hold the whole data, we expand it
        if(remainingBufferSpace < requiredBufferSpace) {
			mData = Field.expand(mData, (mNumSamplesBuffered + requiredBufferSpace) * 2, true);
		}

        System.arraycopy(data, 0, mData, mNumSamplesBuffered, requiredBufferSpace);
        mNumSamplesBuffered += requiredBufferSpace;

        super.propagate(data, samples, channels, rate);
    }
}
