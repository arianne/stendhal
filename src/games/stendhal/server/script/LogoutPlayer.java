
package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.server.game.PlayerEntryContainer;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Logout a player
 *
 * @author hendrik
 */
public class LogoutPlayer extends ScriptImpl {

	private static Logger logger = Logger.getLogger(LogoutPlayer.class);

	@Override
	public void execute(Player admin, List<String> args) {

		// help text
		if (args.size() == 0) {
			admin.sendPrivateText("/script LogoutPlayer.class <playername> logs a player out");
			return;
		}
		
		try {
			//see processLogoutEvent in marauroa-1.34/src/marauroa/server/game/GameServerManager.java
			
			PlayerEntryContainer playerContainer = PlayerEntryContainer.getContainer();
			
			int clientid = playerContainer.getClientidPlayer(args.get(0));
			if (clientid == -1) {
				admin.sendPrivateText(args.get(0) + " not found");
				return;
			}
			
			Player player = StendhalRPRuleProcessor.get().getPlayer(args.get(0));
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
			RPObject.ID id = playerContainer.getRPObjectID(clientid);
			RPObject object = zone.get(id);
			
			//player.sendPrivateText("You have been logged out by an admin");
			
			// remove player from Stendhal
			if(StendhalRPRuleProcessor.get().onExit(id)) {
				// NOTE: Set the Object so that it is stored in Database 
				playerContainer.setRPObject(clientid, object);
			}

			// remove the player from Narauroa's player container
			playerContainer.removeRuntimePlayer(clientid);
			
			admin.sendPrivateText(args.get(0) + " has been logged out");
			
		} catch (Exception e) {
			logger.error(e, e);
		}

	}

}
