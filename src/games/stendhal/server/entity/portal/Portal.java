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

import java.awt.geom.Rectangle2D;
import games.stendhal.server.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.StendhalRPAction;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;

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
			portal.add("hidden", RPClass.FLAG);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public Portal() throws AttributeNotFoundException {
		super();
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
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	@Override
	public String toString() {
		return "Portal at " + get("zoneid") + "[" + getX() + "," + getY() + "]";
	}

	protected void usePortal(Player player) {
		StendhalRPAction.usePortal(player, this);
	}

	public void onUsed(RPEntity user) {
		usePortal((Player) user);
	}

	public void onUsedBackwards(RPEntity user) {
		// do nothing
	}
}
