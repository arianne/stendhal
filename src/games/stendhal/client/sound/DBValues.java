/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.sound;

/**
 * is a smelly lazy class just keeping the precalculated dbValues
 * which represent loudness(?) 
 * 
 * TODO: remove the magic 100 , 101 numbers from DBValues
 * TODO: prove if the dbValues are as intended volume = 0 -> dbValue -> NegNAN, volume = 100 -> dbValue -> 0
 * 
 * @author astridemma 19.02.2007 
 * 
 */
public class DBValues {
	
	/**
	 *  dbValue[0] is mute and dbValue[100] is loudest
	 */
	private static final float[] dBValues = new float[101];

	static {
		// init our volume -> decibel map
		for (int i = 0; i < 101; i++) {
			double level = ((double) i) / 100;
			dBValues[i] = (float) (Math.log(level) / Math.log(10.0) * 20.0);
			
		}
	}
	/**
	 * calculates a dbValue accoring to the volume
	 * @param volume the volume to be calculated <p>
	 * any value < 0 will be adjusted to 0 <p>
	 * any value > 100 will be adjusted to 100
	 *  
	 * @return the calculated dbValue
	 */
	public static float getDBValue(int volume){
		if (volume < 0)
			volume = 0;
		if (volume > 100)
			volume = 100;
		return dBValues[volume]; 
	}
	
}
