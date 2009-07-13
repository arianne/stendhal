package games.stendhal.tools.modifer;

import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;

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
