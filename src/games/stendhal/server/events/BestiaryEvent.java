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
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.SyntaxException;

public class BestiaryEvent extends RPEvent {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ShowItemListEvent.class);

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

			if (firstChar == '[') {
				killString = killString.substring(1);
			}
			if (lastChar == ']') {
				killString = killString.substring(0, killString.length() - 1);
			}

			final List<String> soloKills = new ArrayList<String>();
			final List<String> sharedKills = new ArrayList<String>();

			for (String k: killString.split("\\]\\[")) {
				if (k.startsWith("solo.")) {
					k = k.replace("solo.", "");
					String[] count = k.split("=");
					if (Integer.parseInt(count[1]) > 0) {
						soloKills.add(count[0]);
					}
				} else if (k.startsWith("shared.")) {
					k = k.replace("shared.", "");
					String[] count = k.split("=");
					if (Integer.parseInt(count[1]) > 0) {
						sharedKills.add(count[0]);
					}
				}
			}

			int idx = 0;
			final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
			for (final Creature cr: creatures) {
				final String name = cr.getName();
				Boolean solo = false;
				Boolean shared = false;

				if (soloKills.contains(name)) {
					solo = true;
				}
				if (sharedKills.contains(name)) {
					shared = true;
				}

				sb.append(name + "," + solo.toString() + "," + shared.toString());
				if (idx != creatures.size() - 1) {
					sb.append(";");
				}

				idx++;
			}
		}

		put("enemies", sb.toString());
	}
}
