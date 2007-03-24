

/** Stendhal TeleportMove Extenstion
 *  @author Seather
 *  Toggle-able Mode which teleports the admin to the location they double click.
*/

package games.stendhal.server.extension;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalServerExtension;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.actions.MoveAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * @author Seather
 * This extension adds teleclickmode to the game world.
 * there are 2 commands:
 *  /teleclickmode which will toggle the ability to teleport to the location you double click
 * This command is an admin command of the same access level as /ghostmode
 *  [Dobule Click] which will teleport the player to the location they double click if the toggle is set
 * The toggle is set to off by default, and each admin has their own toggle.
  * Reminder: Double click also works on the minimap
 * To enable this extension, add it to the marauroa.int file:

  # load StendhalServerExtension(s)
  groovy=games.stendhal.server.scripting.StendhalGroovyRunner
  http=games.stendhal.server.StendhalHttpServer
  telemove=games.stendhal.server.extension.TeleportMoveExtension
  server_extension=groovy,http,telemove

 */

public class TeleportMoveExtension extends StendhalServerExtension {

	private final String TELE_CLICK_CMD_NAME = "teleclickmode";
	private final int TELE_CLICK_CMD_LVL = 500;
	private final String QUEST = "tele_move_mode";
	private static final Logger logger = Log4J.getLogger(TeleportMoveExtension.class);
	private static final MoveAction move = new MoveAction();
	
	/**
	 * 
	 */
	public TeleportMoveExtension() {
		super();
		logger.info("TeleportMoveExtension starting...");
		StendhalRPRuleProcessor.register(TELE_CLICK_CMD_NAME, this);
		StendhalRPRuleProcessor.register("moveto", this);
		AdministrationAction.registerCommandLevel(TELE_CLICK_CMD_NAME, TELE_CLICK_CMD_LVL);
	}

	/* 
	 * @see games.stendhal.server.StendhalServerExtension#init()
	 */
	@Override
	public void init() {
		// this extension has no spespecific init code, everything is
		// implemented as /commands that are handled onAction
	}
	
	@Override
	public void onAction(Player player, RPAction action) {
		String type = action.get("type");
		
		if (type.equals(TELE_CLICK_CMD_NAME)) {
			onTeleModeToggle(player, action);
		} else if (type.equals("moveto")) {
			onTeleMoveTo(player, action);
		}
	
	}
	
	private void onTeleModeToggle(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleModeToggle");
		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
				TELE_CLICK_CMD_NAME, true)) {
			return;
		}
		
		//Toggle mode
		boolean mode = false;
		if (!player.hasQuest(QUEST)) {
			player.setQuest(QUEST,"on");
			mode = true;
		} else if ((player.getQuest(QUEST)).equals("on")) {
			player.setQuest(QUEST,"off");
			mode = false;
		} else {
			player.setQuest(QUEST,"on");
			mode = true;
		}
		
		//Inform admin of new mode state by private message
		String text = TELE_CLICK_CMD_NAME + " state set to ";
		if (mode) {
			text += "ON";
		} else {
			text += "OFF";
		}
		player.sendPrivateText(text);
		
		Log4J.finishMethod(logger, "onTeleModeToggle");
	}
	
	
	
	
	private void onTeleMoveTo(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleMoveTo");
		//even with verbose false isPlayerAllowedToExecuteAdminCommand will tell non-admins
		//that they lack access, and the cmd lvls can't be accessed through any other means
		if (!(player.getAdminLevel() >= TELE_CLICK_CMD_LVL)
				|| !player.hasQuest(QUEST)
				|| !(player.getQuest(QUEST)).equals("on")) {
			move.onAction(player,action);
			return;
		}
		
		//Might not need this
		if (player.hasPath()) {
			player.clearPath();
		}
		
		//Teleport
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(
					player.getID());
		int x = action.getInt("x");
		int y = action.getInt("y");
		player.teleport(zone, x, y, null, null);
		
		Log4J.finishMethod(logger, "onTeleMoveTo");
	}
	
}
