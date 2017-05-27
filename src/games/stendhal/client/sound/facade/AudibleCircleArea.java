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
package games.stendhal.client.sound.facade;

import java.util.Arrays;

import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Geometry;

/**
 * AudibleCircleAfrea defines a circular area for audible objects.
 * @author silvio
 */
public class AudibleCircleArea implements AudibleArea
{
    private final float[] mCenter;
    private float   mInnerRadius;
    private float   mOuterRadius;

    public AudibleCircleArea(float[] center, float innerRadius, float outerRadius)
    {
    	mCenter = new float[center.length];
    	System.arraycopy(center, 0, mCenter, 0, center.length);
        mInnerRadius = innerRadius;
        mOuterRadius = outerRadius;
    }

    public void setArea(float innerRadius, float outerRadius)
    {
        mInnerRadius = innerRadius;
        mOuterRadius = outerRadius;
    }

    public void  setPosition   (float[] position) { Algebra.mov_Vecf(mCenter, position); }
    public void  getPosition   (float[] result)   { Algebra.mov_Vecf(result, mCenter);   }
    public float getInnerRadius()                 { return mInnerRadius;                 }
    public float getOuterRadius()                 { return mOuterRadius;                 }

    @Override
	public float getHearingIntensity(float[] hearerPos)
    {
        float distance = Algebra.distanceSqrt_Vecf(hearerPos, mCenter);

        if(distance > (mOuterRadius * mOuterRadius)) {
			return 0.0f;
		} else if(distance < (mInnerRadius * mInnerRadius)) {
			return 1.0f;
		}

        distance = (float)Math.sqrt(distance) - mInnerRadius;
        return 1.0f - distance / (mOuterRadius - mInnerRadius);
    }

    @Override
	public void getClosestPoint(float[] result, float[] hearerPos)
    {
        Geometry.closestPoint_SpherePointf(result, mCenter, mInnerRadius, hearerPos);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AudibleCircleArea [mCenter=");
		sb.append(Arrays.toString(mCenter));
		sb.append(", mInnerRadius=" + mInnerRadius);
		sb.append(", mOuterRadius=");
		sb.append(mOuterRadius + "]");
		return sb.toString();
	}

}
