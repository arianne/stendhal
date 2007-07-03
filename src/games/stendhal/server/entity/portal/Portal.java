/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.portal;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;

public class Portal extends Entity implements UseListener {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Portal.class);

	private boolean settedDestination;

	private Object reference;

	private String destinationZone;

	private Object destinationReference;

	public static void generateRPClass() {
		try {
			RPClass portal = new RPClass("portal");
			portal.isA("entity");
			portal.add("hidden", Type.FLAG);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public Portal() {
		put("type", "portal");
		settedDestination = false;
	}

	/**
	 * @deprecated	Use setReference().
	 */
	@Deprecated
	public void setNumber(int number) {
		setReference(new Integer(number));
	}

	/**
	 * Set the portal reference to identify this specific portal with-in
	 * a zone. This value is opaque and requires a working equals(), but
	 * typically uses a String or Integer.
	 *
	 * @param	reference	A reference tag.
	 */
	public void setReference(Object reference) {
		this.reference = reference;
	}

	public Object getReference() {
		return reference;
	}

	/**
	 * @deprecated	Use setDestination(String, Object).
	 */
	@Deprecated
	public void setDestination(String zone, int number) {
		setDestination(zone, new Integer(number));
	}

	/*
	 * Set the destination portal zone and reference. The reference should
	 * match the same type/value as that passed to setReference() in the
	 * corresponding portal.
	 *
	 * @param	zone		The target zone.
	 * @param	reference	A reference tag.
	 */
	public void setDestination(String zone, Object reference) {
		this.destinationReference = reference;
		this.destinationZone = zone;
		this.settedDestination = true;
	}

	public Object getDestinationReference() {
		return destinationReference;
	}

	public String getDestinationZone() {
		return destinationZone;
	}

	public boolean loaded() {
		return settedDestination;
	}



	@Override
	public String toString() {
		return "Portal at " + get("zoneid") + "[" + getX() + "," + getY() + "]";
	}

	/**
	 * Use the portal.
	 *
	 * @return	<code>true</code> if the portal worked,
	 *		<code>false</code> otherwise.
	 */
	protected boolean usePortal(Player player) {
		if (!nextTo(player)) {
			// Too far to use the portal
			return false;
		}

		if (getDestinationZone() == null) {
			// This portal is incomplete
			logger.error(this + " has no destination.");
			return false;
		}

		StendhalRPZone destZone = StendhalRPWorld.get().getZone(getDestinationZone());

		Portal dest = destZone.getPortal(getDestinationReference());

		if (dest == null) {
			// This portal is incomplete
			logger.error(this + " has invalid destination");
			return false;
		}

		player.teleport(destZone, dest.getX(), dest.getY(), null, null);
		player.stop();

		dest.onUsedBackwards(player);

		return true;
	}

	public void onUsed(RPEntity user) {
		usePortal((Player) user);
	}

	public void onUsedBackwards(RPEntity user) {
		// do nothing
	}
}
