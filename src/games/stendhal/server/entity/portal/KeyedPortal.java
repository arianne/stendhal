/*
 * @(#) src/games/stendhal/server/entity/portal/KeyedPortal.java
 *
 * $Id$
 */

package games.stendhal.server.entity.portal;

//
//

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

/**
 * A keyed portal is a special kind of portal which requires a key to pass it.
 * If the player carries the key with him, he can use the portal just
 * like a normal portal.
 */
public class KeyedPortal extends Portal {

	protected String key;

	protected int quantity;

	protected String rejected;

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key		The name of the required key.
	 */
	public KeyedPortal(String key) {
		this(key, 1);
	}

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key		The name of the required key.
	 * @param quantity	The key quantity required.
	 */
	public KeyedPortal(String key, int quantity) {
		this(key, quantity, null);
	}

	/**
	 * Creates a new keyed portal.
	 *
	 * @param key		The name of the required key.
	 * @param quantity	The key quantity required.
	 * @param rejected	The message to given when rejected.
	 */
	public KeyedPortal(String key, int quantity, String rejected) {
		this.key = key;
		this.quantity = quantity;
		this.rejected = rejected;
	}

	/**
	 * Use the portal, if allowed.
	 */
	@Override
	public void onUsed(RPEntity user) {
		if (user.isEquipped(key, quantity)) {
			super.onUsed(user);
		} else if (rejected != null) {
			TurnNotifier.get().notifyInTurns(0, new SendMessage(user), rejected);
		}
	}

	//
	//

	/*
	 * A turn listener that sends a user message. Once sendPrivateText()
	 * is fixed (via a queue or something) to always work, this can go
	 * away.
	 */
	protected static class SendMessage implements TurnListener {

		/**
		 * The user to send to.
		 */
		protected RPEntity user;

		/**
		 * Create a message sending turn listener.
		 *
		 * @param	user		The user to send to.
		 */
		public SendMessage(RPEntity user) {
			this.user = user;
		}

		//
		// TurnListener
		//

		/**
		 * This method is called when the turn number is reached.
		 *
		 * @param	currentTurn	Current turn number.
		 * @param	message		The string that was used.
		 */
		public void onTurnReached(int currentTurn, String message) {
			user.sendPrivateText(message);
		}
	}
}
