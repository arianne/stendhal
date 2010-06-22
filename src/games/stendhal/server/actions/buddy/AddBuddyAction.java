package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.BUDDYONLINE;
import static games.stendhal.common.constants.Actions.BUDDY_OFFLINE;
import static games.stendhal.common.constants.Actions.TARGET;

import java.util.List;

import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.CheckCharacterExistsCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;


/**
 * Adds someone to your buddy list.
 */
class AddBuddyAction implements ActionListener, TurnListener {
	
	private ResultHandle handle = new ResultHandle();
	
	
	/**
	 * Starts to Handle a Buddy action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (countBuddies(player) > 500) {
			player.sendPrivateText(NotificationType.ERROR, "Sorry, you have already too many buddies");
			return;
		}
		
		final String who = action.get(TARGET);
		
		DBCommand command = new CheckCharacterExistsCommand(player, who);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(0, this);
	}
	
	/**
	 * Completes handling the buddy action.
	 * 
	 * @param currentTurn.
	 */
	public void onTurnReached(int currentTurn) {
		List<CheckCharacterExistsCommand> list = DBCommandQueue.get().getResults(CheckCharacterExistsCommand.class, handle);
		
		if (list.isEmpty()) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}

		// update the sign
		CheckCharacterExistsCommand checkcommand = list.get(0);
		boolean characterExists = checkcommand.exists();
		Player player = checkcommand.getPlayer();
		String who = checkcommand.getWho();
		
		if(!characterExists) {
				player.sendPrivateText(NotificationType.ERROR, "Sorry, " + who + " could not be found.");
				return;
		} 
		
		String online = BUDDY_OFFLINE;
		final Player buddy = SingletonRepository.getRuleProcessor().getPlayer(who);
		if ((buddy != null) && !buddy.isGhost()) {
			online = BUDDYONLINE;
		}
		player.addBuddy(who, (buddy != null) && !buddy.isGhost());

		player.sendPrivateText(who + " was added to your buddy list.");
		new GameEvent(player.getName(), "buddy", "add", who).raise();
	}

	/**
	 * counts the number of buddies this player has.
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
