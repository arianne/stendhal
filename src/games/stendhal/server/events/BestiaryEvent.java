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

public class BestiaryEvent extends RPEvent {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(BestiaryEvent.class);

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
	 *
	 * @param player
	 * 		Player from whom the bestiary is requested.
	 */
	public BestiaryEvent(final Player player) {
		super(BESTIARY);

		final StringBuilder sb = new StringBuilder();

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

			final List<String> soloKills = new ArrayList<String>();
			final List<String> sharedKills = new ArrayList<String>();

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
						// exclude rare & abnormal creatures
						final Creature creature = em.getCreature(count[0]);
						if (creature != null && !creature.isAbnormal()) {
							if (shared) {
								sharedKills.add(count[0]);
							} else {
								soloKills.add(count[0]);
							}
						}
					}
				} catch (final NumberFormatException e) {
					logger.warn("Kill count value for creature \"" + count[1] + "\" not numeric");
				}
			}

			int idx = 0;
			final List<Creature> enemies = new ArrayList<>();
			enemies.addAll(em.getCreatures());

			// sort alphabetically
			Collections.sort(enemies, new Comparator<Creature>() {
				@Override
				public int compare(final Creature c1, final Creature c2) {
					return (c1.getName().toLowerCase().compareTo(c2.getName().toLowerCase()));
				}
			});

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

				sb.append(name + "," + solo.toString() + "," + shared.toString());
				if (idx != enemies.size() - 1) {
					sb.append(";");
				}

				idx++;
			}
		}

		put("enemies", sb.toString());
	}
}
