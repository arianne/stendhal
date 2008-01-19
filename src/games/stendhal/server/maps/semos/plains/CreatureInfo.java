package games.stendhal.server.maps.semos.plains;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * Mappings for the zone names that would look weird with the dynamic
	 * translation.
	 */
	private static final Map<String, String> zoneNameMappings = new HashMap<String, String>();

	static {
		zoneNameMappings.put("0_athor_ship_w2", "Athor ferry");
		zoneNameMappings.put("-1_athor_ship_w2", "Athor ferry");
		zoneNameMappings.put("-2_athor_ship_w2", "Athor ferry");
	}

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
	public CreatureInfo(Map<Double, String> probabilityLiterals,
			Map<Integer, String> amountLiterals,
			Map<Double, String> dangerLiterals, String[] lineStartTexts,
			String[] respawnTexts, String[] carryTexts,
			String[] carryNothingTexts, String[] locationTexts,
			String[] locationUnknownTexts) {
		this.probabilityLiterals = probabilityLiterals;
		this.amountLiterals = amountLiterals;
		this.dangerLiterals = dangerLiterals;
		this.lineStartTexts = lineStartTexts;
		this.respawnTexts = respawnTexts;
		this.carryTexts = carryTexts;
		this.carryNothingTexts = carryNothingTexts;
		this.locationTexts = locationTexts;
		this.locationUnknownTexts = locationUnknownTexts;
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
			final DefaultCreature creature, int maxLocations, int maxDrops,
			boolean respawn) {
		if (player == null) {
			throw new IllegalArgumentException("player is null");
		}
		if (creature == null) {
			throw new IllegalArgumentException("creature is null");
		}
		String dropInfo = maxDrops <= 0 ? "" : getDropItemsInfo(creature,
				maxDrops);
		String locationInfo = maxLocations <= 0 ? "" : getLocationInfo(
				creature.getCreatureName(), maxLocations);
		String respawnInfo = !respawn ? "" : getRespawnInfo(creature);
		String result = getCreatureBasicInfo(player, creature);
		if (respawn) {
			if (respawnInfo != null && respawnInfo.length() > 0) {
				result += getRandomString(respawnTexts, respawnInfo) + " ";
			}
		}
		if (maxDrops > 0) {
			if (dropInfo != null && dropInfo.length() > 0) {
				result += getRandomString(carryTexts, dropInfo) + " ";
			} else {
				result += getRandomString(carryNothingTexts) + " ";
			}
		}
		if (maxLocations > 0) {
			if (locationInfo != null && locationInfo.length() > 0) {
				result += getRandomString(locationTexts, locationInfo) + " ";
			} else {
				result += getRandomString(locationUnknownTexts,
						Grammar.a_noun(creature.getCreatureName()))
						+ " ";
			}
		}
		return result;
	}

	/**
	 * get the approximate respawn time of a creature.
	 *
	 * @param creature
	 * @return a string representing the next respawntime of a creature
	 */
	private String getRespawnInfo(DefaultCreature creature) {
		return TimeUtil.approxTimeUntil((int) (creature.getRespawnTime() * 0.3));
	}

	/**
	 * get creature respawn locations.
	 *
	 * @param creatureName
	 * @param maxNumberOfLocations
	 *            how many (most frequent) respawn locations are listed
	 * @return
	 */
	private String getLocationInfo(String creatureName, int maxNumberOfLocations) {
		String prefix = "";
		Map<String, Integer> zoneCounts = getCreatureZoneCounts(creatureName);
		Set<String> places = new HashSet<String>();
		int counter = 0;
		for (Map.Entry<String, Integer> entry : zoneCounts.entrySet()) {
			if (!skippedZoneNames.contains(entry.getKey())) {
				String placeName = translateZoneName(entry.getKey());
				if (placeName != null && placeName.length() > 0) {
					places.add(placeName);
				}
				if (++counter >= maxNumberOfLocations) {
					prefix = "for example ";
					break;
				}
			}
		}
		return places.size() == 0 ? "" : prefix + "at "
				+ Grammar.enumerateCollection(places);
	}

	/**
	 * get the count of specified creature for all zones.
	 *
	 * @param creatureName
	 * @return map of zonenames with creature counts
	 */
	public Map<String, Integer> getCreatureZoneCounts(String creatureName) {
		Map<String, Integer> zoneCounts = new HashMap<String, Integer>();

		/* count creatures for each zone */
		for (IRPZone zone : StendhalRPWorld.get()) {
			for (CreatureRespawnPoint p : ((StendhalRPZone) zone).getRespawnPointList()) {
				Creature c = p.getPrototypeCreature();
				if (creatureName.equals(c.getName())) {
					String zoneName = zone.getID().getID();
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
	 * Translate zone name into a more readable form.
	 *
	 * @param zoneName
	 * @return translated zone name
	 */
	private static String translateZoneName(String zoneName) {
		if (zoneNameMappings.get(zoneName) != null) {
			return zoneNameMappings.get(zoneName);
		}
		String result = "";
		Pattern p = Pattern.compile("^(-?[\\d]|int)_(.+)$");
		Matcher m = p.matcher(zoneName);
		if (m.matches()) {
			String level = m.group(1);
			String remainder = m.group(2);
			if ("int".equals(level)) {
				return "inside a building in " + getInteriorName(zoneName);
			} else if (level.startsWith("-")) {
				int levelValue = 0;
				try {
					levelValue = Integer.parseInt(level);
				} catch (Exception e) {
					/* do nothing */
				}
				result = levelValue < -2 ? "deep below ground level at "
						: "below ground level at ";
			} else if (level.matches("^\\d")) { /* positive floor */
				int levelValue = 0;
				try {
					levelValue = Integer.parseInt(level);
				} catch (Exception e) {
					/* do nothing */
				}
				if (levelValue != 0) {
					result = levelValue > 1 ? "high above the ground level at "
							: "above the ground level at ";
				}
			}
			String direction = "";
			String[] directions = new String[] { ".+_n\\d?e\\d?($|_).*",
					"north east ", "_n\\d?e\\d?($|_)", "_",
					".+_n\\d?w\\d?($|_).*", "north west ", "_n\\d?w\\d?($|_)",
					"_", ".+_s\\d?e\\d?($|_).*", "south east ",
					"_s\\d?e\\d?($|_)", "_", ".+_s\\d?w\\d?($|_).*",
					"south west ", "_s\\d?w\\d?($|_)", "_", ".+_n\\d?($|_).*",
					"north ", "_n\\d?($|_)", "_", ".+_s\\d?($|_).*", "south ",
					"_s\\d?($|_)", "_", ".+_w\\d?($|_).*", "west ",
					"_w\\d?($|_)", "_", ".+_e\\d?($|_).*", "east ",
					"_e\\d?($|_)", "_", };
			for (int i = 0; i < directions.length; i += 4) {
				if (remainder.matches(directions[i])) {
					direction += directions[i + 1];
					remainder = remainder.replaceAll(directions[i + 2],
							directions[i + 3]);
				}
			}
			if (direction.length() > 0) {
				result += direction + "of ";
			}
			result += remainder.replaceAll("_", " ");
		} else {
			System.err.println("no match: " + zoneName);
		}
		return "".equals(result) ? zoneName : result.trim();
	}

	private static String getInteriorName(String zoneName) {
		if (zoneName == null) {
			throw new IllegalArgumentException("zoneName is null");
		}
		int start = zoneName.indexOf('_') + 1;
		int end = zoneName.indexOf('_', start);
		if (end < 0) {
			end = zoneName.length();
		}
		return start > 0 && end > start ? zoneName.substring(start, end)
				: zoneName;
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
	public String getHowDangerous(Player player, DefaultCreature creature,
			Map<Double, String> dangerLiterals) {
		String s = getLiteral(dangerLiterals,
				(double) ((double) creature.getLevel())
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
	 * @return
	 */
	private String getDropItemsInfo(final DefaultCreature creature,
			int maxNumberOfItems) {
		StringBuffer result = new StringBuffer();
		List<DropItem> dropItems = creature.getDropItems();
		Collections.sort(dropItems, new Comparator<DropItem>() {
			public int compare(DropItem o1, DropItem o2) {
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
		for (DropItem item : dropItems) {
			String probability = getLiteral(probabilityLiterals,
					item.probability, 0.0);
			if (prevProbability != null && !probability.equals(prevProbability)) {
				result.append(result.length() > 0 ? ", " : "");
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
		result.append(result.length() > 0 ? ", " : "");
		result.append(prevProbability.replaceAll("%s",
				Grammar.enumerateCollection(items)));
		return (prefix + result.toString()).replaceAll("_", " ");
	}

	/**
	 * Utility method for returning a random string from array of strings.
	 *
	 * @param texts
	 * @param params
	 * @return
	 */
	private String getRandomString(String[] texts, String... params) {
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
	 *
	 * @param literals
	 *            map of the literal strings
	 * @param val
	 *            the numeric value
	 * @param defValue
	 *            numeric value if given value is out of bounds
	 * @return
	 */
	private <T extends Number> String getLiteral(Map<T, String> literals,
			T val, T defValue) {
		String result = literals.get(defValue);
		for (T d : literals.keySet()) {
			if (d.doubleValue() <= val.doubleValue()) {
				result = literals.get(d);
				break;
			}
		}
		return result;
	}
}
