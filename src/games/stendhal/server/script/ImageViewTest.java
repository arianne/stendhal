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
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.RPEvent;

/**
 * A simple test for the client ImageViewer.
 * 
 * @author timothyb89
 */
public class ImageViewTest extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {

		final RPEvent event = new RPEvent("examine");
		event.put("path", "/data/sprites/examine/map-semos-city.png");
		event.put("alt", "Map of Semos City");
		event.put("title", "Semos City");
		event.put(
				"text",
				"Semos City is your starting point and you will return here often during your journey through the world.<br>"
						+ "1&nbsp;Townhall,&nbsp;Tad&nbsp;lives&nbsp;here, "
						+ "2&nbsp;Library, 3&nbsp;Bank, 4&nbsp;Storage, 5&nbsp;Bakery, "
						+ "6&nbsp;Blacksmith,&nbsp;Carmen, 7&nbsp;Inn,&nbsp;Margaret, "
						+ "8&nbsp;Temple,&nbsp;ilisa, 9&nbsp;Dangerous&nbsp;Dungeon<br>"
						+ "A&nbsp;Semos&nbsp;Village, B&nbsp;Northern Plains&nbsp;and&nbsp;Mine, "
						+ "C&nbsp;Very&nbsp;long&nbsp;path&nbsp;to&nbsp;Ados, "
						+ "D&nbsp;Southern&nbsp;Plains&nbsp;and&nbsp;Nalwor&nbsp;Forest");
		admin.addEvent(event);
	}
}
