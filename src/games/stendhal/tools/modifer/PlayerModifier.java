package games.stendhal.tools.modifer;

import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.Transaction;

public class PlayerModifier {

	IDatabase database;

	public void setDatabase(IDatabase database) {
		this.database = database;
	}

	public Player loadPlayer(String characterName) {
		if (database == null) {
			throw new IllegalStateException("no database");
		}
		Transaction transaction = database.getTransaction();

		try {

			RPObject loadCharacter = database.loadCharacter(transaction, characterName, characterName);
			if (loadCharacter != null) {
				return new Player(loadCharacter);
			}
		} catch (SQLException e) {
			//do nothing
		} catch (IOException e) {
			//do nothing
		}
		return null;
	}

	public boolean savePlayer(Player player) {
		if (database == null) {
			throw new IllegalStateException("no database");
		}
		Transaction transaction = database.getTransaction();
		try {
			database.storeCharacter(transaction, player.getName(), player.getName(), player);

		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
