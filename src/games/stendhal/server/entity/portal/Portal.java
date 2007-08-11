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
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

/**
 * A portal which teleports the player to another portal if used.
 */
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
			portal.addAttribute("hidden", Type.FLAG);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new portal
	 */
	public Portal() {
		setRPClass("portal");
		put("type", "portal");

		settedDestination = false;
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

	/**
	 * gets the identifier of this portal
	 *
	 * @return identifier
	 */
	public Object getReference() {
		return reference;
	}


	/**
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
	 * @param player the Player who wants to use this portal 
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

	public boolean onUsed(RPEntity user) {
		return usePortal((Player) user);
	}

	/**
	 * if this portal is the destination of another portal used
	 *
	 * @param user the player who used the other portal teleporting to us
	 */
	@SuppressWarnings("unused")
	public void onUsedBackwards(RPEntity user) {
		// do nothing
	}
}
