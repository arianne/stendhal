package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.StendhalPlayerDatabase;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.Entity;

import java.sql.SQLException;
import java.util.Map;

import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

/**
 * Manages selling/renting of houses to players
 *
 * @author hendrik
 */
public class CityHouseCouncil extends Entity implements ZoneConfigurator {
	private static Logger logger = Logger.getLogger(CityHouseCouncil.class);
	private StendhalRPZone zone;

	// TODO: This is just a small attempt to understand zone storage. It does not work yet and will require refactoring after completed.
	
	public CityHouseCouncil() {
		super.store();
	}

	public void save() {
		JDBCDatabase database = (JDBCDatabase) DatabaseFactory.getDatabase();
		Transaction trans = database.getTransaction();

		try {
			StendhalPlayerDatabase.getDatabase().storeRPZone(trans, zone);
		} catch (Exception e) {
			try {
				trans.rollback();
			} catch (SQLException e1) {
				logger.error(e1, e1);
			}
			logger.error("Can't store zone", e);
		}		
	}

	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		this.zone = zone;
		super.put("test", "1");
		save();
	}

}
