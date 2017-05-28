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


import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author silvio
 */
public class AudibleAreaList extends LinkedList<AudibleArea> implements AudibleArea
{
    /**
	 * serial version uid
	 */
	private static final long serialVersionUID = 8277563402183797791L;

	@Override
	public float getHearingIntensity(float[] hearerPos)
    {
        if(isEmpty()) {
			return 0.0f;
		}

        Iterator<AudibleArea> iterator  = super.iterator();
        AudibleArea           area      = iterator.next();
        float                 intensity = area.getHearingIntensity(hearerPos);

        while(iterator.hasNext())
        {
            area = iterator.next();
            intensity = Math.max(intensity, area.getHearingIntensity(hearerPos));
        }

        return intensity;
    }

    @Override
	public void getClosestPoint(float[] result, float[] hearerPos)
    {
        if(isEmpty()) {
			return;
		}

        Iterator<AudibleArea> iterator    = super.iterator();
        AudibleArea           closestArea = iterator.next();
        float                 intensity   = closestArea.getHearingIntensity(hearerPos);

        while(iterator.hasNext())
        {
            AudibleArea area   = iterator.next();
            float       intens = area.getHearingIntensity(hearerPos);

            if(intens > intensity)
            {
                intensity   = intens;
                closestArea = area;
            }
        }

        closestArea.getClosestPoint(result, hearerPos);
    }
}
