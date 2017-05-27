/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

public class ExpirationTracker extends Entity {
	/**
	 * The RPClass name
	 */
	public final static String RPCLASS_EXPIRATION_TRACKER = "expiration_tracker";

	/**
	 * The expiration attribute name
	 */
	private final static String EXPIRES = "expires";

	/**
	 * The identifier attribute name
	 */
	private final static String TRACKER_ID = "identifier";

	/**
	 * The player attribute name
	 */
	private final static String PLAYER_NAME = "player_name";

	/**
	 * Generate the RPClass of this entity
	 */
	public static void generateRPClass() {
		if (!RPClass.hasRPClass(RPCLASS_EXPIRATION_TRACKER)) {
			final RPClass gate = new RPClass(RPCLASS_EXPIRATION_TRACKER);
			gate.isA("entity");
			gate.addAttribute(EXPIRES, Type.STRING);
			gate.addAttribute(TRACKER_ID, Type.STRING);
			gate.addAttribute(PLAYER_NAME, Type.STRING);
		}
	}

	/**
	 * Constructor to create an empty tracker
	 */
	public ExpirationTracker() {
		super();

		setRPClass(RPCLASS_EXPIRATION_TRACKER);
		put("type", RPCLASS_EXPIRATION_TRACKER);
		put(EXPIRES, Long.toString(0));
		put(TRACKER_ID, "");
		put(PLAYER_NAME, "");

		setResistance(0);

		hide();
		store();
	}

	/**
	 * Sets the new expiration time
	 *
	 * @param newTime the new expiration time
	 */
	public void setExpirationTime(final long newTime) {
		put(EXPIRES, Long.toString(newTime));

		saveToDatabase();
	}

	/**
	 * Gets the expiration time
	 *
	 * @return the expiration time
	 */
	public long getExpirationTime() {
		return Long.parseLong(get(EXPIRES));
	}

	/**
	 * Sets the new player id
	 *
	 * @param player the new player id
	 */
	public void setPlayerName(final String player) {
		put(PLAYER_NAME, player);

		saveToDatabase();
	}

	/**
	 * Gets the player id
	 *
	 * @return the player id
	 */
	public String getPlayerName() {
		return get(PLAYER_NAME);
	}

	/**
	 * Saves this entity to the database keeping the expiration time persistent
	 */
	private void saveToDatabase() {
		StendhalRPZone zone = this.getZone();

		if (zone != null) {
			zone.storeToDatabase();
		}
	}

	/**
	 * Sets this tracker's identifier
	 *
	 * @param id the new identifier
	 */
	public void setIdentifier(final String id) {
		put(TRACKER_ID, id);

		saveToDatabase();
	}

	/**
	 * Gets this tracker's identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return get(TRACKER_ID);
	}
}
