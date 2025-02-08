/* $Id$ */
/***************************************************************************
 *                 Copyright © 2007-2024 - Faiumoni e. V.                  *
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

import java.util.List;

import games.stendhal.server.core.scripting.impl.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * Tries to add an RPEvent.
 *
 * @author hendrik
 */
public class EventTest extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() < 1) {
			admin.sendPrivateText("Usage: /script EventTest.class {some-text}");
			return;
		}

		final RPClass rpclass = new RPClass("testevent");
		rpclass.add(DefinitionClass.RPEVENT, "testevent", Definition.STANDARD);

		final RPEvent event = new RPEvent("testevent");
		event.put("arg", args.get(0));
		admin.addEvent(event);
		admin.notifyWorldAboutChanges();
	}
}
