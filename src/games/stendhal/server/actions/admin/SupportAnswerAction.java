package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.SUPPORTANSWER;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.common.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SupportAnswerAction extends AdministrationAction {



	public static void register() {
		CommandCenter.register(SUPPORTANSWER, new SupportAnswerAction(), 50);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TARGET) && action.has(TEXT)) {
			final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET))
					+ " support question: " + action.get(TEXT);

			new GameEvent(player.getName(), SUPPORTANSWER, action.get(TARGET), action.get(TEXT)).raise();
			final Player supported = SingletonRepository.getRuleProcessor().getPlayer(action.get(TARGET));
			if (supported != null) {

				supported.sendPrivateText("Support (" + player.getTitle() + ") tells you: " + action.get(TEXT) + " \nIf you wish to reply, use /support.");
				supported.notifyWorldAboutChanges();
				SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
				
			} else {
				player.sendPrivateText(action.get(TARGET) + " is not currently logged in.");
			}
		}
	}

}
