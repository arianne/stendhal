package games.stendhal.tools.playerUpdate;

import games.stendhal.server.core.engine.StendhalPlayerDatabase;
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

    /**
     * Loads a player from database.
     * @return the player
     */
    public void loadAndUpdatePlayers() {
        StendhalPlayerDatabase spdb = (StendhalPlayerDatabase) StendhalPlayerDatabase.getDatabase();
        Iterator<RPObject> i = spdb.iterator();
        while (i.hasNext()) {
            Player player = Player.create(i.next());
            try {
                spdb.storeCharacter(spdb.getTransaction(), player.getName(), player.getName(), player);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        spdb.close();
    }
    
    public static void main(final String[] args) {
        new UpdatePlayerEntities().loadAndUpdatePlayers();
    }

}

 	  	 
