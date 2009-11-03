package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.SUPPORTANSWER;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
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
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (action.has(TARGET) && action.has(TEXT)) {
			String reply = action.get(TEXT);
			
			// test for use of standard response shortcut, and replace the reply message if so
			// if you alter these please update client/actions/GMHelpAction (or put the string replies in a common file if you like)
			if (reply.startsWith("$")) {
				if ("$faq".equals(reply)) {
					reply = "Hi, you will find the answer to your question in the Stendhal FAQ. It's very helpful so please read it thoroughly! #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ. Thanks for playing Stendhal!";
				} else if ("$faqsocial".equals(reply)) {
					reply = "Hi, sorry to hear about that. But unfortunately support is not here to help you with social problems unless it gets way out of hand. This issue is discussed further on the stendhal FAQ and how to deal with it is described there. Please read carefully #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ#Player_social_problems - hopefully the rest of your Stendhal experience will be more pleasant.";
				} else if ("$faqpvp".equals(reply)) {
					reply = "Hi, sorry to hear about that. Player attacks are actually within the rules of the game, and it is not something that support gets involved with for that reason. Please read carefully #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ#Player_vs_Player - good luck for the future.";
				} else if ("$wiki".equals(reply)) {
					reply = "Hi, this is a question which is answered on the Stendhal wiki, please look on #http://stendhal.game-host.org/wiki/index.php/Stendhal as this is full of useful information. Thanks for playing Stendhal.";
				} else if ("$knownbug".equals(reply)) {
					reply = "Hi, thank you for telling us about this bug, we have found it ourselves too and it's already reported. Thank you though and please do keep reporting bugs if you see them!";
				} else if ("$bugstracker".equals(reply)) {
					reply = "Hi, it sounds like you have found a new bug. Please could you create a bug report, details on how to do this are at #http://stendhal.game-host.org/wiki/index.php/SubmitBug - thank you very much.";
				} else if ("$rules".equals(reply)) {
					reply = "Please read the Stendhal Rules at #http://stendhal.game-host.org/wiki/index.php/StendhalRuleSystem - thank you.";
				} else if ("$abuse".equals(reply)) {
					reply = "Sorry, but support is not here for that issue. Please use #http://stendhal.game-host.org and the wiki #http://stendhal.game-host.org/wiki/index.php/Stendhal as information sources. Repeated abuse of the support channel will be punished.";
				} else {
					player.sendPrivateText(reply + " is not a recognised shortcut. Did you mean $faq, $faqsocial, $faqpvp, $wiki, $knownbug, $bugstracker, $rules or $abuse?");
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
