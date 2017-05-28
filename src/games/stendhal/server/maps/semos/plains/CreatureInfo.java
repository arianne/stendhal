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
package games.stendhal.server.maps.semos.plains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.IRPZone;

public class CreatureInfo {

	private final Map<Double, String> probabilityLiterals;

	private final Map<Integer, String> amountLiterals;

	private final Map<Double, String> dangerLiterals;

	private final String[] lineStartTexts;

	private final String[] respawnTexts;

	private final String[] carryTexts;

	private final String[] carryNothingTexts;

	private final String[] locationTexts;

	private final String[] locationUnknownTexts;

	/**
	 * The zonenames that are not listed as the creature locations.
	 */
	private static final List<String> skippedZoneNames = Arrays.asList(new String[] {
			"int_admin_playground", "int_pathfinding" });

	/**
	 * Create an instance of creatureInfo.
	 *
	 * @param probabilityLiterals
	 *            templates for probabilities. %s is replaced with item
	 *            description (name and amount)
	 * @param amountLiterals
	 *            templates for item amounts. %s is replaced with singular item
	 *            name, %a with "a/an item name" depending on the item name
	 * @param dangerLiterals
	 *            templates for how dangerous a creature is based on the
	 *            percentual difference to player level. %s is replaced with
	 *            singular creature name, %S with plural
	 * @param lineStartTexts
	 *            templates for line starts. %s is replaced with singular
	 *            creature name, %S plural
	 * @param respawnTexts
	 *            templates for respawn texts. %1 = time to respawn.
	 * @param carryTexts
	 *            templates for drops. %1 = list of items dropped.
	 * @param carryNothingTexts
	 *            templates for no drops.
	 * @param locationTexts
	 *            templates for creature locations. %1 = list of locations
	 * @param locationUnknownTexts
	 *            templates for unknown location. %1 = name of the creature
	 */
	public CreatureInfo(final Map<Double, String> probabilityLiterals,
			final Map<Integer, String> amountLiterals,
			final Map<Double, String> dangerLiterals, final String[] lineStartTexts,
			final String[] respawnTexts, final String[] carryTexts,
			final String[] carryNothingTexts, final String[] locationTexts,
			final String[] locationUnknownTexts) {
		this.probabilityLiterals = probabilityLiterals;
		this.amountLiterals = amountLiterals;
		this.dangerLiterals = dangerLiterals;
		this.lineStartTexts = lineStartTexts.clone();
		this.respawnTexts = respawnTexts.clone();
		this.carryTexts = carryTexts.clone();
		this.carryNothingTexts = carryNothingTexts.clone();
		this.locationTexts = locationTexts.clone();
		this.locationUnknownTexts = locationUnknownTexts.clone();
	}

	/**
	 * get information on a creature.
	 *
	 * @param player
	 * @param creature
	 * @param maxLocations
	 *            max number of locations listed
	 * @param maxDrops
	 * @param respawn
	 * @return string containing creature information
	 */
	public String getCreatureInfo(final Player player,
			final DefaultCreature creature, final int maxLocations, final int maxDrops,
			final boolean respawn) {
		if (player == null) {
			throw new IllegalArgumentException("player is null");
		}
		if (creature == null) {
			throw new IllegalArgumentException("creature is null");
		}
		String dropInfo;
		if (maxDrops <= 0) {
			dropInfo = "";
		} else {
			dropInfo = getDropItemsInfo(creature,
					maxDrops);
		}
		String locationInfo;
		if (maxLocations <= 0) {
			locationInfo = "";
		} else {
			locationInfo = getLocationInfo(
					creature.getCreatureName(), maxLocations);
		}
		String respawnInfo;
		if (respawn) {
			respawnInfo = getRespawnInfo(creature);
		} else {
			respawnInfo = "";
		}
		StringBuilder result = new StringBuilder(getCreatureBasicInfo(player, creature));
		if (respawn) {
			if (respawnInfo != null && respawnInfo.length() > 0) {
				result.append(getRandomString(respawnTexts, respawnInfo)).append(' ');
			}
		}
		if (maxDrops > 0) {
			if (dropInfo != null && dropInfo.length() > 0) {
				result.append(getRandomString(carryTexts, dropInfo)).append(' ');
			} else {
				result.append(getRandomString(carryNothingTexts)).append(' ');
			}
		}
		if (maxLocations > 0) {
			if (locationInfo != null && locationInfo.length() > 0) {
				result.append(getRandomString(locationTexts, locationInfo)).append(' ');
			} else {
				result.append(getRandomString(locationUnknownTexts,
						Grammar.a_noun(creature.getCreatureName()))).append(' ');
			}
		}
		return result.toString();
	}

	/**
	 * get the approximate respawn time of a creature.
	 *
	 * @param creature
	 * @return a string representing the next respawntime of a creature
	 */
	private String getRespawnInfo(final DefaultCreature creature) {
		return TimeUtil.approxTimeUntil((int) (creature.getRespawnTime() * 0.3));
	}

	/**
	 * get creature respawn locations.
	 *
	 * @param creatureName
	 * @param maxNumberOfLocations
	 *            how many (most frequent) respawn locations are listed
	 * @return a string containing the names of the zones
	 */
	private String getLocationInfo(final String creatureName, final int maxNumberOfLocations) {
		String prefix = "";
		final Map<String, Integer> zoneCounts = getCreatureZoneCounts(creatureName);
		final Set<String> places = new HashSet<String>();
		int counter = 0;
		for (final Map.Entry<String, Integer> entry : zoneCounts.entrySet()) {
			if (!skippedZoneNames.contains(entry.getKey())) {
				final String placeName = StendhalRPZone.describe(entry.getKey());
				if (placeName != null && placeName.length() > 0) {
					places.add(placeName);
				}
				if (++counter >= maxNumberOfLocations) {
					prefix = "for example ";
					break;
				}
			}
		}
		if (places.isEmpty()) {
			return "";
		} else {
			return prefix + " "
					+ Grammar.enumerateCollection(places);
		}
	}

