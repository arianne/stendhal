package games.stendhal.tools.playerUpdate;

import games.stendhal.server.core.engine.RPClassGenerator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import marauroa.common.game.RPObject;

/**
 * Loads all Players from the database, performs update operations and saves afterwards. 
 * @author madmetzger
 */
public class UpdatePlayerEntities {

    private final StendhalPlayerDatabase spdb;

    UpdatePlayerEntities() {
    	this.spdb = (StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase();
    }

	public void initRPClasses() {
		new RPClassGenerator().createRPClasses();
	}
    
	private void loadAndUpdatePlayers() {
    	final Iterator<RPObject> i = spdb.iterator();
    	while (i.hasNext()) {
    		try {
	    		final RPObject next = i.next();
	    		final Player p = createPlayerFromRPO(next);
	    		savePlayer(p);
			} catch (final SQLException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
    	}
    }

	Player createPlayerFromRPO(final RPObject next) {
		final Player p = (Player) new PlayerTransformer().transform(next);
		return p;
	}

	void savePlayer(final Player p)
			throws SQLException, IOException {
		spdb.storeCharacter(spdb.getTransaction(), p.getName(), p.getName(), p);
	}
    
    private void doUpdate() {
		this.loadAndUpdatePlayers();
	}
    
	public static void main(final String[] args) {
        UpdatePlayerEntities updatePlayerEntities = new UpdatePlayerEntities();
        updatePlayerEntities.initRPClasses();
		updatePlayerEntities.doUpdate();
    }

}

 	  	 
