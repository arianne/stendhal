package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.BUDDYONLINE;
import static games.stendhal.common.constants.Actions.BUDDY_OFFLINE;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class AddBuddyAction implements ActionListener {



	public void onAction(final Player player, final RPAction action) {
		final String who = action.get(TARGET);
		String online = BUDDY_OFFLINE;
		final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(who);
		if ((buddy != null) && !buddy.isGhost()) {
			online = BUDDYONLINE;
		}
		player.setKeyedSlot("!buddy", "_" + who, online);

		SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), "buddy",
				"add", who);

	}

}
