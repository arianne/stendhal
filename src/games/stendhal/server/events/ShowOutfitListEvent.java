/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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

import static games.stendhal.common.constants.Events.OUTFIT_LIST;

import org.apache.log4j.Logger;

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.SyntaxException;


public class ShowOutfitListEvent extends RPEvent {

	private static final Logger logger = Logger.getLogger(ShowOutfitListEvent.class);

	private static final String TITLE = "title";
	private static final String CAPTION = "caption";
	private static final String OUTFITS = "outfits";


	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		try {
			final RPClass rpclass = new RPClass(OUTFIT_LIST);
			rpclass.addAttribute(TITLE, Type.STRING, Definition.PRIVATE);
			rpclass.addAttribute(CAPTION, Type.STRING, Definition.PRIVATE);
			rpclass.addAttribute(OUTFITS, Type.LONG_STRING, Definition.PRIVATE);
			rpclass.addAttribute("show_base", Type.FLAG, Definition.PRIVATE);
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public ShowOutfitListEvent(final String title, final String caption, final String outfits) {
		super(OUTFIT_LIST);

		put(TITLE, title);
		if (caption != null) {
			put(CAPTION, caption);
		}
		put(OUTFITS, outfits);
	}
}
