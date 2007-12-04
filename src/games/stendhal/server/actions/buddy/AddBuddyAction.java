package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;

class AddBuddyAction implements ActionListener {

	public void onAction(Player player, RPAction action) {
		String who = action.get("target");
		String online = "0";
		Player buddy = StendhalRPRuleProcessor.get().getPlayer(who);
		if (buddy != null && !buddy.isGhost()) {
			online = "1";
		}
		player.setKeyedSlot("!buddy", "_" + who, online);

		StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "buddy",
				"add", who);

	}

}
