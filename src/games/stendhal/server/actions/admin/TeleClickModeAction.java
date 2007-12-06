package games.stendhal.server.actions.admin;

import marauroa.common.game.RPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;

public class TeleClickModeAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register("teleclickmode", new TeleClickModeAction(), 500);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (player.isTeleclickEnabled()) {
			player.setTeleclickEnabled(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleclickmode", "off");
		} else {
			player.setTeleclickEnabled(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleclickmode", "on");
		}
	}

}
