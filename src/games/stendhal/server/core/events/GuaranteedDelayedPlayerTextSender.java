/**
 *
 */
package games.stendhal.server.core.events;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandQueue;

/**
 * Sends a private message to a player after a short delay, and if they weren't online, uses postman
 *
 * @author kymara
 *
 */
public class GuaranteedDelayedPlayerTextSender extends DelayedPlayerTextSender {
	private final String source;

	/**
	 * Creates a new GuaranteedDelayedPlayerTextSender.
	 * @param source
	 * @param player
	 * @param message
	 * @param seconds
	 */
	public GuaranteedDelayedPlayerTextSender(final String source, final Player player, final String message,
			int seconds) {
		super(player, message, seconds);
		this.source = source;
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		final String playername = player.getName();
		final Player playerNow = SingletonRepository.getRuleProcessor().getPlayer(playername);
		if (playerNow != null) {
			playerNow.sendPrivateText(NotificationType.PRIVMSG, source + " tells you:\n" + message);
		} else {
			DBCommandQueue.get().enqueue(new StoreMessageCommand(source, playername, message, "N"));
		}
	}
}
