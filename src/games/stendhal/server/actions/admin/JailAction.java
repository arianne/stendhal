package games.stendhal.server.actions.admin;

import games.stendhal.server.Jail;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.WellKnownActionConstants;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.MINUTES;

public class JailAction extends AdministrationAction {
	
	private static final String USAGE_JAIL_NAME_MINUTES_REASON = "Usage: /jail name minutes reason";
	private static final String _JAIL = "jail";

	public static void register() {
		CommandCenter.register(_JAIL, new JailAction(), 400);

	}

	@Override
	public void perform(Player player, RPAction action) {

		if (action.has(TARGET) && action.has(MINUTES)) {
			String target = action.get(TARGET);
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				int minutes = action.getInt(MINUTES);
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						_JAIL, target, Integer.toString(minutes), reason);
				Jail.get().imprison(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
			}
		} else {
			player.sendPrivateText(USAGE_JAIL_NAME_MINUTES_REASON);
		}

	}

}
