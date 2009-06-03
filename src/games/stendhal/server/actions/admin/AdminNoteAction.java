package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

public class AdminNoteAction extends AdministrationAction {
	@Override
	protected void perform(final Player player, final RPAction action) {
		if (action.has("target")) {
			String target = action.get("target");
			String adminnote = action.get("note");

			Logger.getLogger(AdminNoteAction.class).info(player.getName() + " has added an adminnote to " + target + " saying: " + adminnote);
			new GameEvent(player.getName(), "adminnote",  target, adminnote).raise();				
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper",
					player.getName() + " has added an adminnote to " + target
					+ " saying: " + adminnote);
		}
	}
	public static void register() {
		CommandCenter.register("adminnote", new AdminNoteAction(), 100);
	}
}
