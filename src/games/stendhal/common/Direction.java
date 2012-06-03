/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

/**
 * a direction to face or walk to
 *
 * @author hendrik
 */
public enum Direction {
	/** do not move */
	STOP,
	/** up, away from the screen */
	UP,
	/** to the right */
	RIGHT,
	/** down, facing the player */
	DOWN,
	/** to the left */
	LEFT;
}
