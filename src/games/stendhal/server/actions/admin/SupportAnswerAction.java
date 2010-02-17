package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.support.SupportMessageTemplatesFactory;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import marauroa.common.game.RPAction;
import static games.stendhal.common.constants.Actions.SUPPORTANSWER;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;

public class SupportAnswerAction extends AdministrationAction {
	
	private static final Map<String, String> messageTemplates = new SupportMessageTemplatesFactory().getTemplates();

	public static void register() {
		CommandCenter.register(SUPPORTANSWER, new SupportAnswerAction(), 50);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (action.has(TARGET) && action.has(TEXT)) {
			String reply = action.get(TEXT);
			
			// test for use of standard response shortcut, and replace the reply message if so
			// if you alter these please update client/actions/GMHelpAction (or put the string replies in a common file if you like)
			if (reply.startsWith("$")) {
				if (messageTemplates.containsKey(reply)) {
					reply = messageTemplates.get(reply);
				} else {
					player.sendPrivateText(reply + " is not a recognised shortcut. Did you mean $faq, $faqsocial, $ignore, $faqpvp, $wiki, $knownbug, $bugstracker, $rules, $notsupport or $spam?");
					// send no support answer message if the shortcut wasn't understood
					return;
				}				
			}
			final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET))
					+ " support question: " + reply;

			new GameEvent(player.getName(), SUPPORTANSWER, action.get(TARGET), reply).raise();
			final Player supported = SingletonRepository.getRuleProcessor().getPlayer(action.get(TARGET));
			if (supported != null) {

				supported.sendPrivateText(NotificationType.SUPPORT, "Support (" + player.getTitle() + ") tells you: " + reply + " \nIf you wish to reply, use /support.");
				supported.notifyWorldAboutChanges();
				SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
				
			} else {
				player.sendPrivateText(action.get(TARGET) + " is not currently logged in.");
			}
		}
	}
}
