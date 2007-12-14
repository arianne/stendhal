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
	 * Generates an equally distributed random number.
	 * 
	 * @param n
	 *            the upper boundary
	 * @return A random number between 0 and <i>n</i> - 1, equally distributed.
	 */
	public static int rand(int n) {
		return rand.nextInt(n);
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
	public static <T> T rand(List<T> list) {
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
	public static <T> T rand(Set<T> set) {
		int n = rand(set.size());
		int i = 0;
		for (T element : set) {
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
	public static <T> T rand(T[] array) {
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
	public static int rand(int mean, int sd) {
		return (int) (rand.nextGaussian() * sd + mean);
	}
}
