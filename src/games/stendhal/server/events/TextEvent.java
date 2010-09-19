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
package games.stendhal.server.events;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * A public text message.
 *
 * @author hendrik
 */
public class TextEvent extends RPEvent {
	private static final String RPCLASS_NAME = "text";
	private static final String TEXT = "text";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
	}

	/**
	 * Creates a new text event.
	 *
	 * @param text Text
	 */
	public TextEvent(final String text) {
		super(RPCLASS_NAME);		
		put(TEXT, text);
	}
}
