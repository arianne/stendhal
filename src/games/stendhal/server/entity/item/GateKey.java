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
package games.stendhal.server.entity.item;

import java.util.Map;

/**
 * A key that matches if the gate identifier is right.
 */
public class GateKey extends Item {
	/**
	 * Different color keys
	 */
	private static String[] imageNames = {
		"purple",
		"turquoise",
		"yellow",
		"lime",
		"pink",
		"red",
		"orange",
		"navy",
		"brown",
		"green",
		"grey",
		"lilac" };

	/**
	 * Used to indicate that the key never expires
	 */
	private static long NEVER = Long.MAX_VALUE;

	public GateKey(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		// first slot is identifier, second slot is expiration timestamp
		setInfoString("0;" + NEVER);
		setPersistent(true);
	}

	/**
	 * Copy constructor.
	 *
	 * @param key
	 *            the key to be copied.
	 */
	public GateKey(final GateKey key) {
		super(key);
	}

	/**
	 * Sets the key to open the set lock identifier and to expire at the given
	 * time.
	 *
	 * @param gateId The identifier to link with.
	 * @param expirationTime time stamp
	 */
	public void setup(final String gateId, final long expirationTime) {
		setInfoString(gateId + ";" + Long.toString(expirationTime));
		chooseImage();
	}

	/**
	 * Checks if the key matches the given gate identifier.
	 *
	 * @param gateId
	 *            Identifier of the gate
	 * @return true if the identifiers match, false otherwise
	 */
	public boolean matches(final String gateId) {
		final String[] info = getInfoString().split(";");
		final long expirationTime = Long.parseLong(info[1]);

		return info[0].equals(gateId) && expirationTime > System.currentTimeMillis();
	}

	/**
	 * Choose an image for the key, depending on gate identifier and expiration
	 * time.
	 */
	private void chooseImage() {
		final String[] info = getInfoString().split(";");

		put("subclass", imageNames[Math.abs((info[0].hashCode() + info[1].hashCode())
						% imageNames.length)]);
	}
}
