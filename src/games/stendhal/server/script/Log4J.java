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

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
/**
 * Script to change log level of certain loggers at runtime
 *
 * Parameters:
 * 1. full qualified class name where the logger is used
 * 2. log level as defined by log4j
 *    - INFO
 *    - DEBUG
 *    - WARN
 *    - ERROR
 *    - FATAL
 *    - OFF
 *    - TRACE
 *    - ALL
 *
 * For log level names see also the log4j documentation
 *
 * @author madmetzger
 */
public class Log4J extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		if(args.size() != 2) {
			admin.sendPrivateText("Needed parameters: [full qualified class name] [log level]");
			return;
		}
		Level levelToSet = Level.toLevel(args.get(1), Level.INFO);
		try {
			Class<?> clazz = Class.forName(args.get(0));
			Logger.getLogger(clazz).setLevel(levelToSet);
		} catch (ClassNotFoundException e) {
			admin.sendPrivateText("The class '"+args.get(0)+"' could not be found.");
		}
	}

}
