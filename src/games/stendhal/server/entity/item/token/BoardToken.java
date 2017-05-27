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
package games.stendhal.server.entity.item.token;

import java.util.Map;

import games.stendhal.server.entity.player.Player;

/**
 * a token to be used on a game board
 *
 * @author hendrik
 */
public class BoardToken extends Token {
	private int homeX = 1;
	private int homeY = 1;
	private int lastX = 1;
	private int lastY = 1;
	private int moveCountSinceHome = 0;

	/**
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public BoardToken(String name, String clazz, String subclass,Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * @param item
	 */
	public BoardToken(Token item) {
		super(item);
	}

	/**
	 * was this token on its home spot before the last move?
	 *
	 * @return <code>true</code> iff the token was on the home spot before this move
	 */
	public boolean wasMovedFromHomeInLastMove() {
		return moveCountSinceHome <= 1;
	}

	@Override
	public void onPutOnGround(final Player player) {
		moveCountSinceHome++;
		super.onPutOnGround(player);
		lastX = getX();
		lastY = getY();
	}

	/**
	 * sets the home position for this token.
	 *
	 * @param x x
	 * @param y y
	 */
	public void setHomePosition(int x, int y) {
		homeX = x;
		homeY = y;
	}

	/**
	 * moves this token back to the home position
	 */
	public void resetToHomePosition() {
		this.setPosition(homeX, homeY);
		lastX = homeX;
		lastY = homeY;
		moveCountSinceHome = 0;
		notifyWorldAboutChanges();
	}

	public void undoMove() {
		this.setPosition(lastX, lastY);
		moveCountSinceHome--;
	}
}
