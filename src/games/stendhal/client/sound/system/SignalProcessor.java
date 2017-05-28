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
package games.stendhal.client.sound.system;


/**
 * Every class that either wants to modify or generate a stream of
 * PCM audio data should derive from this class
 * <p>
 * Each SignalProcessor is a chain link in a doubly linked list, a processing chain.
 * The first SignalProcessor in such a chain should always generate audio data
 * otherwise no data will arrive at the quit of the chain
 *
 * @author silvio
 */
public abstract class SignalProcessor
{
    private SignalProcessor mNext = null;
    private SignalProcessor mPrev = null;

    /**
     * Connects this SignalProcessor to the next one
     * (it is inserted in the processing chain before nextProcessor)
     *
     * @param processor nextProcessor
     * @param before flag to insert processor before this object
     */
    public final synchronized void insert(SignalProcessor processor, boolean before)
    {
        assert processor != this;

        if(mNext != null) { mNext.mPrev = mPrev; }
        if(mPrev != null) { mPrev.mNext = mNext; }

        if(processor != null)
        {
            if(before)
            {
                mNext = processor;
                mPrev = processor.mPrev;

                if(processor.mPrev != null) {
					processor.mPrev.mNext = this;
				}

                processor.mPrev = this;
            }
            else
            {
                mNext = processor.mNext;
                mPrev = processor;

                if(processor.mNext != null) {
					processor.mNext.mPrev = this;
				}

                processor.mNext = this;
            }
        }
        else
        {
            mNext = null;
            mPrev = null;
        }
    }

    public final synchronized void connectTo(SignalProcessor processor, boolean before)
    {
        if(before)
        {
            if(mNext == processor) {
				return;
			}

            if(mNext           != null) { mNext.mPrev           = null; }
            if(processor.mPrev != null) { processor.mPrev.mNext = null; }

            mNext           = processor;
            processor.mPrev = this;
        }
        else
        {
            if(mPrev == processor) {
				return;
			}

            if(mPrev           != null) { mPrev.mNext           = null; }
            if(processor.mNext != null) { processor.mNext.mPrev = null; }

            mPrev           = processor;
            processor.mNext = this;
        }
    }

    public final synchronized void split(boolean before)
    {
        if(before)
        {
            if(mPrev != null)
			{
                mPrev.mNext = null;
				mPrev       = null;
			}
        }
        else
        {
            if(mNext != null)
			{
                mNext.mPrev = null;
				mNext       = null;
			}
        }
    }

    /**
     * Replaces this SignalProcessor with "processor" in the processing chain
     *
     * @param processor
     */
    public final synchronized void replace(SignalProcessor processor)
    {
        if(processor == this) {
			return;
		}

        if(processor != null)
        {
            processor.disconnect();

			if(mNext != null) { mNext.mPrev = processor; }
			if(mPrev != null) { mPrev.mNext = processor; }

            processor.mNext = mNext;
            processor.mPrev = mPrev;
        }
        else
        {
            disconnect();
        }
    }

    /**
     * Removes this SignalProcessor from the processing chain
	 * leaving adjacent SignalProcessors disconnected
     */
    public final void disconnect()
    {
		if(mNext != null)
		{
			mNext.mPrev = null;
			mNext       = null;
		}

		if(mPrev != null)
		{
			mPrev.mNext = null;
			mPrev       = null;
		}
    }

    /**
     * This function should be called from a derived class
     * to propagate the modified audio data to the next SignalProcessor in the chain
     *
     * @param data
     * @param samples
     * @param channels
     * @param rate
     */
    protected final synchronized void propagate(float[] data, int samples, int channels, int rate)
    {
        if(mNext != null) {
			mNext.modify(data, samples, channels, rate);
		}
    }

    public final synchronized void quit()
    {
        if(mNext != null) {
			mNext.finished();
		}
    }

    /**
     * This will call the generate() method of the first
     * SignalProcessor in the processing chain
     *
     * @return <code>true</code>, until the stream is finished
     */
    public synchronized boolean request()
    {
        if(mPrev != null) {
			return mPrev.request();
		}

        return generate();
    }

    /**
     * This function should be overwritten by all classes that want to
     * modify an PCM audio stream. The audio data is uniform and interleaved.
     * uniform:     Each sample has a value between -1.0 and 1.0
     * interleaved: The channels are not separated. They are bundled in frames
     *              e.g. if there is stereo PCM data:
     *              data[0] and data[1] are the left and right channels of sample 0
     *              data[2] and data[3] are the left and right channels of sample 1
     *              data[4] and data[5] are the left and right channels of sample 2
     *              and so on ...
     *
	 * The number of samples can be calculated by: frames * channels
	 *
     * @param data     the audio data
     * @param frames   the number of sample frames contained in "data"
     * @param channels number of channels
     * @param rate     the sample rate
     */
    protected void modify(float[] data, int frames, int channels, int rate)
    {
        propagate(data, frames, channels, rate);
    }

    /**
     * This function should be overwritten by all classes that want to
     * generate an PCM audio stream e.g. a mp3 decoder, a frequency generator, ...
     *
     * @return <code>true</code>, until the stream is finished
     */
    protected boolean generate() { return false; }

    protected void finished() { quit(); }

    /**
     * This function will create a processing chain by connecting any number
     * of SignalProcessors together
     *
     * @param processors any number of SignalProcessors to insert together
     */
    public static void createChain(SignalProcessor ...processors)
    {
        int l = processors.length;

        if(l >= 2)
        {
            for(int i=1; i<(l - 1); ++i) {
				processors[i].insert(processors[i-1], false);
			}

            processors[0  ].connectTo(processors[1  ], true);
            processors[l-1].connectTo(processors[l-2], false);
        }
    }
}
