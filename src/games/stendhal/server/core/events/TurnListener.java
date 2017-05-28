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
package games.stendhal.server.core.events;

/**
 * Implementing classes can be notified that a certain turn number has been
 * reached.
 *
 * After registering at the TurnNotifier, the TurnNotifier will wait until the
 * specified turn number has been reached, and notify the TurnListener.
 *
 * A string can be passed to the TurnNotifier while registering; this string
 * will then be passed back to the TurnListener when the specified turn number
 * has been reached. Using this string, a TurnListener can register itself
 * multiple times at the TurnNotifier.
 *
 * @author hendrik
 */
public interface TurnListener {
	/**
	 * This method is called when the turn number is reached.
	 *
	 * @param currentTurn
	 *            current turn number
	 */
	void onTurnReached(int currentTurn);
}
