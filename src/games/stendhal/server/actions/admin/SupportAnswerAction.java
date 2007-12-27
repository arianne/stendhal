package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;

public class SupportAnswerAction extends AdministrationAction {

	private static final String _TEXT = "text";

	private static final String _SUPPORTANSWER = "supportanswer";

	public static void register() {
		CommandCenter.register(_SUPPORTANSWER, new SupportAnswerAction(), 50);

	}

	@Override
	public void perform(Player player, RPAction action) {
		if (action.has(TARGET) && action.has(_TEXT)) {
			final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET))
					+ " support question: " + action.get(_TEXT);

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), _SUPPORTANSWER, action.get(TARGET),
					action.get(_TEXT));
			Player supported = StendhalRPRuleProcessor.get().getPlayer(action.get(TARGET));
			if (supported != null) {

				supported.sendPrivateText("Support (" + player.getTitle() + ") tells you: " + action.get(_TEXT));
				supported.notifyWorldAboutChanges();
				StendhalRPRuleProcessor.get().getOnlinePlayers().forFilteredPlayersExecute(

				new Task() {

					public void execute(Player player) {
						player.sendPrivateText(message);
						player.notifyWorldAboutChanges();

					}

				}, new FilterCriteria<Player>() {

					public boolean passes(Player p) {
						return p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT;
					}

				});
			} else {
				player.sendPrivateText(action.get(TARGET) + " is not currently logged in.");
			}
		}
	}

}
