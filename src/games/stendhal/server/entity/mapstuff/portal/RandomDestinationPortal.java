/***************************************************************************
 *                      (C) Copyright 2010 - Marauroa                      *
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * A portal that sends the using player to a randomly
 * chosen destination. Valid destinations are filtered
 * based on the player's level from a pre-defined list
 * of interesting places.
 */
public class RandomDestinationPortal extends QuestCompletedPortal {
	private static final String questslot = "learn_scrying";
	private static Logger logger = Logger.getLogger(RandomDestinationPortal.class);

	/**
	 * A class to store the location and suggested minimum level
	 * for a place.
	 */
	private static class Location {
		String zoneName;
		int level, x, y;
		Spot spot;

		/**
		 * Create a new <code>Location</code>.
		 *
		 * @param zoneName name of the zone of the teleportation destination
		 * @param level minimum level of a player to consider the location possible
		 * @param x x coordinate of the destination
		 * @param y y coordinate of the destination
		 */
		public Location(String zoneName, int level, int x, int y) {
			this.zoneName = zoneName;
			this.level = level;
			this.x = x;
			this.y = y;
		}

		/**
		 * Get the minimum level needed for the location
		 *
		 * @return minimum level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Get the spot represented by this location
		 *
		 * @return a teleport destination
		 */
		public Spot getSpot() {
			/*
			 * The spots need to be initialized lazily, instead
			 * of doing it in the constructor, because not all
			 * zones have been loaded by the time this portal is
			 * created.
			 */
			if (spot == null) {
				IRPZone zone = SingletonRepository.getRPWorld().getRPZone(zoneName);
				// All locations should be valid, but do not crash in case not all maps
				// are not loaded.
				if (zone != null) {
					spot = new Spot((StendhalRPZone) zone, x, y);
				} else {
					logger.error("Can not find zone: " + zoneName);
				}
			}

			return spot;
		}
	}

	/**
	 * A list of interesting locations
	 */
	private final List<Location> locations = new ArrayList<Location>();

