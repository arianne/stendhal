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
package games.stendhal.server.maps.quests.houses;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;

public class HouseUtilities {
	private static List<HousePortal> allHousePortals = null;
	private static final String HOUSE_QUEST_SLOT = "house";
	private static final Logger logger = Logger.getLogger(HouseUtilities.class);
	private static final String[] zoneNames = {
		"0_kalavan_city",
		"0_kirdneh_city",
		"0_ados_city_n",
		"0_ados_city",
		"0_ados_city_s",
		"0_ados_wall",
		"0_athor_island"
	};

	private HouseUtilities() {
		// hide constructor, this is a static class
	}

	/**
	 * clears the house cache
	 */
	public static void clearCache() {
		 allHousePortals = null;
	}

	/**
	 * Get the house owned by a player.
	 *
	 * @param player the player to be examined
	 * @return portal to the house owned by the player, or <code>null</code>
	 * if he does not own one.
	 */
	protected static HousePortal getPlayersHouse(final Player player) {
		if (player.hasQuest(HOUSE_QUEST_SLOT)) {
			final String claimedHouse = player.getQuest(HOUSE_QUEST_SLOT);

			try {
				final int id = Integer.parseInt(claimedHouse);
				final HousePortal portal = getHousePortal(id);

				if (portal != null) {
					if (player.getName().equals(portal.getOwner())) {
						return portal;
					}
				} else {
					logger.error("Player " + player.getName() + " claims to own a nonexistent house " + id);
				}
			} catch (final NumberFormatException e) {
				logger.error("Invalid number in house slot", e);
			}
		}

		return null;
	}

	/**
	 * Check if a player owns a house.
	 *
	 * @param player the player to be checked
	 * @return <code>true</code> if the player owns a house, false otherwise
	 */
	protected static boolean playerOwnsHouse(final Player player) {
		return (getPlayersHouse(player) != null);
	}

	/**
	 * Find a portal corresponding to a house number.
	 *
	 * @param houseNumber the house number to find
	 * @return the portal to the house, or <code>null</code> if there is no
	 * house by number <code>id</code>
	 */
	protected static HousePortal getHousePortal(final int houseNumber) {
		final List<HousePortal> portals = getHousePortals();

		for (final HousePortal houseportal : portals) {
			final int number = houseportal.getPortalNumber();
			if (number == houseNumber) {
				return houseportal;
			}
		}

		// if we got this far, we didn't find a match
		// (triggered by AdosHouseSellerTest.testAdosHouseSellerNoZones)
		logger.error("getHousePortal was given a number (" + Integer.toString(houseNumber) + ") it couldn't match a house portal for");
		return null;
	}

	/**
	 * Get a list of all house portals available to players.
	 *
	 * @return list of all house portals
	 */
	protected static List<HousePortal> getHousePortals() {
		if (allHousePortals == null) {
			// this is only done once per server run
			List<HousePortal> tempAllHousePortals = new LinkedList<HousePortal>();

			for (final String zoneName : zoneNames) {
				final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneName);
				if (zone == null) {
					logger.warn("Could not find zone " + zoneName);
				} else {
					for (final Portal portal : zone.getPortals()) {
						if (portal instanceof HousePortal) {
							tempAllHousePortals.add((HousePortal) portal);
						}
					}
				}
			}
			allHousePortals = tempAllHousePortals;
		}
		final int size = allHousePortals.size();
		logger.debug("Number of house portals in world is " + Integer.toString(size));

		return allHousePortals;
	}

	/**
	 * Find a chest corresponding to a house portal.
	 *
	 * @param portal the house portal of the house containing the chest we want to find
	 * @return the chest in the house, or <code>null</code> if there is no
	 * chest in the zone which the house portal leads to (Note, then, that chests should be on the 'ground floor')
	 */

	protected static StoredChest findChest(final HousePortal portal) {
		final String zoneName = portal.getDestinationZone();
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneName);

		final List<Entity> chests = zone.getFilteredEntities(new FilterCriteria<Entity>() {
			@Override
			public boolean passes(final Entity object) {
				return (object instanceof StoredChest);
			}
		});

		if (chests.size() != 1) {
			logger.error(chests.size() + " chests in " + portal.getDoorId());
			return null;
		}

		return (StoredChest) chests.get(0);
	}

	// this will be ideal for a seller to list all unbought houses
	// using Grammar.enumerateCollection
	private static List<String> getUnboughtHouses() {
	    final List<String> unbought = new LinkedList<String>();
		final List<HousePortal> portals =  getHousePortals();
		for (final HousePortal houseportal : portals) {
			final String owner = houseportal.getOwner();
			if (owner.length() == 0) {
				unbought.add(houseportal.getDoorId());
			}
		}
		return unbought;
	}

	// this will be ideal for a seller to list all unbought houses in a specific location
	// using Grammar.enumerateCollection
	protected static List<String> getUnboughtHousesInLocation(final String location) {
		final String regex = location + ".*";
		final List<String> unbought = new LinkedList<String>();
		for (final String doorId : getUnboughtHouses()) {
			if (doorId.matches(regex)) {
				unbought.add(doorId);
			}
		}
		return unbought;
	}

}
