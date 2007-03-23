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

public class Pair<T1, T2> {

	private T1 first;

	private T2 second;

	public Pair(T1 o1, T2 o2) {
		first = o1;
		second = o2;
	}

	public T1 first() {
		return first;
	}

	public T2 second() {
		return second;
	}

	public void setFirst(T1 o1) {
		first = o1;
	}

	public void setSecond(T2 o2) {
		second = o2;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair object = (Pair) o;
			if ((first != null) && first.equals(object.first) && (second != null) && second.equals(object.second)) {
				return true;
			}

			if ((first == null) && (first == object.first) && (second != null) && second.equals(object.second)) {
				return true;
			}

			if ((second == null) && (second == object.second) && (first != null) && first.equals(object.first)) {
				return true;
			}

			if ((first == null) && (first == object.first) && (second == null) && (second == object.second)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		int h1 = 0;
		int h2 = 0;

		if (first != null) {
			h1 = first.hashCode();
		}

		if (second != null) {
			h2 = second.hashCode();
		}

		return h1 * h2;
	}
}
