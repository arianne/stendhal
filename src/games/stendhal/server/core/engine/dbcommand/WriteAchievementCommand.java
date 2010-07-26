package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.events.achievements.Achievement;

import java.io.IOException;
import java.sql.SQLException;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;
/**
 * Writes an Achievement assynchronously to the database
 *  
 * @author madmetzger
 */
public class WriteAchievementCommand extends AbstractDBCommand {
	
	private final Achievement achievement;
	
	private Integer savedId;
	
	/**
	 * Create a new WriteAchievementCommand
	 * 
	 * @param achievement
	 */
	public WriteAchievementCommand(Achievement achievement) {
		super();
		this.achievement = achievement;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		savedId = DAORegister.get().get(AchievementDAO.class).saveAchievement(achievement.getIdentifier(), achievement.getTitle(),
																			  achievement.getCategory(), achievement.getBaseScore(),
																			  transaction);
	}
	
	/**
	 * @return the id the Achievement got in the database
	 */
	public Integer getSavedId() {
		return savedId;
	}

}
