package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.*;

public class AdminLevelAction extends AdministrationAction {

	private static final String _ADMINLEVEL = "adminlevel";
	private static final String _NEWLEVEL = "newlevel";
	private static final String _TARGET = TARGET;

	public static void register() {
		CommandCenter.register(_ADMINLEVEL, new AdminLevelAction(), 0);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (action.has(_TARGET)) {

			String name = action.get(_TARGET);
			Player target = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (target == null || (target.isGhost() && !isAllowedtoSeeGhosts(player))) {
				logger.debug("Player \"" + name + "\" not found");
				player.sendPrivateText("Player \"" + name + "\" not found");
				return;
			}

			int oldlevel = target.getAdminLevel();
			String response = target.getTitle() + " has adminlevel " + oldlevel;

			if (action.has(_NEWLEVEL)) {
				// verify newlevel is a number
				int newlevel;
				try {
					newlevel = Integer.parseInt(action.get(_NEWLEVEL));
				} catch (NumberFormatException e) {
					player.sendPrivateText("The new adminlevel needs to be an Integer");

					return;
				}

				// If level is beyond max level, just set it to max.
				if (newlevel > REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
					newlevel = REQUIRED_ADMIN_LEVEL_FOR_SUPER;
				}

				int mylevel = player.getAdminLevel();
				if (mylevel < REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
					response = "Sorry, but you need an adminlevel of "
							+ REQUIRED_ADMIN_LEVEL_FOR_SUPER
							+ " to change adminlevel.";
				} else {

					// OK, do the change
					SingletonRepository.getRuleProcessor().addGameEvent(
							player.getName(), _ADMINLEVEL, target.getName(),
							_ADMINLEVEL, action.get(_NEWLEVEL));
					target.setAdminLevel(newlevel);
					target.update();
					target.notifyWorldAboutChanges();

					response = "Changed adminlevel of " + target.getTitle()
							+ " from " + oldlevel + " to " + newlevel + ".";
					target.sendPrivateText(player.getTitle()
							+ " changed your adminlevel from " + +oldlevel
							+ " to " + newlevel + ".");
				}
			}

			player.sendPrivateText(response);
		}
	}

	boolean isAllowedtoSeeGhosts(Player player) {
		return AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "ghostmode", false);
	}

}
