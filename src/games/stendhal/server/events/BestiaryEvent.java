/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.events;

import static games.stendhal.common.constants.Events.BESTIARY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.SyntaxException;


/**
 * An event to show which creatures a player has killed.
 */
public class BestiaryEvent extends RPEvent {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(BestiaryEvent.class);

	private final List<Creature> standardEnemies;
	private final List<Creature> rareEnemies;
	private final List<Creature> abnormalEnemies;

	private final List<String> soloKills;
	private final List<String> sharedKills;


	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		try {
			final RPClass rpclass = new RPClass(BESTIARY);
			rpclass.addAttribute("enemies", Type.VERY_LONG_STRING, Definition.PRIVATE);
		} catch (final SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	/**
	 * Creates a new bestiary event.
	 *
	 * @param player
	 * 		Player from whom the bestiary is requested.
	 */
	public BestiaryEvent(final Player player) {
		this(player, true, true);
	}

	/**
	 * Creates a new bestiary event.
	 *
	 * @param player
	 * 		Player from whom the bestiary is requested.
	 * @param includeRare
	 * 		If <code>true</code>, creatures marked as "rare" will be included.
	 * @param includeAbnormal
	 * 		If <code>true</code>, creatures marked as "abnormal" will be included.
	 */
	public BestiaryEvent(final Player player, final boolean includeRare, final boolean includeAbnormal) {
		super(BESTIARY);

		// lists of enemies to be shown in bestiary
		standardEnemies = new ArrayList<>();
		rareEnemies = new ArrayList<>();
		abnormalEnemies = new ArrayList<>();

		soloKills = new ArrayList<String>();
		sharedKills = new ArrayList<String>();

		final StringBuilder formatted = new StringBuilder();

		if (player.hasSlot("!kills")) {
			String killString = player.getSlot("!kills").getFirst().toAttributeString();
			final char firstChar = killString.charAt(0);
			final char lastChar = killString.charAt(killString.length() - 1);

			// remove leading & trailing brackets
			if (firstChar == '[') {
				killString = killString.substring(1);
			}
			if (lastChar == ']') {
				killString = killString.substring(0, killString.length() - 1);
			}

			final EntityManager em = SingletonRepository.getEntityManager();

			for (String k: killString.split("\\]\\[")) {
				boolean shared = false;

				if (k.startsWith("solo.")) {
					k = k.replace("solo.", "");
				} else if (k.startsWith("shared.")) {
					shared = true;
					k = k.replace("shared.", "");
				}

				if (!k.contains("=")) {
					logger.warn("Invalid !kill format: " + k);
					continue;
				}

				final String[] count = k.split("=");
				try {
					if (Integer.parseInt(count[1]) > 0) {
						if (shared) {
							sharedKills.add(count[0]);
						} else {
							soloKills.add(count[0]);
						}
					}
				} catch (final NumberFormatException e) {
					logger.warn("Kill count value for creature \"" + count[1] + "\" not numeric");
				}
			}

			// place rare & abnormal enemies in separate lists
			for (final Creature e: em.getCreatures()) {
				if (e.isRare()) {
					rareEnemies.add(e);
				} else if (e.isAbnormal()) {
					abnormalEnemies.add(e);
				} else {
					standardEnemies.add(e);
				}
			}

			// sort alphabetically
			final Comparator<Creature> sorter = new Comparator<Creature>() {
				@Override
				public int compare(final Creature c1, final Creature c2) {
					return (c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase()));
				}
			};
			Collections.sort(standardEnemies, sorter);
			Collections.sort(rareEnemies, sorter);
			Collections.sort(abnormalEnemies, sorter);

			formatted.append(getFormattedString(standardEnemies));
			if (includeRare) {
				formatted.append(";" + getFormattedString(rareEnemies));
			}
			if (includeAbnormal) {
				formatted.append(";" + getFormattedString(abnormalEnemies));
			}
		}

		put("enemies", formatted.toString());
	}

	/**
	 * Formats a list of creatures into a string to be sent to & parsed by client.
	 *
	 * Entries are separated by semi-colons & are in the form of "name,solo,shared"
	 * where "solo" & "shared" are boolean values.
	 *
	 * @param enemies
	 * 		List of enemies.
	 * @return
	 * 		Formatted string.
	 */
	private String getFormattedString(final List<Creature> enemies) {
		final boolean rare = enemies.equals(rareEnemies);
		final boolean abnormal = enemies.equals(abnormalEnemies);

		final StringBuilder sb = new StringBuilder();
		final int creatureCount = enemies.size();
		int idx = 0;

		for (final Creature enemy: enemies) {
			String name = enemy.getName();
			Boolean solo = false;
			Boolean shared = false;

			if (soloKills.contains(name)) {
				solo = true;
			}
			if (sharedKills.contains(name)) {
				shared = true;
			}

			// hide the names of creatures not killed by player
			if (!solo && !shared) {
				name = "???";
			}

			if (rare) {
				name += " (rare)";
			} else if (abnormal) {
				name += " (abnormal)";
			}

			sb.append(name + "," + solo.toString() + "," + shared.toString());
			if (idx != creatureCount - 1) {
				sb.append(";");
			}

			idx++;
		}

		return sb.toString();
	}
}
