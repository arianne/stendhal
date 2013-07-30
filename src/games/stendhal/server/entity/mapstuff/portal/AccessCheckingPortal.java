/*
 * @(#) src/games/stendhal/server/entity/portal/AccessCheckingPortal.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;

/**
 * An access checking portal is a special kind of portal which requires some
 * condition to use.
 */
abstract class AccessCheckingPortal extends Portal {
    
    /**
     * Optional password required to use portal.
     */
    protected String requiredPassword;
    
    /**
     * Optional message to show when portal is successfully used.
     */
    protected String acceptedMessage;
    
	/**
	 * The message to given when rejected.
	 */
	protected String rejectedMessage;
	
	/**
	 * The radius at which the portal will listed for a password (must be at least 1).
	 */
	protected int listeningRadius = 1;
	
	/**
	 * Creates an access checking portal with default values.
	 */
	public AccessCheckingPortal() {
	    this.rejectedMessage = "Why should i go down there?. It looks very dangerous.";
	}

	/**
	 * Creates an access checking portal.
	 * 
	 * @param rejectMessage
	 *            The message to given when rejected.
	 */
	public AccessCheckingPortal(final String rejectMessage) {
		this.rejectedMessage = rejectMessage;
	}
	
	public AccessCheckingPortal(final RPObject object) {
		super(object);
	}
	
	public String getAcceptedMessage() {
	    return acceptedMessage;
	}
	
	public int getListeningRadius() {
	    return listeningRadius;
	}
	
	public String getRejectedMessage() {
	    return rejectedMessage;
	}
	
	public String getRequiredPassword() {
	    return requiredPassword;
	}

	/**
	 * Determine if this portal can be used.
	 * 
	 * @param user
	 *            The user to be checked.
	 * 
	 * @return <code>true</code> if the user can use the portal.
	 */
	protected boolean isAllowed(RPEntity user) {
	    boolean allowed = true;
	    if (requiredPassword != null) {
	        allowed = false;
	    }
	    return allowed;
	}

	/**
	 * Called when the user is rejected. This sends a rejection message to the
	 * user if set.
	 * 
	 * @param user
	 *            The rejected user.
	 */
	protected void rejected(final RPEntity user) {
		if (rejectedMessage != null) {
			sendMessage(user, rejectedMessage);
			/*
			 * Supesses sprite bounce-back in the case of non-resistant portals
			 */
			if (getResistance() != 0) {
				user.stop();
				user.clearPath();
			}
		}
	}

	/**
	 * Wrapper to send a message to a user, without getting lost.
	 * 
	 * @param user
	 *            The user to send to.
	 * @param text
	 *            The message to send.
	 */
	protected void sendMessage(final RPEntity user, final String text) {
		SingletonRepository.getTurnNotifier().notifyInTurns(0, new SendMessage(user, text));
	}

	/**
	 * Set the accepted message.
	 * 
	 * @param message
	 *             The message to be given when portal successfully used.
	 */
	public void setAcceptedMessage(final String message) {
	    acceptedMessage = message;
	}
	
	/**
	 * Set the rejection message.
	 * 
	 * @param rejectMessage
	 *            The message to given when rejected.
	 */
	public void setRejectedMessage(final String message) {
		rejectedMessage = message;
	}
	
	/**
	 * 
	 * @param password
	 */
	public void setRequiredPassword(final String password) {
	    requiredPassword = password;
	}
	
	/**
	 * 
	 * @param radius
	 *         Distance at wich portal listens for players speaking.
	 */
	public void setListeningRadius(int radius) {
	    // Listening radius must be at least 1.
	    if (radius <= 0) {
	        radius = 1;
	    }
	    listeningRadius = radius;
	}

	/**
	 * Use the portal, if allowed.
	 * 
	 * @param user
	 *            that wants to pass.
	 * @return true if passed , false otherwise.
	 */
	@Override
	public boolean onUsed(final RPEntity user) {
		if (isAllowed(user)) {
			return super.onUsed(user);
		} else {
			/*
			 * Supresses sprite bounce-back in the case of non-resistant portals
			 */
			if (getResistance() != 0) {
				user.stop();
			}
			rejected(user);
			return false;
		}
	}

	/**
	 * A turn listener that sends a user message. Once sendPrivateText() is
	 * fixed (via a queue or something) to always work, this can go away.
	 */
	protected static class SendMessage implements TurnListener {
		/**
		 * The user to send to.
		 */
		private final RPEntity user;
		private final String text;

		/**
		 * Create a message sending turn listener.
		 * 
		 * @param user
		 *            The user to send to.
		 * @param text
		 *            Message to send
		 */
		public SendMessage(final RPEntity user, final String text) {
			this.user = user;
			this.text = text;
		}

		/**
		 * This method is called when the turn number is reached.
		 * 
		 * @param currentTurn
		 *            Current turn number.
		 */
		@Override
		public void onTurnReached(final int currentTurn) {
			user.sendPrivateText(this.text);
			user.notifyWorldAboutChanges();
		}
	}
}
