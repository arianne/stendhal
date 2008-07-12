package games.stendhal.tools.modifer;

import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.Transaction;

public class PlayerModifier {

	IDatabase database;

	public void setDatabase(final IDatabase database) {
		this.database = database;
	}

	public Player loadPlayer(final String characterName) {
		if (database == null) {
			throw new IllegalStateException("no database");
		}
		if (characterName == null) {
			return null;
		}
		final Transaction transaction = database.getTransaction();

		try {

			final RPObject loadCharacter = database.loadCharacter(transaction, characterName, characterName);
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

	public boolean savePlayer(final Player player) {
		if (database == null) {
			throw new IllegalStateException("no database");
		}
		final Transaction transaction = database.getTransaction();
		try {
			database.storeCharacter(transaction, player.getName(), player.getName(), player);

		} catch (final SQLException e) {
			return false;
		} catch (final IOException e) {
			return false;
		}
		return true;
	}

}
