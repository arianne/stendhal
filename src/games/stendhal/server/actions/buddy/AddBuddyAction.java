package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.BUDDYONLINE;
import static games.stendhal.common.constants.Actions.BUDDY_OFFLINE;
import static games.stendhal.common.constants.Actions.TARGET;
import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * Adds someone to your buddy list.
 */
class AddBuddyAction implements ActionListener {

	/**
	 * Adds someone to your buddy list.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (countBuddies(player) > 500) {
			player.sendPrivateText(NotificationType.ERROR, "Sorry, you have already too many buddies");
			return;
		}

		final String who = action.get(TARGET);
		String online = BUDDY_OFFLINE;
		final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(who);
		if ((buddy != null) && !buddy.isGhost()) {
			online = BUDDYONLINE;
		}
		player.setKeyedSlot("!buddy", "_" + who, online);

		player.sendPrivateText(who + " was added to your buddy list.");
		new GameEvent(player.getName(), "buddy", "add", who).raise();

	}

	/**
	 * counts the number of boddies this player has.
	 *
	 * @param player Player
	 * @return number of buddies
	 */
	private int countBuddies(Player player) {
		final RPObject object = KeyedSlotUtil.getKeyedSlotObject(player, "!buddy");
		if (object == null) {
			return 0;
		}
		int res = 0;
		for (String key : object) {
			if (key.startsWith("_")) {
				res++;
			}
		}
		return res;
	}

}
