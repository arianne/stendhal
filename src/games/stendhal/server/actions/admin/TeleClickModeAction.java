package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.TELECLICKMODE;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;


public class TeleClickModeAction extends AdministrationAction {


	public static void register() {
		CommandCenter.register(TELECLICKMODE, new TeleClickModeAction(), 500);

	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (player.isTeleclickEnabled()) {
			player.setTeleclickEnabled(false);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					TELECLICKMODE, "off");
		} else {
			player.setTeleclickEnabled(true);
			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					TELECLICKMODE, "on");
		}
	}

}
