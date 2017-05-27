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

import games.stendhal.common.math.Algebra;
import games.stendhal.common.math.Geometry;

/**
 *
 * @author silvio
 */
public class AudibleCapsuleArea implements AudibleArea
{
    private float[] mStartPoint;
    private float[] mEndPoint;
    private float   mInnerRadius;
    private float   mOuterRadius;

    public AudibleCapsuleArea(float[] startPoint, float[] endPoint, float innerRadius, float outerRadius)
    {
        mStartPoint  = startPoint.clone();
        mEndPoint    = endPoint.clone();
        mInnerRadius = innerRadius;
        mOuterRadius = outerRadius;
    }

    public void setArea(float innerRadius, float outerRadius)
    {
        mInnerRadius = innerRadius;
        mOuterRadius = outerRadius;
    }

    public void  setStartPoint (float[] point)  { Algebra.mov_Vecf(mStartPoint, point);  }
    public void  setEndPoint   (float[] point)  { Algebra.mov_Vecf(mEndPoint  , point);  }
    public void  getStartPoint (float[] result) { Algebra.mov_Vecf(result, mStartPoint); }
    public void  getEndPoint   (float[] result) { Algebra.mov_Vecf(result, mEndPoint  ); }
    public float getInnerRadius()               { return mInnerRadius;                   }
    public float getOuterRadius()               { return mOuterRadius;                   }

    @Override
	public float getHearingIntensity(float[] hearerPos)
    {
        float distance = Geometry.distanceSqrt_LinePointf(mStartPoint, mEndPoint, hearerPos);

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
        Geometry.closestPoint_CapsulePointf(result, mStartPoint, mEndPoint, mInnerRadius, hearerPos);
    }
}
