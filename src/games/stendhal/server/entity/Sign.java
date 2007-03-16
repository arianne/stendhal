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

import java.awt.geom.Rectangle2D;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;

public class Sign extends Entity {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Sign.class);

	public static void generateRPClass() {
		try {
			RPClass sign = new RPClass("sign");
			sign.isA("entity");
			sign.add("text", RPClass.LONG_STRING);
			sign.add("class", RPClass.LONG_STRING);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public Sign() throws AttributeNotFoundException {
		super();
		put("type", "sign");
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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
}
