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

import java.util.Objects;

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

		return Objects.equals(prim, other.prim) && Objects.equals(sec, other.sec)
				&& Objects.equals(third, other.third);
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
