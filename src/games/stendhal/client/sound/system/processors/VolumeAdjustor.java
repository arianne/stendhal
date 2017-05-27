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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import games.stendhal.client.sound.facade.Time;
import games.stendhal.client.sound.system.SignalProcessor;
import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Numeric;

/**
 * Adjust the volume of a PCM audio signal.
 * @author silvio
 */
public class VolumeAdjustor extends SignalProcessor
{
    private final AtomicInteger mCurrentVolume      = new AtomicInteger();
    private final AtomicInteger mTargetVolume       = new AtomicInteger();
	private final AtomicInteger mVolumeBeforeFading = new AtomicInteger();
    private final AtomicLong    mFadingDuration     = new AtomicLong();

    public VolumeAdjustor()
    {
        mCurrentVolume.set(floatToInt(1.0f));
        mTargetVolume.set(floatToInt(1.0f));
		mVolumeBeforeFading.set(floatToInt(1.0f));
        mFadingDuration.set(0);
    }

    public VolumeAdjustor(float volume)
    {
        mCurrentVolume.set(floatToInt(volume));
        mTargetVolume.set(floatToInt(volume));
		mVolumeBeforeFading.set(floatToInt(volume));
        mFadingDuration.set(0);
    }

    public void setVolume(float volume)
    {
        mCurrentVolume.set(floatToInt(volume));
		mVolumeBeforeFading.set(floatToInt(volume));
        mFadingDuration.set(0);
    }

	public float getVolume()
	{
		return intToFloat(mCurrentVolume.get());
	}

    public void startFading(float volume, Time duration)
    {
		mVolumeBeforeFading.set(mCurrentVolume.get());

        if(duration.getInNanoSeconds() <= 0)
        {
            mCurrentVolume.set(floatToInt(volume));
            mFadingDuration.set(0);
        }
        else
        {
            mTargetVolume.set(floatToInt(volume));
            mFadingDuration.set(duration.getInNanoSeconds());
        }
    }

	public void startFading(Time duration)
    {
        if(duration.getInNanoSeconds() <= 0)
        {
            mCurrentVolume.set(mVolumeBeforeFading.get());
            mFadingDuration.set(0);
        }
        else
        {
            mTargetVolume.set(mVolumeBeforeFading.get());
            mFadingDuration.set(duration.getInNanoSeconds());
        }
    }

    @Override
    protected void modify(float[] data, int frames, int channels, int rate)
    {
        assert data.length >= (frames * channels);

        if(mFadingDuration.get() <= 0)
        {
            float volume = intToFloat(mCurrentVolume.get());

            // if volume is zero we return without processing the audio data
            if(Algebra.isEqual_Scalf(volume, 0.0f)) {
				return;
			}

            if(!Algebra.isEqual_Scalf(volume, 1.0f))
            {
                for(int i=0; i<(frames*channels); ++i) {
					data[i] *= volume;
				}
            }

			// else if volume is 1 we propagate the unmodified audio data
        }
        else
        {
            Time fadingDuration  = new Time(mFadingDuration.get(), Time.Unit.NANO);
            Time segmentDuration = new Time(frames, rate);

            int   numSamples    = frames;
            float volume        = intToFloat(mCurrentVolume.get());
            float volumeSegment = intToFloat(mTargetVolume.get()) - intToFloat(mCurrentVolume.get());

            if(segmentDuration.getInNanoSeconds() > fadingDuration.getInNanoSeconds()) {
				numSamples = (int)fadingDuration.getInSamples(rate);
			} else {
				volumeSegment *= (float)((double)segmentDuration.getInNanoSeconds() / (double)fadingDuration.getInNanoSeconds());
			}

            for(int i=0; i<numSamples; ++i)
            {
                int    index = i * channels;
                double vol   = volume + (volumeSegment * i / numSamples);

                for(int c=0; c<channels; ++c) {
					data[index + c] *= vol;
				}
            }

            for(int i=(numSamples * channels); i<(frames * channels); ++i) {
				data[i] *= volume + volumeSegment;
			}

            mCurrentVolume.addAndGet(floatToInt(volumeSegment));
            mFadingDuration.addAndGet(-segmentDuration.getInNanoSeconds());
        }

        super.propagate(data, frames, channels, rate);
    }

    private static int   floatToInt(float v) { return Numeric.floatToInt(v, 10000000.0f); }
    private static float intToFloat(int   v) { return Numeric.intToFloat(v, 10000000.0f); }
}
