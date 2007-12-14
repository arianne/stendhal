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
package games.stendhal.server.entity.mapstuff.sign;

import games.stendhal.server.entity.Entity;

import org.apache.log4j.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

/**
 * A sign which is placed on the ground and can be right clicked by players to
 * be read.
 */
public class Sign extends Entity {
	/**
	 * The sign text attribute name.
	 */
	protected static final String ATTR_TEXT = "text";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Sign.class);

	public static void generateRPClass() {
		try {
			RPClass sign = new RPClass("sign");
			sign.isA("entity");
			sign.addAttribute(ATTR_TEXT, Type.LONG_STRING);
			sign.addAttribute("class", Type.STRING);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Create a sign.
	 */
	public Sign() {
		setRPClass("sign");
		put("type", "sign");

		setResistance(100);
	}

	/**
	 * Get the sign's text.
	 * 
	 * @return The sign text.
	 */
	public String getText() {
		if (has(ATTR_TEXT)) {
			return get(ATTR_TEXT);
		} else {
			return null;
		}
	}

	/**
	 * Set the sign text.
	 * 
	 * @param text
	 *            The sign text.
	 */
	public void setText(String text) {
		put(ATTR_TEXT, text);
	}
}
