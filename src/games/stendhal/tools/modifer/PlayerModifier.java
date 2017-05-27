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
package games.stendhal.tools.modifer;

import java.io.IOException;
import java.sql.SQLException;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

public class PlayerModifier {

	public Player loadPlayer(final DBTransaction transaction, final String characterName) {
		if (characterName == null) {
			return null;
		}

		try {
			final RPObject loadCharacter = DAORegister.get().get(CharacterDAO.class).loadCharacter(transaction, characterName, characterName);
			if (loadCharacter != null) {
				return new Player(loadCharacter);
			}
		} catch (final SQLException e) {
			return null;
		} catch (final IOException e) {
			return null;
		}
		return null;
	}

	public boolean savePlayer(final DBTransaction transaction, final Player player) {
		try {
			DAORegister.get().get(CharacterDAO.class).storeCharacter(transaction, player.getName(), player.getName(), player);

		} catch (final SQLException e) {
			return false;
		} catch (final IOException e) {
			return false;
		}
		return true;
	}

}
