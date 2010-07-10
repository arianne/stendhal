package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class WriteReachedAchievementCommand extends AbstractDBCommand {

	private final String title;
	private final String category;
	private final String playerName;
	
	/**
	 * @param title
	 * @param category
	 * @param playerName
	 */
	public WriteReachedAchievementCommand(String title, String category, String playerName) {
		this.title = title;
		this.category = category;
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(title, category, playerName, transaction);
	}

}
