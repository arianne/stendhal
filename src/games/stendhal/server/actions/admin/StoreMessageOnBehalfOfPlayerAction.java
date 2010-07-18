package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;
import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

public class StoreMessageOnBehalfOfPlayerAction extends AdministrationAction implements TurnListener  {

	private ResultHandle handle = new ResultHandle();
	
	public static void register() {
		CommandCenter.register("storemessageonbehalfofplayer", new StoreMessageOnBehalfOfPlayerAction(), 2000);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		// TODO: do we want to check this? the player might be postman.
		if (!player.getChatBucket().checkAndAdd()) {
			return;
		}

		if (action.has("source") && action.has(TARGET) && action.has(TEXT)) {
			
			String message = action.get(TEXT);
			
			DBCommand command = new StoreMessageCommand(action.get("source"), action.get(TARGET), message, "P");
			DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
			TurnNotifier.get().notifyInTurns(0, this);
		}
	}
	
	/**
	 * Completes handling the store message action
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
		
		final Player admin = SingletonRepository.getRuleProcessor().getPlayer(adminName);
		
		if(!"postman".equals(adminName) && !characterExists) {
			if (admin != null) {
				// incase admin logged out while waiting we want to avoid NPE
				admin.sendPrivateText(NotificationType.ERROR, "Sorry, " + target + " could not be found, so the message cannot be stored.");
			}
			return;
		} 
		
	}
}
