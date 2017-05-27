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

import java.util.List;
import java.util.Random;
import java.util.Set;

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
	 *
	 * @return Either 1 or 2, equally distributed.
	 */
	public static int throwCoin() {
		return rand.nextInt(2) + 1;
	}

	/**
	 * Simulates rolling a dice with 6 sides.
	 *
	 * @return A random number between 1 and 6, equally distributed.
	 */
	public static int roll1D6() {
		return rand.nextInt(6) + 1;
	}

	/**
	 * Simulates rolling a dice with 20 sides.
	 *
	 * @return A random number between 1 and 20, equally distributed.
	 */
	public static int roll1D20() {
		return rand.nextInt(20) + 1;
	}

	/**
	 * Simulates rolling a dice with 100 sides.
	 *
	 * @return A random number between 1 and 100, equally distributed.
	 */
	public static int roll1D100() {
		return rand.nextInt(100) + 1;
	}


	/**
	 * Generates an equally distributed random number between <i>a</i> and <i>b</i> inclusive
	 * It doesn't matter if a or b is bigger.
	 *
	 * @param a
	 *          the first boundary number (upper or lower)
	 * @param b
	 *          the second boundary number (upper or lower)
	 *
	 * @return A random number between <i>a</i> and <i>b</i>, equally distributed.
	 */
	public static int randUniform(final int a, final int b) {
		final int max = Math.max(a, b);
		final int min = Math.min(a, b);

		return rand.nextInt(max - min + 1) + min;
	}

	/**
	 * Generates an equally distributed random number.
	 *
	 * @param n
	 *            the upper boundary
	 * @return A random number between 0 and <i>n</i> - 1, equally distributed.
	 */
	public static int rand(final int n) {
		return rand.nextInt(n);
	}

	/**
	 * Generates an equally distributed double precision random number.
	 *            the upper boundary
	 * @return A random number between 0 and 1, equally distributed.
	 */
	public static double rand() {
		return rand.nextDouble();
	}

	/**
	 * Given a list of any type, returns an arbitrary element, using an equal
	 * distribution. Generics are used so that the returned element will have
	 * the same type as the list's elements have.
	 *
	 * @param <T>
	 *            Any type.
	 * @param list
	 *            The list from which an element should be chosen.
	 * @return A random list element.
	 */
	public static <T> T rand(final List<T> list) {
		return list.get(rand(list.size()));
	}

	/**
	 * Given a set of any type, returns an arbitrary element, using an equal
	 * distribution. Generics are used so that the returned element will have
	 * the same type as the set's elements have.
	 *
	 * NOTE: This is not very efficient. If you need to do this on large sets
	 * several times per second, consider copying the set contents to an array,
	 * then call rand() on this array.
	 *
	 * @param <T>
	 *            Any type.
	 * @param set
	 *            The set from which an element should be chosen.
	 * @return A random set element.
	 */
	public static <T> T rand(final Set<T> set) {
		final int n = rand(set.size());
		int i = 0;
		for (final T element : set) {
			if (i == n) {
				return element;
			}
			i++;
		}
		// can't happen
		return null;
	}

	/**
	 * Given a array of any type, returns an arbitrary element, using an equal
	 * distribution. Generics are used so that the returned element will have
	 * the same type as the array's elements have.
	 *
	 * @param <T>
	 *            Any type.
	 * @param array
	 *            The array from which an element should be chosen.
	 * @return A random array element.
	 */
	public static <T> T rand(final T[] array) {
		return array[rand(array.length)];
	}

	/**
	 * Generates a normally distributed random number and rounds it.
	 *
	 * @param mean
	 *            The mean value
	 * @param sd
	 *            The standard deviation
	 * @return An integer near <i>mean</i>
	 */
	public static int randGaussian(final int mean, final int sd) {
		return (int) (rand.nextGaussian() * sd + mean);
	}

	/**
	 * Generates an exponentially distributed random number and rounds it.
	 *
	 * @param mean
	 *            The mean value
	 * @return An integer exponential variate <i>mean</i>
	 */
	public static int randExponential(final int mean) {
		return (int) (-mean * Math.log(rand.nextDouble()));
	}

	/**
	 * Calculate the probability for a given mean value in an exponential distribution
	 *
	 * @param mean the desired mean value of the distribution
	 * @return the probability to reach the given mean value (1 for mean == 0)
	 */
	public static double propabilityForMeanExp(final long mean) {
		if(mean == 0) {
			return 1;
		}
		double meandouble = mean;
		return 1d/meandouble;
	}

	/**
	 * Flip a coin to decide between true and false based on a probability
	 * @param propability the probability to get true
	 * @return true or false randomly
	 */
	public static boolean flipCoin(final double propability) {
		return rand.nextDouble() <= propability;
	}
}
