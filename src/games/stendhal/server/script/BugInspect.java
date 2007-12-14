package games.stendhal.server.script;

import games.stendhal.server.Jail;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Deep inspects a player and all his/her items
 * 
 * @author hendrik
 */
public class BugInspect extends ScriptImpl implements TurnListener {
	private static Logger logger = Logger.getLogger(BugInspect.class);
	private HashSet<String> seen = new HashSet<String>();
	private boolean keepRunning = true;

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		TurnNotifier.get().notifyInTurns(6, this);
		keepRunning = true;
		seen.clear();
	}

	public void onTurnReached(int currentTurn) {

		for (Player player : StendhalRPRuleProcessor.get().getPlayers()) {
			if (seen.contains(player.getName())) {
				continue;
			}
			seen.add(player.getName());

			StringBuffer sb = new StringBuffer();
			sb.append("Inspecting " + player.getName() + "\n");
			boolean caught = false;
			boolean warn = false;

			// inspect slots
			for (RPSlot slot : player.slots()) {
				if (slot.getName().equals("!buddy")
						|| slot.getName().equals("!ignore")
						|| slot.getName().equals("!kills")
						|| slot.getName().equals("!quests")) {
					continue;
				}
				sb.append("\nSlot " + slot.getName() + ": \n");

				// list objects
				for (RPObject object : slot) {
					if (object instanceof StackableItem) {
						StackableItem item = (StackableItem) object;
						if (!item.getName().equals("money")
								&& item.getQuantity() > 10000) {
							caught = true;
						}
						if (item.getName().equals("money")
								&& item.getQuantity() > 10000000) {
							caught = true;
						}
						if (!item.getName().equals("money")
								&& item.getQuantity() > 1000) {
							warn = true;
						}
						if (item.getName().equals("money")
								&& item.getQuantity() > 100000) {
							warn = true;
						}
					}
					sb.append("   " + object + "\n");
				}
			}

			String message = player.getName() + " has a large amount of items";
			if (caught) {

				StendhalRPRuleProcessor.get().addGameEvent("bug inspect",
						"jail", player.getName(), Integer.toString(-1),
						"possible bug abuse");
				Jail.get().imprison(player.getName(), player, -1,
						"possible bug abuse");
				player.sendPrivateText("Please use /support to talk to an admin about your large amount of items which may have been the result of a bug.");
				player.notifyWorldAboutChanges();

				message = "auto jailed " + player.getName()
						+ " because of a large number of items";
			}

			if (warn || caught) {

				StendhalRPRuleProcessor.get().addGameEvent("bug inspect",
						"support", message);
				String completeMessage = "bug_inspect asks for support to ADMIN: "
						+ message;
				for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
					if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
						p.sendPrivateText(completeMessage);
						p.notifyWorldAboutChanges();
					}
				}
				logger.warn("User with large amout of items: " + message
						+ "\r\n" + sb.toString());
			}
		}

		if (keepRunning) {
			TurnNotifier.get().notifyInTurns(6, this);
		}
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		keepRunning = false;
	}

}
