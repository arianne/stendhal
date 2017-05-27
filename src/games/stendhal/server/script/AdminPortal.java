/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.mapstuff.portal.LevelCheckingPortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;

/**
 * Enables admins to create portals.
 *
 * @author hendrik
 */
public class AdminPortal extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() >= 2 && args.size() != 4) {
			try {
				createPortal(admin, args);
			} catch (RuntimeException e) {
				admin.sendPrivateText(e.toString());
			}
		} else {
			// syntax error, print help text
			sandbox.privateText(
					admin,
					"This script creates portals:\n" +
					"/script AdminPortal.class <destination-zone> <destination-ref>\n" +
					"/script AdminPortal.class <name> <destination-zone> <destination-ref>\n" +
					"/script AdminPortal.class <name> <destination-zone> <destination-ref> level <min-level> <max-level> [<reject-message>]");
		}
	}

	/**
	 * creates a portal
	 *
	 * @param admin the admin to send errors to
	 * @param args arguments
	 */
	private void createPortal(final Player admin, final List<String> args) {
		sandbox.setZone(sandbox.getZone(admin));
		int x = admin.getX();
		int y = admin.getY();

		Portal portal = instantiatePortal(args);
		setPortalName(args, portal);
		portal.setPosition(x, y);
		int destinationOffset = getDestinationOffset(args);
		portal.setDestination(args.get(destinationOffset), args.get(destinationOffset + 1));

		// add entity to game
		sandbox.add(portal);
	}

	/**
	 * gets the index offset for the destination
	 *
	 * @param args arguments
	 * @return 0 if there is no name, 1 otherwise
	 */
	private int getDestinationOffset(List<String> args) {
		if (args.size() > 2) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * instantiates a portal based on its type
	 *
	 * @param args arguments
	 * @return Portal or subclass of Portal
	 */
	private Portal instantiatePortal(List<String> args) {
		if (args.size() < 4) {
			return new Portal();
		} else if (args.get(3).equals("level")) {
			String rejectMessage = null;
			if (args.size() == 7) {
				rejectMessage = args.get(6);
			}
			return new LevelCheckingPortal(Integer.parseInt(args.get(4)), Integer.parseInt(args.get(5)), rejectMessage);
		}
		throw new IllegalArgumentException("Invalid portal type.");
	}

	/**
	 * sets the portal name if it is specified
	 *
	 * @param args arguments
	 * @param portal portal to set the name on
	 */
	private void setPortalName(final List<String> args, Portal portal) {
		if (args.size() > 2) {
			portal.setIdentifier(args.get(0));
		}
	}
}
