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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Triple<P, S , T> other = (Triple <P, S, T>) obj;
		if (prim == null) {
			if (other.prim != null) {
				return false;
			}
		} else if (!prim.equals(other.prim)) {
			return false;
		}
		if (sec == null) {
			if (other.sec != null) {
				return false;
			}
		} else if (!sec.equals(other.sec)) {
			return false;
		}
		if (third == null) {
			if (other.third != null) {
				return false;
			}
		} else if (!third.equals(other.third)) {
			return false;
		}
		return true;
	}

	public Triple(final P prim, final S sec, final T third) {
		this.prim = prim;
		this.sec = sec;
		this.third = third;
	}

	

}
