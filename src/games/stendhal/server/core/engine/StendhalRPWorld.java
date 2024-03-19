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
package games.stendhal.server.core.engine;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.WordList;
import games.stendhal.server.core.config.ZoneGroupsXMLLoader;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.server.game.rp.RPWorld;

public class StendhalRPWorld extends RPWorld {

	/** The logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPWorld.class);

	/** The singleton instance. */
	protected static StendhalRPWorld instance;

	public final static String MAPS_FOLDER;

	static {
		if (StendhalRPWorld.class.getClassLoader().getResource(
				"tiled/tileset/README") == null) {
			MAPS_FOLDER = "data/maps/";
		} else {
			logger.warn("Developing mode, loading maps from tiled/ instead of data/maps");
			MAPS_FOLDER = "tiled/";
		}
	}

	/**
	 * A common place for milliseconds per turn.
	 */
	public static final int MILLISECONDS_PER_TURN = 300;

	private final RPClassGenerator genRPClass = new RPClassGenerator();

	private final Map<String, Set<StendhalRPZone>> regionMap = new HashMap<String, Set<StendhalRPZone>>();


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static StendhalRPWorld get() {
		synchronized(StendhalRPWorld.class) {
			if (instance == null) {
				instance = new StendhalRPWorld();
				instance.initialize();
			}
		}

		return instance;
	}

	protected StendhalRPWorld() {
		super();
	}

	@Override
	public IRPZone removeRPZone(final ID zoneid) throws Exception {
		final StendhalRPZone zone = (StendhalRPZone) super.getRPZone(zoneid);
		for(final Set<StendhalRPZone> zones : regionMap.values()) {
			if(zones.contains(zone)) {
				zones.remove(zone);
			}
		}
		return super.removeRPZone(zoneid);
	}

	public void removeZone(final StendhalRPZone toBeRemoved) {
		try {
			removeRPZone(toBeRemoved.getID());
		} catch (final Exception e) {
			logger.error(e, e);
		}
	}


	@Override
	protected void initialize() {
		super.initialize();
		genRPClass.createRPClasses();
	}

	/**
	 * This method is a workaround for a groovy bug:
	 * https://jira.codehaus.org/browse/GROOVY-1484
	 *
	 * Don't use it in Java code, only in Groovy. Remove this method once the
	 * Groovy bug has been resolved.
	 *
	 * @return StendhalRPWorld
	 * @deprecated use {@link #get()}.
	 */
	@Deprecated
	public static StendhalRPWorld getInstance() {
		return SingletonRepository.getRPWorld();
	}

	/**
	 * Gives the number of turns that will take place during a given number of
	 * seconds.
	 *
	 * @param seconds
	 *            The number of seconds.
	 *
	 * @return The number of turns.
	 */
	public int getTurnsInSeconds(final int seconds) {
		return seconds * 10 / 3; // prevent overflows: 1000 / MILLISECONDS_PER_TURN;
	}



	@Override
	public void onInit() {
		try {
			super.onInit();

			// Create the NPC parser word list.
			WordList.getInstance();

			final ZoneGroupsXMLLoader loader = new ZoneGroupsXMLLoader(new URI(
					"/data/conf/zones.xml"));

			loader.load();

			validatePortals();
			SingletonRepository.getAchievementNotifier().initialize();
			SingletonRepository.getGagManager();
			SingletonRepository.getJail();
			/*
			if (System.getProperty("stendhal.testserver") != null) {
				SingletonRepository.getLoginNotifier().addListener(TutorialRunner.get());
				SingletonRepository.getLogoutNotifier().addListener(TutorialRunner.get());
			}
			*/
		} catch (final Exception e) {
			logger.error("Error on Init the server.", e);
		}
	}

	/**
	 * Checks for unpaired portals.
	 */
	private void validatePortals() {
		for (final IRPZone zone : this) {
			for (final Portal portal : ((StendhalRPZone) zone).getPortals()) {
				validatePortal(portal);
			}
		}
	}

	@Override
	public void onFinish() {
		super.onFinish();
		new GameEvent("server system", "shutdown").raise();
		try {
			//TODO: find a more appropriate way to do this
			// give gameevents a chance to be processed;
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			//do nothing
		}

	}

	protected void validatePortal(final Portal portal) {
		if (!portal.loaded()) {
			logger.warn(portal + " has no destination");
			return;
		}

		if (portal instanceof OneWayPortalDestination) {
			return;
		}

		final String id = portal.getDestinationZone();

		if (id == null) {
			logger.warn(portal + " has no destination zone");
			return;
		}

		final StendhalRPZone zone = getZone(id);

		if (zone == null) {
			logger.warn(portal + " has an invalid destination zone: " + id);
			return;
		}

		final Object ref = portal.getDestinationReference();

		if (ref == null) {
			logger.warn(portal + " has no destination reference");
			return;
		}

		if (zone.getPortal(ref) == null) {
			logger.warn(portal + " has an invalid destination reference: " + id
					+ "[" + ref + "]");
		}
	}

