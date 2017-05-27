/* $Id$ */
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
package games.stendhal.server.script;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * Searches for inconsistencies in zone collisions. For every square
 * that there is an open way out, should be also an open square in
 * the neighbouring zone.
 * <p>
 * No attention paid to performance. There should be no reason to run
 * this on anything but a test server.
 */
public class ZoneCollisionCheck extends ScriptImpl {
	private static enum Border {
		NORTH {
			@Override
			public Border opposite() {
				return SOUTH;
			}
		},
		EAST {
			@Override
			public Border opposite() {
				return WEST;
			}
		},
		SOUTH {
			@Override
			public Border opposite() {
				return NORTH;
			}
		},
		WEST {
			@Override
			public Border opposite() {
				return EAST;
			}
		};


		public abstract Border opposite();
	}

	private Entity entity;
	private Player admin;
	private int badnessThreshold = 1;

	@Override
	public void execute(final Player admin, final List<String> args) {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		this.admin = admin;

		entity = new Entity() {
			// anon subclass to allow instantiation.
		};

		if (args.size() > 1) {
			usage();
			return;
		} else if (args.size() == 1) {
			String arg = args.get(0);
			try {
				badnessThreshold = Integer.parseInt(arg);
			} catch (NumberFormatException e) {
				admin.sendPrivateText("Invalid number: " + arg);
				usage();
				return;
			}
		}

		for (IRPZone izone : world) {
			StendhalRPZone zone = (StendhalRPZone) izone;
			checkZone(zone);
		}
	}

	private void usage() {
		admin.sendPrivateText("Usage: /script ZoneCollisionCheck.class [badness]\n\tBadness parameter is the minimum number of failing checks next to each other");
	}

	private void checkZone(StendhalRPZone zone) {
		for (Border border : Border.values()) {
			report(checkBorder(zone, border));
		}
	}

	/**
	 * Check a border
	 * @param zone the to check
	 * @param border the border to check
	 * @return list of the found problems
	 */
	private List<String> checkBorder(StendhalRPZone zone, Border border) {
		LinkedList<String> problems = new LinkedList<String>();

		// the coordinates to check in zone
		int zoneX = 0;
		int zoneY = 0;

		// walking direction
		int dx = 0;
		int dy = 0;

		// for finding the neighbour
		int tmpx = zone.getX();
		int tmpy = zone.getY();
		switch (border) {
		case NORTH:
			tmpy--;
			dx = 1;
			break;
		case EAST:
			tmpx += zone.getWidth();
			zoneX = zone.getWidth() - 1;
			dy = 1;
			break;
		case SOUTH:
			tmpy += zone.getHeight();
			zoneY = zone.getHeight() - 1;
			dx = 1;
			break;
		case WEST:
			dy = 1;
			tmpx--;
		}

		final StendhalRPZone neighbour = SingletonRepository.getRPWorld().getZoneAt(
				zone.getLevel(), tmpx, tmpy, entity);
		if (neighbour != null) {
			// find the starting coordinates for neighbour
			int neighbourX = 0;
			int neighbourY = 0;

			switch (border.opposite()) {
			case NORTH:
				neighbourX = zone.getX() - neighbour.getX();
				neighbourY = 0;
				break;
			case EAST:
				neighbourX = neighbour.getWidth() - 1;
				neighbourY = zone.getY() - neighbour.getY();
				break;
			case SOUTH:
				neighbourX = zone.getX() - neighbour.getX();
				neighbourY = neighbour.getHeight() - 1;
				break;
			case WEST:
				neighbourX = 0;
				neighbourY = zone.getY() - neighbour.getY();
			}

			// Walk through the border and check do the collisions match
			int badness = 0;
			while ((zoneX < zone.getWidth()) && (zoneY < zone.getHeight())) {
				if ((neighbourX < 0) || (neighbourY < 0)) {
					// haven't yet reached the point where to start checking
					continue;
				}
				if ((neighbourX >= neighbour.getWidth()) || (neighbourY >= neighbour.getHeight())) {
					// done checking all of the border
					break;
				}

				boolean zCollides = zone.collides(zoneX, zoneY);
				boolean nCollides = neighbour.collides(neighbourX, neighbourY);

				if (zCollides != nCollides) {
					badness++;
					if (badness >= badnessThreshold) {
						problems.add(collidesMessage(zone.getName(), zoneX, zoneY, zCollides)
								+ " but " +
								collidesMessage(neighbour.getName(), neighbourX, neighbourY, nCollides));
					}
				} else {
					badness = 0;
				}

				zoneX += dx;
				zoneY += dy;
				neighbourX += dx;
				neighbourY += dy;
			}
		}

		return problems;
	}

	private String collidesMessage(String zone, int x, int y, boolean collides) {
		if (collides) {
			return zone + " has collision at [" + x + "," + y + "]";
		} else {
			return zone + " does not have collision at [" + x + "," + y + "]";
		}
	}

	/**
	 * Send a problem report to the admin.
	 *
	 * @param problems the problems to include in this report
	 */
	private void report(List<String> problems) {
		if (!problems.isEmpty()) {
			StringBuilder msg = new StringBuilder();
			for (String problem : problems) {
				msg.append(problem);
				msg.append("\n");
			}

			admin.sendPrivateText(msg.toString());
		}
	}
}
