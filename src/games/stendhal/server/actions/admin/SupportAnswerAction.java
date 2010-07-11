package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.common.messages.SupportMessageTemplatesFactory;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

import marauroa.common.game.RPAction;
import static games.stendhal.common.constants.Actions.SUPPORTANSWER;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;

public class SupportAnswerAction extends AdministrationAction implements TurnListener  {
	
	private static final Map<String, String> messageTemplates = new SupportMessageTemplatesFactory().getTemplates();

	private ResultHandle handle = new ResultHandle();
	
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
			final Player supported = SingletonRepository.getRuleProcessor().getPlayer(action.get(TARGET));
			
			if (reply.startsWith("$")) {
				if (messageTemplates.containsKey(reply)) {
					reply = messageTemplates.get(reply);
					reply = String.format(reply, action.get(TARGET));
				} else {
					player.sendPrivateText(reply + " is not a recognised shortcut. Did you mean $faq, $faqsocial, $ignore, $faqpvp, $wiki, $knownbug, $bugstracker, $rules, $notsupport or $spam?");
					// send no support answer message if the shortcut wasn't understood
					return;
				}				
			}
			
			final String message = player.getTitle() + " answers " + Grammar.suffix_s(action.get(TARGET))
					+ " support question: " + reply;

			new GameEvent(player.getName(), SUPPORTANSWER, action.get(TARGET), reply).raise();
			if (supported != null) {

				supported.sendPrivateText(NotificationType.SUPPORT, "Support (" + player.getTitle() + ") tells you: " + reply + " \nIf you wish to reply, use /support.");
				supported.notifyWorldAboutChanges();
				SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
				
			} else {
				// that player is not logged in. Do they exist at all or are they just offline? Try sending a message with postman.
				DBCommand command = new StoreMessageCommand(player.getName(), action.get(TARGET), "In answer to your support question:\n" + reply + " \nIf you wish to reply, use /support.", "S");
				DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
				TurnNotifier.get().notifyInTurns(0, this);
			}
		}
	}
	
	/**
	 * Completes handling the supportanswer action.
	 * 
	 * @param currentTurn ignored
	 */
	public void onTurnReached(int currentTurn) {
		StoreMessageCommand checkcommand = DBCommandQueue.get().getOneResult(StoreMessageCommand.class, handle);
		
		if (checkcommand == null) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}

		boolean characterExists = checkcommand.targetCharacterExists();
		String adminName = checkcommand.getSource();
		String target = checkcommand.getTarget();
		String supportmessage = checkcommand.getMessage();
		
		final Player admin = SingletonRepository.getRuleProcessor().getPlayer(adminName);
		
		if(!characterExists) {
				if (admin != null) {
					// incase admin logged out while waiting we want to avoid NPE
					admin.sendPrivateText(NotificationType.ERROR, "Sorry, " + target + " could not be found.");
				}
				return;
		} 
		
		final String message = adminName + " answers " + Grammar.suffix_s(target)
		+ " support question using postman: " + supportmessage;
		
		SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
	}
}
