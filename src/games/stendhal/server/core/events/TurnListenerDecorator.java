/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
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
 * a TurnListener decorator. Multiple instance can point to the same
 * real TurnListener but will be treated as unique listeners by the
 * set in the TurnNotifier.
 *
 * @author hendrik
 */
public class TurnListenerDecorator implements TurnListener {
	private TurnListener turnListener;

	/**
	 * creates a new TurnListenerDecorator
	 *
	 * @param turnListener the real turnlistener to delegate to
	 */
	public TurnListenerDecorator(TurnListener turnListener) {
		this.turnListener = turnListener;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		if (turnListener != null) {
			turnListener.onTurnReached(currentTurn);
		}
	}

}
