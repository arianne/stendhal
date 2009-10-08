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
package games.stendhal.server.entity.item.token;

import games.stendhal.server.entity.item.Item;
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
	 * A listener that will be notified on token move.
	 */
	public interface TokenMoveListener {

		/**
		 * a token was moved.
		 * 
		 * @param player
		 *            the player moving it
		 */
		void onTokenMoved(Player player);
	}

	/**
	 * Creates a new token.
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
	public Token(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Token(final Token item) {
		super(item);
	}

	@Override
	public void onPutOnGround(final Player player) {
		super.onPutOnGround(player);

		if ((player != null) && (tokenMoveListener != null)) {
			tokenMoveListener.onTokenMoved(player);
		}
	}

	/**
	 * Sets a TokenMoveListener.
	 * 
	 * @param tokenMoveListener
	 *            TokenMoveListener
	 */
	public void setTokenMoveListener(final TokenMoveListener tokenMoveListener) {
		this.tokenMoveListener = tokenMoveListener;
	}
}
