/***************************************************************************
 *                (C) Copyright 2013-2013 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * a portal which requires a password to pass through
 */

// PasswordPortal does not extend AccessCheckingPortal because they are not "used".
public class PasswordPortal extends Portal {

    // Logger instance
    private static final Logger logger = Logger.getLogger(PasswordPortal.class);

    private String requiredPassword;
    private String acceptedMessage;
    private String rejectedMessage;

    private int listeningRadius = 1;

    /**
     * Creates a default PasswordPortal
     */
    public PasswordPortal() {
    }
    /**
     * creates a portal which requires a password to be said by the player
     *
     * @param
     *      password password to say
     */
    public PasswordPortal(final String password) {
        this.requiredPassword = password;
    }

    /**
     * gets the password
     *
     * @return
     *      password
     */
    public String getPassword() {
        return this.requiredPassword;
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
     * gets the reject message
     *
     * @return
     *      reject message
     */
    public String getRejectedMessage() {
        return this.rejectedMessage;
    }

    @Override
	public void logic() {
        List<Player> players = getNearbyPlayersThatHaveSpoken();

        String text;

        for (Player player : players) {
            text = player.get("text");
            if (text.equals(requiredPassword)) {
                if (acceptedMessage != null) {
                    player.sendPrivateText(acceptedMessage);
                }
                usePortal(player);
            } else if (rejectedMessage != null) {
                player.sendPrivateText(rejectedMessage);
            }
        }
    }

    /**
     * Override so portal does not get "used"
     */
    @Override
    public boolean onUsed(final RPEntity user) {
        if (logger.isDebugEnabled()) {
            logger.debug("Using this portal has been disabled.");
        }
        return false;
    }

    /**
     * Optional message to be sent to player when portal is successfully used
     *
     * @param message
     *      Message to be sent
     */
    public void setAcceptedMessage(final String message) {
        this.acceptedMessage = message;
    }

    /**
     *
     * @param radius
     *      Radius at which portal will listen for player's speech
     */
    public void setListeningRadius(int radius) {
        if (radius <= 0) {
            radius = 1; // The portal must have at least 1 listening square
        }
        this.listeningRadius = radius;
    }

    /**
     * sets the required password
     *
     * @param password
     *      new password
     */
    public void setPassword(final String password) {
        this.requiredPassword = password;
    }

    /**
     * sets the reject message
     *
     * @param message
     *      message informing the player about the failed condition
     */
    public void setRejectedMessage(final String message) {
        this.rejectedMessage = message;
    }
}
