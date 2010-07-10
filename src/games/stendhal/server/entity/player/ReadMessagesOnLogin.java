package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.ChatMessage;
import games.stendhal.server.core.engine.dbcommand.GetPostmanMessagesCommand;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;

import java.util.List;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

import org.apache.log4j.Logger;

public class ReadMessagesOnLogin implements LoginListener, TurnListener {
	
	private static final Logger LOGGER = Logger.getLogger(ReadMessagesOnLogin.class);
	
	private ResultHandle handle = new ResultHandle();
	
	/** 
	 * Get any messages stored for the player when they log in
	 */
	public void onLoggedIn(final Player player) {
		DBCommand command = new GetPostmanMessagesCommand(player);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(0, this);
	}
	
	/**
	 * Completes handling the get messages action.
	 * 
	 * @param currentTurn.
	 */
	public void onTurnReached(int currentTurn) {
		List<GetPostmanMessagesCommand> list = DBCommandQueue.get().getResults(GetPostmanMessagesCommand.class, handle);
		
		if (list.isEmpty()) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}

		GetPostmanMessagesCommand checkcommand = list.get(0);
	    List<ChatMessage> messages = checkcommand.getMessages();
		Player player = checkcommand.getPlayer();
		LOGGER.debug(messages.size()+ " messages left for " + player.getName());
		for (ChatMessage chatmessage : messages) {
			LOGGER.debug(player.getName() + " got message: " + chatmessage.toString());
			player.sendPrivateText("postman tells you: " + chatmessage.getSource() + " asked me to deliver this message: \n" + chatmessage.getMessage()
					+ "\non " + chatmessage.getTimestamp());
			// we faked that postman sent a message, better set the last private chatter incase the player now uses /answer
			player.setLastPrivateChatter("postman");
		}
		
	}
	
}