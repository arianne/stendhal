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

import games.stendhal.server.entity.RPEntity;

/**
 * a portal which requires a password to pass through
 */

// PasswordPortal does not extend AccessCheckingPortal because they are not "used".
public class PasswordPortal extends Portal {
    
    private String password;
    private String rejected;

    /**
     * Creates a default PasswordPortal
     */
    public PasswordPortal() {
    }
    /**
     * creates a portal which requires a password to be said by the player
     *
     * @param password password to say
     */
    public PasswordPortal(String password) {
        this.password = password;
    }

    /**
     * sets the password
     *
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * gets the password
     *
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * is the password correct?
     *
     * @param password password to check
     * @return true, if the password is correct, false otherwise
     */
    @SuppressWarnings("hiding")
    public boolean isCorrect(String password) {
        return password.equals(this.password);
    }
    
    /**
     * Override so portal does not get "used"
     */
    @Override
    public boolean onUsed(final RPEntity user) {
        user.sendPrivateText("Using this portal is deactivated");
        return false;
    }

    /**
     * sets the reject message
     *
     * @param message message informing the player about the failed condition
     */
    public void setRejectedMessage(final String message) {
        this.rejected = message;
    }

    /**
     * gets the reject message
     *
     * @return reject message
     */
    public String getRejectedMessage() {
        return this.rejected;
    }
    
    /**
     * Password portals are "used" by saying the correct password
     *
     * @param entity RPEntity trying to use the portal
     * @param password password said by the entity
     */
    @SuppressWarnings("hiding")
	public void sayPassword(final RPEntity entity, final String password) {
        if (password.equals(this.password)) {
            onUsed(entity);
        }
    }
}