	public IRPZone getRPZone(final String zone) {
		return getRPZone(new IRPZone.ID(zone));
	}

	/**
	 * Finds a zone by its id.
	 *
	 * @param id
	 *            The zone's id
	 *
	 * @return The matching zone, or <code>null</code> if not found.
	 */
	public StendhalRPZone getZone(final String id) {
		return (StendhalRPZone) getRPZone(new IRPZone.ID(id));
	}

	/**
	 * Find the zone that would contain an entity at global coordinates.
	 *
	 * TODO: This is likely broken for entity larger than 2x2, because parts of
	 * them will exist in multiple zones (and not in collision)
	 *
	 * @param level
	 *            The level.
	 * @param wx
	 *            The global X coordinate.
	 * @param wy
	 *            The global Y coordinate.
	 * @param entity
	 *            The entity.
	 *
	 * @return The matching zone, or <code>null</code> if not found.
	 */
	public StendhalRPZone getZoneAt(final int level, final int wx, final int wy, final Entity entity) {
		for (final IRPZone izone : this) {
			final StendhalRPZone zone = (StendhalRPZone) izone;

			if (zone.isInterior()) {
				continue;
			}

			if (zone.getLevel() != level) {
				continue;
			}

			if (zone.intersects(entity.getArea(wx, wy))) {
				logger.debug("Contained at :" + zone.getID());
				return zone;
			}
		}

		return null;
	}

	/**
	 * Adds a zone to a certain region in this world
	 * @param region
	 * @param zone
	 */
	public void addRPZone(final String region, final StendhalRPZone zone) {
		super.addRPZone(zone);
		if(!regionMap.containsKey(region)) {
			regionMap.put(region, new HashSet<StendhalRPZone>());
		}
		regionMap.get(region).add(zone);
	}

	public TreeSet<String> getRegions() {
		// Since we need to make a copy to protect the internal structure,
		// we use a TreeSet for alphabetical ordering.
		return new TreeSet<String>(regionMap.keySet());
	}

	/**
	 * Retrieves all zones from a specified region with the given flags
	 *
	 * @param region the name of the region to search for
	 * @param exterior only exterior zones(true), interior zones(false) or all zones (null)
	 * @param aboveGround only zones above ground(true), zones below ground(false) or all (null)
	 * @param accessible use true to filter out zones that are not accessible for everyone
	 * @return a list of zones
	 */
	public Collection<StendhalRPZone> getAllZonesFromRegion(final String region, final Boolean exterior, final Boolean aboveGround, final Boolean accessible) {
		final Set<StendhalRPZone> zonesInRegion = new HashSet<StendhalRPZone>();
		if(regionMap.containsKey(region)) {
			zonesInRegion.addAll(regionMap.get(region));
			if(exterior != null) {
				filterOutInteriorOrExteriorZones(zonesInRegion, exterior);
			}
			if(aboveGround != null && exterior != null && exterior) {
				filterOutAboveOrBelowGround(zonesInRegion, aboveGround);
			}
			if(accessible != null) {
				filterByAccessibility(zonesInRegion, accessible);
			}
		}
		return zonesInRegion;
	}

	/**
	 * Filter out exterior or interior zones from the given set
	 * @param zonesInRegion the set to filter
	 * @param exterior if true only exterior zones stay in the set
	 */
	private void filterOutInteriorOrExteriorZones(final Set<StendhalRPZone> zonesInRegion, final Boolean exterior) {
		final Set<StendhalRPZone> removals = new HashSet<StendhalRPZone>();
		for (StendhalRPZone zone : zonesInRegion) {
			if(exterior) {
				if(zone.isInterior()) {
					removals.add(zone);
				}
			} else {
				if(!zone.isInterior()) {
					removals.add(zone);
				}
			}
		}
		zonesInRegion.removeAll(removals);
	}

	/**
	 * Filter out zones above or below ground
	 *
	 * @param zonesInRegion
	 * @param aboveGround
	 */
	private void filterOutAboveOrBelowGround(final Set<StendhalRPZone> zonesInRegion, final Boolean aboveGround) {
		final Set<StendhalRPZone> removals = new HashSet<StendhalRPZone>();
		for (StendhalRPZone zone : zonesInRegion) {
			if(aboveGround.booleanValue()) {
				if(zone.getLevel() < 0) {
					removals.add(zone);
				}
			} else {
				if(zone.getLevel() >= 0) {
					removals.add(zone);
				}
			}
		}
		zonesInRegion.removeAll(removals);
	}

	private void filterByAccessibility(final Set<StendhalRPZone> zonesInRegion, final Boolean accessible) {
		final Set<StendhalRPZone> removals = new HashSet<StendhalRPZone>();
		for (StendhalRPZone zone : zonesInRegion) {
			boolean addToRemovals = false;
			if(accessible.booleanValue()) {
				addToRemovals = !zone.isPublicAccessible();
			} else {
				addToRemovals = zone.isPublicAccessible();
			}
			if (addToRemovals) {
				removals.add(zone);
			}
		}
		zonesInRegion.removeAll(removals);
	}
}
