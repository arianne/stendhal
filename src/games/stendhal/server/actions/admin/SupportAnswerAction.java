package games.stendhal.server.actions.admin;

import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import games.stendhal.common.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SupportAnswerAction extends AdministrationAction {

	private static final String _TEXT = "text";

	private static final String _SUPPORTANSWER = "supportanswer";

	public static void register() {
		CommandCenter.register(_SUPPORTANSWER, new SupportAnswerAction(), 50);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TARGET) && action.has(_TEXT)) {
			final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET))
					+ " support question: " + action.get(_TEXT);

			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(), _SUPPORTANSWER, action.get(TARGET),
					action.get(_TEXT));
			final Player supported = SingletonRepository.getRuleProcessor().getPlayer(action.get(TARGET));
			if (supported != null) {

				supported.sendPrivateText("Support (" + player.getTitle() + ") tells you: " + action.get(_TEXT) + " \nIf you wish to reply, use /support.");
				supported.notifyWorldAboutChanges();
				SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
				
			} else {
				player.sendPrivateText(action.get(TARGET) + " is not currently logged in.");
			}
		}
	}

}
