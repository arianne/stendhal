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

/**
 *
 * @author silvio
 */
public class InfiniteAudibleArea implements AudibleArea
{
    @Override
	public float getHearingIntensity(float[] hearerPos)                 { return 1.0f;                         }
    @Override
	public void  getClosestPoint    (float[] result, float[] hearerPos) { Algebra.mov_Vecf(result, hearerPos); }
}
