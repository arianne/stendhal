package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class ReadAchievementsForPlayerCommand extends AbstractDBCommand {
	
	private Set<String> identifiers = new HashSet<String>();
	private final String playerName;

	/**
	 * @param identifiers
	 */
	public ReadAchievementsForPlayerCommand(String playerName) {
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		identifiers = DAORegister.get().get(AchievementDAO.class).loadAllReachedAchievementsOfPlayer(playerName, transaction);
	}

	public Set<String> getIdentifiers() {
		return identifiers;
	}

}
