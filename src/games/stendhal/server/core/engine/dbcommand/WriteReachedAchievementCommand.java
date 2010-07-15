package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.events.achievements.Category;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;
/**
 * command to log a reached achievement to the database
 * 
 * @author madmetzger
 */
public class WriteReachedAchievementCommand extends AbstractDBCommand {

	private final Integer id;
	private final String playerName;
	
	/**
	 * create a new command
	 * @param id database id of the achievement
	 * @param playerName name of player who has reached it
	 */
	public WriteReachedAchievementCommand(Integer id, String title, Category category, String playerName) {
		this.id = id;
		this.playerName = playerName;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		AchievementDAO dao = DAORegister.get().get(AchievementDAO.class);
		dao.saveReachedAchievement(id, playerName, transaction);
	}

}
