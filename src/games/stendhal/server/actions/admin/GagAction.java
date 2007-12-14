package games.stendhal.server.actions.admin;

import games.stendhal.server.GagManager;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import static games.stendhal.server.actions.WellKnownActionConstants.MINUTES;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
public class GagAction extends AdministrationAction {
	private static final String USAGE_GAG_NAME_MINUTES_REASON = "Usage: /gag name minutes reason";
	private static final String _REASON = "reason";

	private static final String _GAG = "gag";

	public static void register(){
		CommandCenter.register(_GAG, new GagAction(), 400);

	}

	@Override
	public void perform(Player player, RPAction action) {
	
		if (action.has(TARGET) && action.has(MINUTES)) {
			String target = action.get(TARGET);
			String reason = "";
			if (action.has(_REASON)) {
				reason = action.get(_REASON);
			}
			try {
				int minutes = action.getInt(MINUTES);
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						_GAG, target, Integer.toString(minutes), reason);
				GagManager.get().gag(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText(USAGE_GAG_NAME_MINUTES_REASON);
			}
		} else {
			player.sendPrivateText(USAGE_GAG_NAME_MINUTES_REASON);
		}
	}

}
