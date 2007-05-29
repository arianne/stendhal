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
package games.stendhal.server.entity;

import java.util.Arrays;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

public class Sign extends Entity {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Sign.class);

	/**
	 * Classes of signs that players, NPCs etc. can walk over
	 * and where you can put items on if they are not placed
	 * on a collision tile.
	 */
	private static final List<String> NON_OBSTACLE_CLASSES = Arrays.asList("book_blue", "book_red", "transparent");

	public static void generateRPClass() {
		try {
			RPClass sign = new RPClass("sign");
			sign.isA("entity");
			sign.addAttribute("text", Type.LONG_STRING);
			sign.addAttribute("class", Type.STRING);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public Sign() {
		super();
		setRPClass("sign");
		put("type", "sign");
	}

	public void setText(String text) {
		put("text", text);
	}

	/**
	 * States what type of sign this should be. This defines how
	 * it will look like in the client.
	 * @param clazz The sign class, e.g. "default" or "signpost".
	 */
	public void setClass(String clazz) {
		put("class", clazz);
	}

	@Override
	public boolean isObstacle(Entity entity) {
		return !(has("class") && NON_OBSTACLE_CLASSES.contains(get("class")));
	}

}
