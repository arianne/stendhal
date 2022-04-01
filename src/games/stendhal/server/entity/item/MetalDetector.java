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

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;


public class MetalDetector extends AreaUseItem {

	private static final Logger logger = Logger.getLogger(MetalDetector.class);

	private static final EntityManager em = SingletonRepository.getEntityManager();

	private static final String ring_quest_slot = "lost_engagement_ring";
	private static final String ring_quest_info = "Ari's ring";


	public MetalDetector(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item Item to copy.
	 */
	public MetalDetector(final MetalDetector item) {
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
							if (detectRing(player, x, y, Integer.parseInt(slot[0]), Integer.parseInt(slot[1]))) {
								final Item ring = em.getItem("engagement ring");
								ring.setInfoString(ring_quest_info);
								ring.setDescription("In eternal devotion to Emma.");
								ring.setBoundTo(player.getName());

								player.equipOrPutOnGround(ring);
								player.sendPrivateText("You found a ring.");

								// shift coordinates in case ring is lost & we need to access them again
								player.setQuest(ring_quest_slot, "found_ring;" + slot[0] + ";" + slot[1]);
							}

							// prevent default message to player
							return true;
						} catch (final NumberFormatException e) {
							logger.error("Malformatted quest slot " + ring_quest_slot, e);
						}
					}
				}
			}

			player.sendPrivateText("It doesn't detect anything.");
		}

		return true;
	}

	/**
	 * Detects how close to ring player is.
	 *
	 * @param player
	 *     Player using metal detector.
	 * @param px
	 *     Player's X coordinate.
	 * @param py
	 *     Player's Y coordinate.
	 * @param rx
	 *     Ring's X coordinate.
	 * @param ry
	 *     Ring's Y coordinate.
	 * @return
	 *     <code>true</code> if player found ring.
	 */
	private boolean detectRing(final Player player, final int px, final int py, final int rx, final int ry) {
		final int distanceX = Math.abs(rx - px);
		final int distanceY = Math.abs(ry - py);

		boolean ring_found = false;
		String beeps = null;

		if (distanceX == 0 && distanceY == 0) {
			// player is standing on ring.
			beeps = "metal_detector_beep_x4";
			ring_found = true;
		} else if (distanceX < 6 && distanceY < 6) {
			// player within 5 steps
			player.sendPrivateText("It is getting louder, something is very close.");
			beeps = "metal_detector_beep_x3";
		} else if (distanceX < 11 && distanceY < 11) {
			// player within 10 steps
			player.sendPrivateText("It is detecting something nearby.");
			beeps = "metal_detector_beep_x2";
		} else if (distanceX < 16 && distanceY < 16) {
			// player wthin 15 steps
			player.sendPrivateText("It faintly detects something.");
			beeps = "metal_detector_beep_x1";
		} else {
			player.sendPrivateText("It doesn't detect anything.");
		}

		if (beeps != null) {
			player.addEvent(new SoundEvent(beeps, SoundLayer.FIGHTING_NOISE));
			player.notifyWorldAboutChanges();
		}

		return ring_found;
	}
}