	/**
	 * get the count of specified creature for all zones.
	 *
	 * @param creatureName
	 * @return map of zonenames with creature counts
	 */
	public Map<String, Integer> getCreatureZoneCounts(final String creatureName) {
		final Map<String, Integer> zoneCounts = new HashMap<String, Integer>();

		/* count creatures for each zone */
		for (final IRPZone zone : SingletonRepository.getRPWorld()) {
			for (final CreatureRespawnPoint p : ((StendhalRPZone) zone).getRespawnPointList()) {
				final Creature c = p.getPrototypeCreature();
				if (creatureName.equals(c.getName())) {
					final String zoneName = zone.getID().getID();
					if (zoneCounts.containsKey(zoneName)) {
						zoneCounts.put(zoneName, zoneCounts.get(zoneName) + 1);
					} else {
						zoneCounts.put(zoneName, 1);
					}
				}
			}
		}
		return zoneCounts;
	}

	/**
	 * Get basic information of the creature: initial string + how dangerous the
	 * creature is.
	 *
	 * @param player
	 * @param creature
	 * @return basic information about the creature.
	 */
	public String getCreatureBasicInfo(final Player player,
			final DefaultCreature creature) {
		if (creature == null) {
			throw new IllegalArgumentException("creature is null");
		}
		String result = lineStartTexts[(int) (Math.random() * lineStartTexts.length)];
		result = result.replaceAll("%s", creature.getCreatureName());
		result = result.replaceAll("%S",
				Grammar.plural(creature.getCreatureName()));
		result = result.replaceAll("%a",
				Grammar.a_noun(creature.getCreatureName()))
				+ " ";
		result += getHowDangerous(player, creature, dangerLiterals) + " ";
		return result;
	}

	/**
	 * Get verbal presentation of how dangerous the creature is to the player.
	 *
	 * @param player
	 * @param creature
	 * @param dangerLiterals
	 *            lookup table (map) for texts: formula for key value is
	 *            creature level / player level. Text replacements: %s is
	 *            replaced with creature name; %S is replaced with plural
	 *            creature name.
	 * @return verbal presentation of how dangerous the creature is to player.
	 */
	public String getHowDangerous(final Player player, final DefaultCreature creature,
			final Map<Double, String> dangerLiterals) {
		String s = getLiteral(dangerLiterals,
				((double) creature.getLevel())
						/ ((double) player.getLevel()), 0.0);
		s = s.replaceAll("%s", creature.getCreatureName());
		s = s.replaceAll("%S", Grammar.plural(creature.getCreatureName()));
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * Get verbal presentation of the items dropped by given creature.
	 *
	 * @param creature
	 * @param maxNumberOfItems
	 *            maximum number of items with most frequent drops first
	 * @return string of the items dropped by given creature.
	 */
	private String getDropItemsInfo(final DefaultCreature creature,
			final int maxNumberOfItems) {
		final StringBuilder result = new StringBuilder();
		final List<DropItem> dropItems = creature.getDropItems();
		Collections.sort(dropItems, new Comparator<DropItem>() {
			@Override
			public int compare(final DropItem o1, final DropItem o2) {
				if (o1.probability < o2.probability) {
					return 1;
				} else if (o1.probability > o2.probability) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		List<String> items = new ArrayList<String>();
		String prevProbability = null;
		int counter = 0;
		String prefix = "";
		for (final DropItem item : dropItems) {
			final String probability = getLiteral(probabilityLiterals, item.probability, 0.0);

			if (prevProbability != null && !probability.equals(prevProbability)) {
				if (result.length() > 0) {
					result.append(", ");
				}

				prevProbability = prevProbability.replaceAll("%s",
						Grammar.enumerateCollection(items));
				prevProbability = prevProbability.replaceAll("%a",
						Grammar.a_noun(item.name));
				result.append(prevProbability);
				items = new ArrayList<String>();
			}
			String s = getLiteral(amountLiterals, item.max, 1);
			s = s.replaceAll("%s", item.name);
			s = s.replaceAll("%a", Grammar.a_noun(item.name));
			items.add(s);
			prevProbability = probability;
			if (++counter >= maxNumberOfItems) {
				prefix = "for example ";
				break;
			}
		}

		if (result.length() > 0) {
			result.append(", ");
		}
		if (prevProbability != null) {
			result.append(prevProbability.replaceAll("%s",
					Grammar.enumerateCollection(items)));
		}

		return (prefix + result.toString()).replaceAll("_", " ");
	}

	/**
	 * Utility method for returning a random string from array of strings.
	 *
	 * @param texts
	 * @param params
	 * @return string chosen
	 */
	private String getRandomString(final String[] texts, final String... params) {
		String result = Rand.rand(texts);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				result = result.replaceAll("%" + (i + 1), params[i]);
			}
		}
		return result;
	}

	/**
	 * utility method for turning numeric value into a literal string from a
	 * list.
	 * @param <T>
	 *
	 * @param literals
	 *            map of the literal strings
	 * @param val
	 *            the numeric value
	 * @param defValue
	 *            numeric value if given value is out of bounds
	 * @return a literal string
	 */
	private <T extends Number> String getLiteral(final Map<T, String> literals,
			final T val, final T defValue) {
		String result = literals.get(defValue);
		for (final Entry<T, String> entry  : literals.entrySet()) {
			if (entry.getKey().doubleValue() <= val.doubleValue()) {
				result = entry.getValue();
				break;
			}
		}
		return result;
	}
}
