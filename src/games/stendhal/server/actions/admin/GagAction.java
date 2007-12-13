package games.stendhal.server.actions.admin;

import games.stendhal.server.GagManager;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class GagAction extends AdministrationAction {
	private static final String USAGE_GAG_NAME_MINUTES_REASON = "Usage: /gag name minutes reason";
	private static final String _REASON = "reason";
	private static final String _MINUTES = "minutes";
	private static final String _TARGET = "target";
	private static final String _GAG = "gag";

	public static void register(){
		CommandCenter.register(_GAG, new GagAction(), 400);

	}

	@Override
	public void perform(Player player, RPAction action) {
	
		if (action.has(_TARGET) && action.has(_MINUTES)) {
			String target = action.get(_TARGET);
			String reason = "";
			if (action.has(_REASON)) {
				reason = action.get(_REASON);
			}
			try {
				int minutes = action.getInt(_MINUTES);
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
