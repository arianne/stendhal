/*
 * @(#) src/games/stendhal/server/entity/portal/AccessCheckingPortal.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.portal;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * An access checking portal is a special kind of portal which requires some
 * condition to use.
 */
abstract class AccessCheckingPortal extends Portal {

    /** the logger instance. */
    private static final Logger logger = Logger.getLogger(AccessCheckingPortal.class);

    /** Immediate execution of action when player says password. */
    protected boolean instantAction = false;

    /** Radius at which portal will "listen" for speaking players. */
    protected int listeningRadius;

    /** Optional message to give when correct password used. */
    protected String passwordAcceptedMessage;

    /** Optional message to give when incorrect password used. */
    protected String passwordRejectedMessage;

    /** ID for the portal */
    protected int portalID;
    protected static int portalIDCounter = 100;

    /** The message to give when rejected. */
    protected String rejectedMessage;

    /** Optional password to use portal. */
    protected String requiredPassword;

    /** Optional action to take when access is rejected. */
    protected ChatAction rejectedAction;

    /** Override continuous movement setting. */
    protected boolean forceStop = false;

    /**
     * Creates an access checking portal with default values.
     */
    public AccessCheckingPortal() {
        this.rejectedMessage = "Why should i go down there?. It looks very dangerous.";

        portalID = portalIDCounter;
        portalIDCounter += 1;
    }

	/**
	 * Creates an access checking portal.
	 *
	 * @param rejectMessage
	 *            The message to given when rejected.
	 */
	public AccessCheckingPortal(final String rejectMessage) {
		this.rejectedMessage = rejectMessage;

        portalID = portalIDCounter;
        portalIDCounter += 1;
	}

	/**
	 *
	 * @param object
	 */
	public AccessCheckingPortal(final RPObject object) {
		super(object);

        portalID = portalIDCounter;
        portalIDCounter += 1;
	}

	/**
	 *
	 * @return
	 *      Message when password is accepted.
	 */
    public String getPasswordAcceptedMessage() {
        return passwordAcceptedMessage;
    }

    /**
     *
     * @return
     *      Messager when password is rejected.
     */
    public String getPasswordRejectedMessage() {
        return passwordRejectedMessage;
    }

    /**
     *
     * @return
     *      Radius at which portal detects player speech.
     */
    public int getListeningRadius() {
        return listeningRadius;
    }

    /**
     * Finds players nearby that have spoken.
     *
     * @return
     *      List of players
     */
    private List<Player> getNearbyPlayersThatHaveSpoken() {
        final int x = getX();
        final int y = getY();

        final List<Player> players = new LinkedList<Player>();

        for (final Player player : getZone().getPlayers()) {
            final int px = player.getX();
            final int py = player.getY();

            if (player.has("text")) {
                int dx = px - x;
                int dy = py - y;

                if (Math.abs(dx)<listeningRadius && Math.abs(dy)<listeningRadius) { // check rectangular area
//              if (dx*dx + dy*dy < range*range) { // optionally we could check a circular area
                    players.add(player);
                }
            }
        }

        return players;
    }

    /**
     * Gets the message to send when player is denied access to portal.
     *
     * @return
     *      Rejected message
     */
    public String getRejectedMessage() {
        return rejectedMessage;
    }

    /**
     * Gets the password required to use the portal.
     *
     * @return
     *      Required password
     */
    public String getRequiredPassword() {
        return requiredPassword;
    }

    /**
     * Checks if the portal executes an action immediately after password is said.
     *
     * @return
     *      instantAction
     */
    public boolean hasInstanceAction() {
        return instantAction;
    }

	/**
	 * Determine if this portal can be used.
	 *
	 * @param user
	 *            The user to be checked.
	 *
	 * @return <code>true</code> if the user can use the portal.
	 */
	protected abstract boolean isAllowed(RPEntity user);

	public boolean playerIsPortalUnlocked(final Player player, final Portal portal) {
	    if (player.getUnlockedPortals().contains(portalID)) {
	        return true;
	    }
	    return false;
	}

    @Override
	public void logic() {
        // Execute action for password portal if required password is set.
        if (requiredPassword != null) {
            List<Player> players = getNearbyPlayersThatHaveSpoken();

            String text;

            for (Player player : players) {
                text = player.get("text");
                if (text.equals(requiredPassword)) {
                    if (passwordAcceptedMessage != null) {
                        sendMessage(player, passwordAcceptedMessage);
                    }
                    // Temporarily unlock this portal for player.
                    player.unlockPortal(portalID);
                } else if (passwordRejectedMessage != null) {
                    sendMessage(player, passwordRejectedMessage);
                }
            }
        }
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
            // Check if this is a password portal and player has access.
            if ((requiredPassword != null) && !playerIsPortalUnlocked((Player) user, this)) {
                logger.debug("Player " + user.getName() + " does not have access to portal ID "
                        + Integer.toString(getID().getObjectID()) + " at " + this.getZone().getName()
                        + " (" + Integer.toString(getX()) + "," + Integer.toString(getY())
                        + "). Required password: " + requiredPassword);
                return false;
            }
            return super.onUsed(user);
        }
        // Supresses sprite bounce-back in the case of non-resistant portals
        if (getResistance() != 0) {
            user.stop();
        }
        rejected(user);
        return false;
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

			if (rejectedAction != null) {
				rejectedAction.fire((Player) user, null, null);
			}

			if (forceStop) {
				if (user instanceof Player) {
					((Player) user).forceStop();
				} else {
					user.stop();
				}
			} else {
				/*
				 * Suppresses sprite bounce-back in the case of non-resistant portals
				 */
				if (getResistance() != 0) {
					user.stop();
					user.clearPath();
				}
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
     *
     * @param instant
     *         Use portal automatically.
     */
    public void setInstantAction(final boolean instant) {
        instantAction = instant;
//      logger.info("\nSetting instant action to \"" + Boolean.toString(instant) + "\" for portal at "
//              + getZone().getName() + " (" + Integer.toString(getX()) + "," + Integer.toString(getY()) + "\n");
    }

	/**
	 * Set the password accepted message.
	 *
	 * @param message
	 *            Optional message to be given when correct password used.
	 */
	public void setPasswordAcceptedMessage(final String message) {
	    passwordAcceptedMessage = message;
	}

    /**
     * Set the password rejected message.
     *
     * @param message
     *            Optional message to be given when incorrect password used.
     */
    public void setPasswordRejectedMessage(final String message) {
        passwordRejectedMessage = message;
    }


	/**
	 * Sets the radius at which the portal will "hear" speaking players.
	 *
	 * @param radius
	 *         Distance at wich portal listens for players speaking.
	 */
	public void setListeningRadius(int radius) {
	    if (radius <= 0) {
	        radius = 1;
	    }
	    listeningRadius = radius;
	}

	/**
	 * Set the rejection message.
	 *
	 * @param message The message to give when rejected.
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
	 * Initiates an action to take on rejection.
	 *
	 * @param rejectedAction
	 * 		ChatAction to execute.
	 */
	public void setRejectedAction(ChatAction rejectedAction) {
		this.rejectedAction = rejectedAction;
	}

	/**
	 * Sets flag to override continuous movement & force entity to stop.
	 *
	 * @param forceStop
	 * 		If <code>true</code>, entity is forced to stop movement.
	 */
	public void setForceStop(final boolean forceStop) {
		this.forceStop = forceStop;
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
