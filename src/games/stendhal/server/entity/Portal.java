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
package games.stendhal.server.entity;

import java.awt.geom.Rectangle2D;
import games.stendhal.server.events.UseEvent;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.StendhalRPAction;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import org.apache.log4j.Logger;

public class Portal extends Entity implements UseEvent {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Portal.class);

	private boolean settedDestination;

	private int number;

	private String destinationZone;

	private int destinationNumber;

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

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setDestination(String zone, int number) {
		this.destinationNumber = number;
		this.destinationZone = zone;
		this.settedDestination = true;
	}

	public int getDestinationNumber() {
		return destinationNumber;
	}

	public String getDestinationZone() {
		return destinationZone;
	}

	public boolean loaded() {
		return settedDestination;
	}

	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	public String toString() {
		return "Portal at " + get("zoneid") + "[" + getx() + "," + gety() + "]";
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		if (StendhalRPAction.usePortal(player, this)) {
			StendhalRPAction.transferContent(player);
			world.modify(player);
		}
	}
}
