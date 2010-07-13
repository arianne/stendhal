package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.core.engine.db.AchievementDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;
/**
 * reads all achievement identifiers and the corresponding ids from the database
 * 
 * @author madmetzger
 */
public class ReadAchievementIdentifierToIdMap extends AbstractDBCommand {

	private Map<String, Integer> identifierToIdMap;

	@Override
	public void execute(DBTransaction transaction) throws SQLException,
			IOException {
		setIdentifierToIdMap(DAORegister.get().get(AchievementDAO.class).loadIdentifierIdPairs(transaction));
	}

	private void setIdentifierToIdMap(Map<String, Integer> identifierToIdMap) {
		this.identifierToIdMap = identifierToIdMap;
	}

	/**
	 * @return map from identifier for achievement to its database id
	 */
	public Map<String, Integer> getIdentifierToIdMap() {
		return identifierToIdMap;
	}

}
