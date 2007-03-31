package games.stendhal.server.extension;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalServerExtension;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.actions.MoveAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * Stendhal TeleportSend Extenstion
 *
 * This extension adds teleportsend to the game world.
 * there is 1 command:
 *  /teleportsend [Player] [Player|NPC_Destination] which will teleport the first player to the second player/npc
 * This command is an admin command of the same access level as /teleport
 * To enable this extension, add it to the marauroa.int file:
 *
 * # load StendhalServerExtension(s)
 * teleportsend=games.stendhal.server.extension.TeleportSendExtension
 * server_extension=...,teleportsend
 *
 * @author Seather
 */
public class TeleportSendExtension extends StendhalServerExtension implements ActionListener {

	private final String CMD_NAME = "teleportsend";

	private final String CMD_USAGE = "Usage: #/" + CMD_NAME + " #<Player> #<Player|NPC_Destination>";

	private static final Logger logger = Log4J.getLogger(TeleportSendExtension.class);

	public TeleportSendExtension() {
		super();
		logger.info("TeleportSendExtension starting...");
		StendhalRPRuleProcessor.register(CMD_NAME, this);
		AdministrationAction.registerCommandLevel(CMD_NAME, 400);
	}

	@Override
	public void init() {
		// this extension has no spespecific init code, everything is
		// implemented as /commands that are handled onAction
	}

	public void onAction(Player player, RPAction action) {
		String type = action.get("type");

		if (type.equals(CMD_NAME)) {
			onTeleportSend(player, action);
		}
	}

	private void onTeleportSend(Player admin, RPAction action) {
		Log4J.startMethod(logger, "onTeleportSend");
		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(admin, CMD_NAME, true)) {
			return;
		}

		if (action.has("target") && action.has("args")) {
			//Parse Player1
			String name1 = action.get("target");
			Player player1 = StendhalRPRuleProcessor.get().getPlayer(name1);
			if (player1 == null) {
				String text = "Player \"" + name1 + "\" not found";
				admin.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			//Parse Player2 (player/npc)
			String name2 = action.get("args");
			RPEntity player2 = StendhalRPRuleProcessor.get().getPlayer(name2);
			if (player2 == null) {
				player2 = NPCList.get().get(name2);
				if (player2 == null) {

					String text = "Player \"" + name2 + "\" not found";
					admin.sendPrivateText(text);
					logger.debug(text);
					return;
				}
			}

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player2.getID());
			int x = player2.getX();
			int y = player2.getY();

			player1.teleport(zone, x, y, null, admin);
			
			/*StendhalRPRuleProcessor.get().addGameEvent(admin.getName(), "teleportsend", 
					action.get("target") + " -> " + action.get("args"),
			        zone.getID().getID(), Integer.toString(x), Integer.toString(y));*/
		} else {
			admin.sendPrivateText(CMD_USAGE);
		}

		Log4J.finishMethod(logger, "onTeleportSend");
	}

}
