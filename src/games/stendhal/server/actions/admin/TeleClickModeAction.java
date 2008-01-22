package games.stendhal.server.actions.admin;

import marauroa.common.game.RPAction;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

public class TeleClickModeAction extends AdministrationAction {

	private static final String _TELECLICKMODE = "teleclickmode";

	public static void register() {
		CommandCenter.register(_TELECLICKMODE, new TeleClickModeAction(), 500);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (player.isTeleclickEnabled()) {
			player.setTeleclickEnabled(false);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_TELECLICKMODE, "off");
		} else {
			player.setTeleclickEnabled(true);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_TELECLICKMODE, "on");
		}
	}

}
