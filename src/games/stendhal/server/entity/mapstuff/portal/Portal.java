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
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.StatusType;

import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

/**
 * A portal which teleports the player to another portal if used.
 */
public class Portal extends Entity implements UseListener {
	private static final String RPCLASS_NAME = "portal";

	/**
	 * The hidden flags attribute name.
	 */
	protected static final String ATTR_HIDDEN = "hidden";
	protected static final String ATTR_USE = "use";

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Portal.class);

	private boolean isDestinationSet;

	private Object identifier;

	private String destinationZone;

	private Object destinationReference;

	public static void generateRPClass() {
		try {
				if (!RPClass.hasRPClass(RPCLASS_NAME)){
					final RPClass portal = new RPClass(RPCLASS_NAME);
					portal.isA("entity");
					portal.addAttribute(ATTR_USE, Type.FLAG);
					portal.addAttribute(ATTR_HIDDEN, Type.FLAG);
				}
		} catch (final SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new portal.
	 */
	public Portal() {
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);

		isDestinationSet = false;
	}
	
	public Portal(final RPObject object) {
		super(object);
		setRPClass(RPCLASS_NAME);
		put("type", RPCLASS_NAME);
		isDestinationSet = false;
	}

	/**
	 * Set the portal reference to identify this specific portal with-in a zone.
	 * This value is opaque and requires a working equals(), but typically uses
	 * a String or Integer.
	 * 
	 * @param reference
	 *            A reference tag.
	 */
	public void setIdentifier(final Object reference) {
		this.identifier = reference;
	}

	/**
	 * gets the identifier of this portal.
	 * 
	 * @return identifier
	 */
	public Object getIdentifier() {
		return identifier;
	}

	/**
	 * Set the destination portal zone and reference. The reference should match
	 * the same type/value as that passed to setReference() in the corresponding
	 * portal.
	 * 
	 * @param zone
	 *            The target zone.
	 * @param reference
	 *            A reference tag.
	 */
	public void setDestination(final String zone, final Object reference) {
		this.destinationReference = reference;
		this.destinationZone = zone;
		this.isDestinationSet = true;
	}

	public Object getDestinationReference() {
		return destinationReference;
	}

	public String getDestinationZone() {
		return destinationZone;
	}

	/**
	 * Determine if this portal is hidden from players.
	 * 
	 * @return <code>true</code> if hidden.
	 */
	@Override
	public boolean isHidden() {
		return has(ATTR_HIDDEN);
	}

	public boolean loaded() {
		return isDestinationSet;
	}
	
	public void logic() {
	    // Sub-classes can implement this
	}

	@Override
	public String toString() {
		final StringBuilder sbuf = new StringBuilder();
		sbuf.append("Portal");

		final StendhalRPZone zone = getZone();

		if (zone != null) {
			sbuf.append(" at ");
			sbuf.append(zone.getName());
		}

		sbuf.append('[');
		sbuf.append(getX());
		sbuf.append(',');
		sbuf.append(getY());
		sbuf.append(']');

		if (isHidden()) {
			sbuf.append(", hidden");
		}

		return sbuf.toString();
	}

	/**
	 * Use the portal.
	 * 
	 * @param player
	 *            the Player who wants to use this portal
	 * @return <code>true</code> if the portal worked, <code>false</code>
	 *         otherwise.
	 */
	protected boolean usePortal(final Player player) {
		if (!player.isZoneChangeAllowed()) {
			player.sendPrivateText("For some reason you cannot get through right now.");
			return false;
		}

		if (!nextTo(player) && has("use")) {
			player.sendPrivateText("You must come closer before you can use this orb.");
			return false;
		}
		
		if (!nextTo(player)) {
			// Too far to use the portal from here, but walk to it
			// The pathfinder will do the rest of the work and make the player pass through the portal
			// Check that mouse movement is allowed first
			if (!player.getZone().isMoveToAllowed()) {
				player.sendPrivateText("Mouse movement is not possible here. Use your keyboard.");
			} else if (player.hasStatus(StatusType.POISONED)) {
				player.sendPrivateText("Poison has disoriented you and you cannot move normally. You only seem able to walk backwards and cannot plan out any route in advance.");
			} else {
				final List<Node> path = Path.searchPath(player, this.getX(), this.getY());
				player.setPath(new FixedPath(path, false));
			}
			return false;
		}

		if (getDestinationZone() == null) {
			// This portal is incomplete
			logger.error(this + " has no destination.");
			return false;
		}

		final StendhalRPZone destZone = SingletonRepository.getRPWorld().getZone(
				getDestinationZone());

		if (destZone == null) {
			logger.error(this + " has invalid destination zone: "
					+ getDestinationZone());
			return false;
		}

		final Portal dest = destZone.getPortal(getDestinationReference());

		if (dest == null) {
			// This portal is incomplete
			logger.error(this + " has invalid destination identitifer: "
					+ getDestinationReference());
			return false;
		}

		if (player.teleport(destZone, dest.getX(), dest.getY(), null, null)) {
			player.stop();
			dest.onUsedBackwards(player);
		}
		return true;
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user instanceof Player) {
			return usePortal((Player) user);
		} else {
			logger.error("user is no instance of Player but: " + user, new Throwable());
			return false;
		}
	}

	/**
	 * if this portal is the destination of another portal used.
	 * 
	 * @param user
	 *            the player who used the other portal teleporting to us
	 */
	public void onUsedBackwards(final RPEntity user) {
		// do nothing
	}
}
