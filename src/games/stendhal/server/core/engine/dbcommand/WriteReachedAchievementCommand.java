package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class WriteReachedAchievementCommand extends AbstractDBCommand {

	private final String identifier;
	private final String title;
	private final String category;
	private final String playerName;
	
	/**
	 * @param identifier
	 * @param playerName
	 */
	public WriteReachedAchievementCommand(String identifier, String title, String category, String playerName) {
		this.identifier = identifier;
		this.title = title;
		this.category = category;
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(identifier, title, category, playerName, transaction);
	}

}
