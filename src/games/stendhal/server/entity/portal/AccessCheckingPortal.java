/*
 * @(#) src/games/stendhal/server/entity/portal/AccessCheckingPortal.java
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
 * An access checking portal is a special kind of portal which requires some
 * condition to use.
 */
public abstract class AccessCheckingPortal extends Portal {
	/**
	 * The message to given when rejected.
	 */
	protected String	rejectMessage;


	/**
	 * Creates an access checking portal.
	 *
	 * @param	rejectMessage	The message to given when rejected.
	 */
	public AccessCheckingPortal(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}


	//
	// AccessCheckingPortal
	//

	/**
	 * Determine if this portal can be used.
	 *
	 * @param	user		The user to be checked.
	 *
	 * @return	<code>true<code> if the user can use the portal.
	 */
	protected abstract boolean isAllowed(RPEntity user);


	/**
	 * Called when the user is rejected.
	 * This sends a rejection message to the user if set.
	 *
	 * @param	user		The rejected user.
	 */
	protected void rejected(RPEntity user) {
		if (rejectMessage != null) {
			sendMessage(user, rejectMessage);
		}
	}


	/**
	 * Wrapper to send a message to a user, without getting lost.
	 *
	 * @param	user		The user to send to.
	 * @param	text		The message to send.
	 */
	protected void sendMessage(RPEntity user, String text) {
		TurnNotifier.get().notifyInTurns(0, new SendMessage(user), text);
	}


	/**
	 * Set the rejection message.
	 *
	 * @param	rejectMessage	The message to given when rejected.
	 */
	public void setRejectedMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}


	//
	// Entity
	//

	/**
	 * Use the portal, if allowed.
	 */
	@Override
	public void onUsed(RPEntity user) {
		if (isAllowed(user)) {
			super.onUsed(user);
		} else {
			user.stop();
			rejected(user);
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
			user.notifyWorldAboutChanges();
		}
	}
}
