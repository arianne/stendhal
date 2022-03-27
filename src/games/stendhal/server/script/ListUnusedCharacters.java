/***************************************************************************
 *                   (C) Copyright 2022-2022 - Stendhal                    *
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.KeyedSlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

public class ListUnusedCharacters extends ScriptImpl {

	public static class ListUnusedCharactersCommand extends AbstractDBCommand {
		private Player admin;

		public ListUnusedCharactersCommand(Player admin) {
			this.admin = admin;
		}

		@Override
		public void execute(DBTransaction transaction) throws SQLException, IOException {
			transaction.execute("CREATE TABLE IF NOT EXISTS temp_delete(charname VARCHAR(64), username VARCHAR(64), player_id INT);", null);
			transaction.execute("TRUNCATE temp_delete", null);
			transaction.execute("INSERT INTO temp_delete(charname, username, player_id) "
					+ " SELECT characters.charname, account.username, characters.player_id FROM character_stats, characters, account"
					+ " WHERE age<5 AND level=0 AND character_stats.name=characters.charname AND characters.player_id=account.id"
					+ " AND characters.status='active' AND account.status='active'"
					+ " AND character_stats.lastseen < date_sub(CURRENT_DATE, INTERVAL 1 MONTH)", null);
			transaction.execute("DELETE FROM temp_delete WHERE EXISTS (SELECT null FROM buddy WHERE temp_delete.charname=buddy.buddy)", null);

			CharacterDAO characterDao = DAORegister.get().get(CharacterDAO.class);
			ResultSet resultSet = transaction.query("SELECT charname, username FROM temp_delete", null);
			int i = 0;
			while (resultSet.next()) {
				if (i % 1000 == 0) {
					admin.sendPrivateText("ListUnsuedCharacters processed " + i);
				}
				i++;

				String charname = resultSet.getString("charname");
				String username = resultSet.getString("username");
				RPObject character = characterDao.loadCharacter(transaction, username, charname);

				RPObject visited = KeyedSlotUtil.getKeyedSlotObject(character, "!visited");
				if (visited.size() > 2) {
					resultSet.deleteRow();
					continue;
				}

				if (hasNonInitialItems(character)) {
					resultSet.deleteRow();
					continue;
				}
			}
			admin.sendPrivateText("ListUnsuedCharacters completed, see database table temp_delete");
		}

		private boolean hasNonInitialItems(RPObject character) {
			for (final RPSlot slot : character.slots()) {
				if (slot instanceof KeyedSlot) {
					continue;
				}

				if (slot.isEmpty()) {
					continue;
				}

				for (RPObject object : slot) {
					String name = object.get("name");
					if (name == null) {
						continue;
					}
					if (!name.equals("club") && !name.equals("leather armor") && !name.equals("leather_armor")) {
						return true;
					}
				}
			}
			return false;
		}

	}


	@Override
	public void execute(Player admin, List<String> args) {
		if (admin.getAdminLevel() < 5000) {
			admin.sendPrivateText("Higher adminlevel required.");
			return;
		}
		DBCommandQueue.get().enqueue(new ListUnusedCharactersCommand(admin));
	}

}
