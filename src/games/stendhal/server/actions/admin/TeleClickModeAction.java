package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.TELECLICKMODE;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
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
			new GameEvent(player.getName(), TELECLICKMODE, "off").raise();
		} else {
			player.setTeleclickEnabled(true);
			new GameEvent(player.getName(), TELECLICKMODE, "on").raise();
		}
	}

}
