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
package games.stendhal.server.script;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.RPObjectDAO;


/**
 * Loads an rpobject from the database and dumps it to the logfile
 *
 * @author hendrik
 */
public class DumpCharacterFromDatabase extends ScriptImpl {
	private static Logger logger = Logger.getLogger(DumpCharacterFromDatabase.class);

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("Usage: /script DumpCharacterFromDatabase <rpobjectid>\nSELECT object_id FROM characters WHERE charname='xxx'");
			return;
		}

		try {
			RPObject object = DAORegister.get().get(RPObjectDAO.class).loadRPObject(Integer.parseInt(args.get(0)), false);
			logger.info("loaded character: " + object);
		} catch (SQLException e) {
			logger.error(e, e);
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

}
