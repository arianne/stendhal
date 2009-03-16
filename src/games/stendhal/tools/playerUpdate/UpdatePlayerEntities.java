package games.stendhal.tools.playerUpdate;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import marauroa.common.game.RPObject;

/**
 * Loads all Players from the database, performs update operations and saves afterwards 
 * @author madmetzger
 */
public class UpdatePlayerEntities {

    private StendhalPlayerDatabase spdb;

    public UpdatePlayerEntities() {
    	this.spdb = (StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase();
    	MockStendlRPWorld.get();
    }
    
	private void loadAndUpdatePlayers() {
    	Iterator<RPObject> i = spdb.iterator();
    	while(i.hasNext()) {
    		try {
	    		RPObject next = i.next();
	    		Player p = createPlayerFromRPO(next);
	    		updateAndSavePlayer(p);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

	public Player createPlayerFromRPO(RPObject next) {
		Player p = Player.create(next);
		return p;
	}

	public void updateAndSavePlayer(Player p)
			throws SQLException, IOException {
		spdb.storeCharacter(spdb.getTransaction(),p.getName(),p.getName(),p);
	}
    
    public void doUpdate() {
		this.loadAndUpdatePlayers();
	}
    
	public static void main(String[] args) {
        new UpdatePlayerEntities().doUpdate();
    }

}

 	  	 
