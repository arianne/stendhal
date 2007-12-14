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
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Tokens are items which trigger an event on movement. They may not be
 * equipped.
 * 
 * @author hendrik
 */
public class Token extends Item {

	private TokenMoveListener tokenMoveListener;

	/**
	 * a listener that will be notified on token move
	 */
	public interface TokenMoveListener {

		/**
		 * a token was moved
		 * 
		 * @param player
		 *            the player moving it
		 */
		void onTokenMoved(Player player);
	}

	/**
	 * Create a new token
	 * 
	 * @param name
	 *            name of item
	 * @param clazz
	 *            class (or type) of item
	 * @param subclass
	 *            subclass of this item
	 * @param attributes
	 *            attributes (like attack). may be empty or <code>null</code>
	 */
	public Token(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public Token(Token item) {
		super(item);
	}

	@Override
	public void onPutOnGround(Player player) {
		super.onPutOnGround(player);
		if ((player != null) && (tokenMoveListener != null)) {
			tokenMoveListener.onTokenMoved(player);
		}
	}

	/**
	 * Sets a TokenMoveListener
	 * 
	 * @param tokenMoveListener
	 *            TokenMoveListener
	 */
	public void setTokenMoveListener(TokenMoveListener tokenMoveListener) {
		this.tokenMoveListener = tokenMoveListener;
	}
}
