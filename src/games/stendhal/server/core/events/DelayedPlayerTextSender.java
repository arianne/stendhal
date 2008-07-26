package games.stendhal.server.core.events;

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;

/**
 * Delays the sending of text until the next turn (to work
 * around problems like zone changes).
 */
class DelayedPlayerTextSender implements TurnListener {
	private final Player player;
	private final String message;
	private final NotificationType type;
	
	/**
	 * Creates a new private message type DelayedPlayerTextSender. 
	 * 
	 * @param player
	 *            Player to send this message to
	 * @param message
	 *            message
	 */
	DelayedPlayerTextSender(final Player player, final String message) {
		this.player = player;
		this.message = message;
		this.type = NotificationType.PRIVMSG;
	}

	/**
	 * Creates a new DelayedPlayerTextSender.
	 * 
	 * @param player
	 *            Player to send this message to
	 * @param message
	 *            message
	 * @param type
	 *            logical notificationType
	 */
	DelayedPlayerTextSender(final Player player, final String message, final NotificationType type) {
		this.player = player;
		this.message = message;
		this.type = type;
	}
	
	public void onTurnReached(final int currentTurn) {
		player.sendPrivateText(type, message);
	}
}