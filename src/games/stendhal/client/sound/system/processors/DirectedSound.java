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
import games.stendhal.common.math.Algebra;

/**
 * Signal processor to handle directed sound.
 * @author silvio
 */
public class DirectedSound extends SignalProcessor
{
    private final float[] mUpVector     = { 0.0f, 1.0f, 0.0f };
    private float         mIntensity    = 1.0f;
    private float         mLVolume      = 1.0f;
    private float         mRVolume      = 1.0f;
	private float[]       mOutputBuffer = null;

    public DirectedSound() { }

    public DirectedSound(float[] upVector)
    {
        assert upVector.length == 3;
        Algebra.mov_Vecf(mUpVector, upVector);
    }

    public synchronized void setUpVector(float[] upVector)
    {
        assert upVector.length == 3;
        Algebra.mov_Vecf(mUpVector, upVector);
    }

    public synchronized void setPositions3D(float[] sourcePos, float[] hearerPos, float[] hearerLookingDirection, float intensity)
    {
        if(Algebra.isEqual_Vecf(sourcePos, hearerPos))
        {
            mRVolume   = intensity;
            mLVolume   = intensity;
            mIntensity = intensity;
            return;
        }

        float[] distance   = new float[3]; // distance vector between the sound position (sourcePos) and the hearer (hearerPos)
        float[] localYAxis = new float[3]; // the y-axis of the hearer depending on the hearers looking direction

        Algebra.sub_Vecf(distance, sourcePos, hearerPos);
        Algebra.cross_Vec3f(localYAxis, hearerLookingDirection, mUpVector);
        Algebra.normalize_Vecf(distance  , distance  );
        Algebra.normalize_Vecf(localYAxis, localYAxis);

        float leftRightRatio = Algebra.dot_Vecf(localYAxis, distance);

        mLVolume   = Math.min(1.0f - leftRightRatio, 1.0f);
        mRVolume   = Math.min(1.0f + leftRightRatio, 1.0f);
        mLVolume  += (1.0f - mLVolume) * intensity;
        mRVolume  += (1.0f - mRVolume) * intensity;
        mIntensity = intensity;
    }

    public synchronized void setPositions2D(float[] sourcePos, float[] hearerPos, float[] hearerLookingDirection, float intensity)
    {
        if(Algebra.isEqual_Vecf(sourcePos, hearerPos))
        {
            mRVolume   = intensity;
            mLVolume   = intensity;
            mIntensity = intensity;
            return;
        }

        float[] distance   = new float[2]; // distance vector between the sound position (sourcePos) and the hearer (hearerPos)
        float[] localYAxis = new float[2]; // the y-axis of the hearer depending on the hearers looking direction

        Algebra.sub_Vecf(distance, sourcePos, hearerPos);
        Algebra.cross_Vec2f(localYAxis, hearerLookingDirection);
        Algebra.normalize_Vecf(distance  , distance  );
        Algebra.normalize_Vecf(localYAxis, localYAxis);

        float leftRightRatio = Algebra.dot_Vecf(localYAxis, distance);

        mLVolume  = Math.min(1.0f - leftRightRatio, 1.0f);
        mRVolume  = Math.min(1.0f + leftRightRatio, 1.0f);
        mLVolume += (1.0f - mLVolume) * (intensity * intensity);
        mRVolume += (1.0f - mRVolume) * (intensity * intensity);
        mIntensity = intensity;
    }

    @Override
    protected synchronized void modify(float[] data, int samples, int channels, int rate)
    {
        assert channels > 0 && channels <= 2;
        assert data.length >= (samples * channels);

        if(channels == 1)
        {
			mOutputBuffer = Field.expand(mOutputBuffer, (samples * 2), false);

            for(int i=0; i<samples; ++i)
            {
                mOutputBuffer[i*2 + 0] = data[i] * mLVolume; // multiply with the positional volume
                mOutputBuffer[i*2 + 1] = data[i] * mRVolume; // do the same for the right channel
            }

            data     = mOutputBuffer;
            channels = 2;
        }
        else // if(channels == 2)
        {
            for(int i=0; i<samples; ++i)
            {
                float left  = data[i*2 + 0];         // get the current value of the left channel
                float right = data[i*2 + 1];         // get the current value of the right channel
                float mono  = (left + right) / 2.0f; // calculate the mono value for the two channels

                left  += (mono - left ) * mIntensity; // interpolate between the left stereo value and the mono value
                right += (mono - right) * mIntensity; // do the same for the right channel
                left  *= mLVolume;                    // multiply with the positional volume
                right *= mRVolume;                    // do the same for the right channel

                data[i*2 + 0] = left;
                data[i*2 + 1] = right;
            }
        }

        super.propagate(data, samples, channels, rate);
    }
}