	/**
	 * Create a new <code>RandomDestinationPortal</code>
	 */
	public RandomDestinationPortal() {
		super(questslot);
		initLocations();
	}

	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		/*
		 * A hack to silence StendhalRPWorld's pedantic portal checking. Point
		 * to the portal itself so that it has a valid destination
		 */
		setIdentifier(this);
		setDestination(zone.getName(), this);
	}

	/**
	 * Initialize the locations set
	 */
	private void initLocations() {
		locations.add(new Location("0_semos_mountain_n_w2", 10, 65, 116));	// gnome village
		locations.add(new Location("0_ados_outside_nw", 10, 52, 39)); 		// ados zoo
		locations.add(new Location("0_kirdneh_city", 15, 63, 26));			// kirdneh city
		locations.add(new Location("0_fado_forest", 20, 66, 35));			// fairy ring
		locations.add(new Location("0_orril_castle", 20, 62, 75));          // orril castle
		locations.add(new Location("int_ados_haunted_house", 25, 4, 27));	// haunted house
		locations.add(new Location("-2_orril_dungeon", 25, 106, 21));		// rat dungeon
		locations.add(new Location("-1_semos_mine_nw", 30, 22, 75));		// kobold city
		locations.add(new Location("0_athor_island", 30, 77, 73));          // athor island
        locations.add(new Location("-1_ados_wall", 30, 91, 62));			// ados sewers
        locations.add(new Location("-2_kotoch_entrance", 30, 20, 111));     // orc dungeons
        locations.add(new Location("0_nalwor_city", 60, 88, 85));           // nalwor city
        locations.add(new Location("-1_semos_yeti_cave", 60, 13, 39));		// yeti cave
        locations.add(new Location("-2_ados_outside_nw", 70, 28, 5));       // dwarfs/bario
        locations.add(new Location("0_ados_mountain_n2", 70,  52, 28));		// barbarian camp
        locations.add(new Location("1_kikareukin_cave", 70, 18, 97));		// 1 kika
        locations.add(new Location("-6_kanmararn_city", 70, 33, 52));       // kanmararn
		locations.add(new Location("-2_orril_lich_palace", 70, 67, 118));   // lich palace
		locations.add(new Location("-2_orril_dwarf_mine", 70, 50, 40));     // dwarf mine
		locations.add(new Location("0_amazon_island_nw", 90, 30, 30));      // amazon island
		locations.add(new Location("-1_ados_abandoned_keep", 90, 3, 103));  // abandoned keep
		locations.add(new Location("-1_fado_great_cave_n_e2", 120, 113, 19)); // magic city
		locations.add(new Location("int_kalavan_castle_basement", 150, 30,103)); // kalavan castle
		locations.add(new Location("int_oni_palace_1", 150, 28, 28));		// oni palace
		locations.add(new Location("-4_ados_abandoned_keep", 150, 11, 27));	// -4 abandoned keep
		locations.add(new Location("-2_semos_mine_w2", 150, 22, 39));		// balrog semos mines
		locations.add(new Location("-1_nalwor_drows_tunnel_n", 170, 58, 44)); // nalwor drow tunnel (main)
		locations.add(new Location("-6_ados_abandoned_keep", 170, 15, 19));	// -6 ados abandoned keep
		locations.add(new Location("int_mithrilbourgh_stores", 170, 6, 5));	// mithrilbourgh stores
		locations.add(new Location("hell", 200, 66, 77));					// hell
		locations.add(new Location("-1_fado_great_cave_w2", 200, 90, 57));  // sedah
		locations.add(new Location("4_kikareukin_cave", 200, 10, 10)); 		// 4 kika
		locations.add(new Location("5_kikareukin_cave", 200, 31, 100)); 	// 5 kika
		locations.add(new Location("-2_semos_mine_e2", 200, 4, 5));			// chaos mines
		locations.add(new Location("-1_ados_outside_w", 200, 33, 30)); 		// -1 ados wall
		locations.add(new Location("6_kikareukin_islands", 200, 10, 10));	// 6 kika
	}

	/**
	 * Get a random location to teleport a player.
	 *
	 * @param player the player using the portal
	 * @return a location the player is allowed to go to,
	 * or <code>null</code> if there are no appropriate places
	 */
	private Location getRandomLocation(Player player) {
		List<Location> allowed = getAllowedLocations(player);
		if (allowed.size() > 0) {
			return Rand.rand(getAllowedLocations(player));
		} else {
			return null;
		}
	}

	/**
	 * Get a list of locations appropriate for a player
	 *
	 * @param player the player using the portal
	 * @return list of locations
	 */
	private List<Location> getAllowedLocations(Player player) {
		List<Location> allowed = new LinkedList<Location>();
		int level = player.getLevel();
		for (Location l : locations) {
			if (level >= l.getLevel()) {
				allowed.add(l);
			}
		}

		return allowed;
	}

	/**
	 * Use the portal.
	 *
	 * @param player
	 *            the Player who wants to use this portal
	 * @return <code>true</code> if the portal worked, <code>false</code>
	 *         otherwise.
	 */
	@Override
	protected boolean usePortal(final Player player) {
		if (!nextTo(player)) {
			// Too far to use the portal
			player.sendPrivateText("You must stand nearer to use the orb.");
			return false;
		}

		Location location = getRandomLocation(player);

		// Would happen if the player was below the minimum level for any location
		if (location == null) {
			player.sendPrivateText("You can not concentrate well enough to use this orb.");
			return false;
		}

		Spot spot = getRandomLocation(player).getSpot();

		if (spot == null) {
			logger.error("Failed to determine destination spot.");
			return false;
		}

		if (player.teleport(spot.getZone(), spot.getX(), spot.getY(), null, null)) {
			player.stop();
		}

		return true;
	}
}
