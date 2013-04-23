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
package games.stendhal.common.math;

/**
 * Convert between float and int values using the given accuracy as multiplier.
 * @author silvio
 */
public class Numeric
{
    public static int floatToInt(float value, float accuracy)
    {
        return (int)(value * accuracy);
    }

    public static float intToFloat(int value, float accuracy)
    {
        return value / accuracy;
    }
}
