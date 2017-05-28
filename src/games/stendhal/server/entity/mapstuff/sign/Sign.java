/* $Id$ */
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
package games.stendhal.server.entity.mapstuff.sign;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

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
			final RPClass sign = new RPClass("sign");
			sign.isA("entity");
			sign.addAttribute(ATTR_TEXT, Type.LONG_STRING, Definition.HIDDEN);
			sign.addAttribute(Actions.ACTION, Type.STRING);
			sign.addAttribute("class", Type.STRING);
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a sign.
	 */
	public Sign() {
		setRPClass("sign");
		put(Actions.TYPE, "sign");
		put(Actions.ACTION, Actions.READ);
		setResistance(100);
	}

	/**
	 * Creates a sign based on an existing RPObject. This is just for loading
	 * a sign from the database, use the other constructors.
	 * @param rpobject
	 */
	public Sign(final RPObject rpobject) {
		super(rpobject);
		put(Actions.ACTION, Actions.READ);
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
	public void setText(final String text) {
		put(ATTR_TEXT, text);
	}

	@Override
	public String describe() {
		final String text = getText();
		if (text == null) {
			return "You see a sign without any text";
		}

		if (text.contains("\n")) {
			// The sign's text has multiple lines. Add a linebreak after
			// "you read" so that it is easier readable.
			return "You read:\n\"" + text + "\"";
		} else {
			return "You read: \"" + text + "\"";
		}
	}


}
