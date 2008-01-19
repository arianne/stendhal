//* $Id$ */

/** StendhalSpouse Extension is copyright of Jo Seiler, 2006
 *  @author intensifly
 *  Adds wedding functionality to Stendhal
 */

package games.stendhal.server.extension;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;
import marauroa.common.game.RPAction;

/**
 * This extension adds marriage to the game world. there are 2 commands:
 * <li>/marry &lt;Player1&gt; &lt;Player2&gt;
 * <p>
 * which will create a bond between those players This command is an admin
 * command of the same access level as /jail ;)
 * <li>/spouse which will teleport a married player to his spouse
 * <p>
 * To enable this extension, add it to the marauroa.int file:
 * <p>
 * <tt>#</tt> load StendhalServerExtension(s)
 * <p>
 * groovy=games.stendhal.server.scripting.StendhalGroovyRunner <p>
 * http=games.stendhal.server.StendhalHttpServer <p>
 * spouse=games.stendhal.server.extension.SpouseExtension <p>
 * server_extension=groovy,http,spouse <p>
 * 
 * @author intensifly
 */
public class SpouseExtension extends StendhalServerExtension implements
		ActionListener {

	private final String SPOUSE = "spouse";

	private static final Logger logger = Logger.getLogger(SpouseExtension.class);

	/**
	 * 
	 */
	public SpouseExtension() {
		super();
		logger.info("SpouseExtension starting...");
		CommandCenter.register("marry", this, 400);
		CommandCenter.register("spouse", this);
	}

	/*
	 * @see games.stendhal.server.StendhalServerExtension#init()
	 */
	@Override
	public void init() {
		// this extension has no specific init code, everything is
		// implemented as /commands that are handled onAction
	}

	public void onAction(Player player, RPAction action) {
		String type = action.get("type");

		if (type.equals("marry")) {
			onMarry(player, action);
		} else if (type.equals("spouse")) {
			onSpouse(player, action);
		}
	}

	private void onMarry(Player player, RPAction action) {
		String usage = "Usage: #/marry #<player1> #<player2>";
		String text = "";

		Player player1 = null;
		String name1 = null;
		Player player2 = null;
		String name2 = null;
		boolean canMarry = true;

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
				"marry", true)) {
			return;
		}

		if (action.has("target")) {
			name1 = action.get("target");
			player1 = StendhalRPRuleProcessor.get().getPlayer(name1);
			if (player1 == null) {
				canMarry = false;
				text += "Player \"" + name1 + "\" not found. ";
				canMarry = false;
			}
		} else {
			canMarry = false;
			text = usage;
		}

		if (action.has("args")) {
			name2 = action.get("args");
			player2 = StendhalRPRuleProcessor.get().getPlayer(name2);
			if (player2 == null) {
				canMarry = false;
				text += "Player \"" + name2 + "\" not found. ";
				canMarry = false;
			}
		} else {
			canMarry = false;
			text = usage;
		}

		if (canMarry) {
			if (name1.equals(name2)) {
				text += "Players cannot marry themselves!";
				canMarry = false;
			}
		}

		if (canMarry) {
			if (player1.hasQuest(SPOUSE)) {
				text += name1 + " is already married to "
						+ player1.getQuest(SPOUSE) + ". ";
				canMarry = false;
			}
			if (player2.hasQuest(SPOUSE)) {
				text += name2 + " is already married to "
						+ player2.getQuest(SPOUSE) + ". ";
				canMarry = false;
			}
		}

		if (canMarry) {
			player1.setQuest(SPOUSE, name2);
			player1.sendPrivateText("Congratulations! You are now married to \""
					+ name2
					+ "\". You can use #/spouse if you want to be together.");
			player2.setQuest(SPOUSE, name1);
			player2.sendPrivateText("Congratulations! You are now married to \""
					+ name1
					+ "\". You can use #/spouse if you want to be together.");
			text = "You have successfully married \"" + name1 + "\" and \""
					+ name2 + "\".";
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"marry", name1 + " + " + name2);
		}

		player.sendPrivateText(text.trim());
	}

	private void onSpouse(Player player, RPAction action) {
		if (player.hasQuest(SPOUSE)) {
			Player teleported = null;

			String name = player.getQuest(SPOUSE);
			teleported = StendhalRPRuleProcessor.get().getPlayer(name);

			if (teleported == null) {
				String text = "Your spouse (\"" + name + "\") is not online.";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			StendhalRPZone zone = teleported.getZone();
			int x = teleported.getX();
			int y = teleported.getY();

			// TODO: use Player.teleport()

			if (StendhalRPAction.placeat(zone, player, x, y)) {
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"teleportto", teleported.getName() + "(spouse)");
			}

			player.notifyWorldAboutChanges();
		}
	}

}
