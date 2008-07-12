package games.stendhal.server.actions.admin;

import static games.stendhal.server.actions.WellKnownActionConstants.MINUTES;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class JailAction extends AdministrationAction {

	private static final String USAGE_JAIL_NAME_MINUTES_REASON = "Usage: /jail name minutes reason";
	private static final String _JAIL = "jail";

	public static void register() {
		CommandCenter.register(_JAIL, new JailAction(), 400);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (action.has(TARGET) && action.has(MINUTES)) {
			final String target = action.get(TARGET);
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				final int minutes = action.getInt(MINUTES);
				SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
						_JAIL, target, Integer.toString(minutes), reason);
				SingletonRepository.getJail().imprison(target, player, minutes, reason);
			} catch (final NumberFormatException e) {
				player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
			}
		} else {
			player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
		}

	}

}
