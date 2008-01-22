package games.stendhal.server.actions.buddy;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

class AddBuddyAction implements ActionListener {

	private static final String _BUDDYONLINE = "1";
	private static final String _BUDDY_OFFLINE = "0";

	public void onAction(Player player, RPAction action) {
		String who = action.get(TARGET);
		String online = _BUDDY_OFFLINE;
		Player buddy = SingletonRepository.getRuleProcessor().getPlayer(who);
		if (buddy != null && !buddy.isGhost()) {
			online = _BUDDYONLINE;
		}
		player.setKeyedSlot("!buddy", "_" + who, online);

		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "buddy",
				"add", who);

	}

}
