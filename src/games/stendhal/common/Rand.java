/* $Id$ */
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
package games.stendhal.common;

import java.util.Random;

/**
 * Helper functions to generate random numbers.
 */
public class Rand {
	private static Random rand;

	static {
		rand = new Random();
	}

	/**
	 * Simulates flipping a coin.
	 * @return Either 1 or 2, equally distributed.
	 */
	public static int throwCoin() {
		return rand.nextInt(2) + 1;
	}
	
	/**
	 * Simulates rolling a dice with 6 sides.
	 * @return A random number between 1 and 6, equally distributed.
	 */
	public static int roll1D6() {
		return rand.nextInt(6) + 1;
	}

	/**
	 * Simulates rolling a dice with 20 sides.
	 * @return A random number between 1 and 20, equally distributed.
	 */
	public static int roll1D20() {
		return rand.nextInt(20) + 1;
	}

	/**
	 * Simulates rolling a dice with 100 sides.
	 * @return A random number between 1 and 100, equally distributed.
	 */
	public static int roll1D100() {
		return rand.nextInt(100) + 1;
	}

	/**
	 * Generates an equally distributed random number.
	 * @param n the upper boundary
	 * @return A random number between 0 and <i>n</i> - 1, equally
	 *         distributed.
	 */
	public static int rand(int n) {
		return rand.nextInt(n);
	}

	/**
	 * Generates a normally distributed random number and rounds it. 
	 * @param mean The mean value
	 * @param sd The standard deviation
	 * @return An integer near <i>mean</i>
	 */
	public static int rand(int mean, int sd) {
		return (int) (rand.nextGaussian() * sd + mean);
	}
}
