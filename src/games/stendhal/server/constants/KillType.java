/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.constants;

/**
 * Definitions for killing enemies.
 */
public enum KillType {
	SOLO,
	SHARED,
	ANY;


	public boolean solo() {
		return this.equals(SOLO);
	}

	public boolean shared() {
		return this.equals(SHARED);
	}

	public boolean any() {
		return this.equals(SOLO) || this.equals(SHARED) || this.equals(ANY);
	}
}
