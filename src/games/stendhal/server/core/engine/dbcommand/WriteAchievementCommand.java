package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.events.achievements.Achievement;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

public class WriteAchievementCommand extends AbstractDBCommand {
	
	private final Achievement achievement;
	
	private Integer savedId;
	
	/**
	 * @param achievement
	 */
	/**
	 * @param achievement
	 */
	public WriteAchievementCommand(Achievement achievement) {
		super();
		this.achievement = achievement;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		savedId = DAORegister.get().get(AchievementDAO.class).saveAchievement(achievement.getIdentifier(), achievement.getTitle(), achievement.getCategory(), transaction);
	}
	
	/**
	 * @return the savedId
	 */
	public Integer getSavedId() {
		return savedId;
	}

}
