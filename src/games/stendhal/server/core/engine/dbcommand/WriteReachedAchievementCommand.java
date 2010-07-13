package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.events.achievements.Category;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class WriteReachedAchievementCommand extends AbstractDBCommand {

	private final Integer id;
	private final String title;
	private final Category category;
	private final String playerName;
	
	/**
	 * @param identifier
	 * @param playerName
	 */
	public WriteReachedAchievementCommand(Integer id, String title, Category category, String playerName) {
		this.id = id;
		this.title = title;
		this.category = category;
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(id, title, category, playerName, transaction);
	}

}
