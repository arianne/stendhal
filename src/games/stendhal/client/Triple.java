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
package games.stendhal.client;

/**
 * A container for three objects.
 *
 * @param <P> type of first object
 * @param <S> type of second object
 * @param <T> type of third object
 */
public final class Triple<P, S, T> {
	// they are used in equals and hashcode
	private final P prim;
	private final S sec;
	private final T third;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		int add;

		if (prim == null) {
			add = 0;
		} else {
			add = prim.hashCode();
		}

		result = prime * result + add;
		if (sec == null) {
			add = 0;
		} else {
			add = sec.hashCode();
		}
		result = prime * result + add;

		if (third == null) {
			add = 0;
		} else {
			add = third.hashCode();
		}

		result = prime * result + add;
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Triple)) {
			return false;
		}
		
		final Triple<P, S , T> other = (Triple <P, S, T>) obj;
		
		return nullSafeEquals(prim, other.prim) && nullSafeEquals(sec, other.sec)
				&& nullSafeEquals(third, other.third);
	}
	
	/**
	 * Null safe equality check for objects. <b>Note: this should be replaced
	 * 	with Objects.equals() once we start requiring java 7.</b>
	 * 
	 * @param a first object
	 * @param b second object
	 * @return <code>true</code> if both <code>a</code> and <code>b</code> are
	 * 	<code>null</code> or if they are equal otherwise. In any other case the
	 * 	result is <code>false</code>
	 */
	private boolean nullSafeEquals(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	/**
	 * Create a triple.
	 * 
	 * @param prim first object
	 * @param sec second object
	 * @param third third object
	 */
	public Triple(final P prim, final S sec, final T third) {
		this.prim = prim;
		this.sec = sec;
		this.third = third;
	}
}
