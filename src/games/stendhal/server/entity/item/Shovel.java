/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;


public class Shovel extends AreaUseItem {

	private static final Logger logger = Logger.getLogger(Shovel.class);

	private static final EntityManager em = SingletonRepository.getEntityManager();

	private static final String ring_quest_slot = "lost_engagement_ring";
	private static final String ring_quest_info = "Ari's ring";


	public Shovel(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item Item to copy.
	 */
	public Shovel(final Shovel item) {
		super(item);
	}

	@Override
	protected boolean onUsedInArea(final RPEntity user, final StendhalRPZone zone, final int x, final int y) {
		if (user instanceof Player) {
			final Player player = (Player) user;

			if (zone.getName().equals("0_athor_island")) {
				if (player.hasQuest(ring_quest_slot)) {

					final String[] slot = player.getQuest(ring_quest_slot).split(";");
					if (slot.length > 1 && !slot[0].equals("found_ring") && !slot[0].equals("done")) {
						try {
							final int ringX = Integer.parseInt(slot[0]);
							final int ringY = Integer.parseInt(slot[1]);

							if (x == ringX && y == ringY) {
								final Item ring = em.getItem("engagement ring");
								ring.setInfoString(ring_quest_info);
								ring.setDescription("In eternal devotion to Emma.");
								ring.setBoundTo(player.getName());

								player.equipOrPutOnGround(ring);
								player.sendPrivateText("You found a ring.");

								// shift coordinates in case ring is lost & we need to access them again
								player.setQuest(ring_quest_slot, "found_ring;" + slot[0] + ";" + slot[1]);
							} else if (nearRing(x, y, ringX, ringY)) {
								player.sendPrivateText(
									"You see footprints in the sand. Someone must have been here.");
							}
						} catch (final NumberFormatException e) {
							logger.error("Malformatted quest slot " + ring_quest_slot, e);
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Checks if a position is within 10 steps of where ring is buried.
	 *
	 * @param px
	 *     Player's X coordinate.
	 * @param py
	 *     Player's Y coordinate.
	 * @param rx
	 *     Ring's X coordinate.
	 * @param ry
	 *     Ring's Y coordinate.
	 */
	private boolean nearRing(final int px, final int py, final int rx, final int ry) {
		return Math.abs(rx - px) < 6 && Math.abs(ry - py) < 6;
	}
}
