/*
 * $Id$
 */
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
package games.stendhal.server.entity.item.scroll;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TeleportNotifier;
import games.stendhal.server.entity.player.Player;

/**
 * Represents a marked teleport scroll.
 */
public class MarkedScroll extends TeleportScroll {

	private static final Logger logger = Logger.getLogger(MarkedScroll.class);

	/**
	 * Creates a new marked teleport scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public MarkedScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		this.applyDestInfo();
		this.update();
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public MarkedScroll(final MarkedScroll item) {
		super(item);

		this.applyDestInfo();
		this.update();
	}

	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the player on
	 * the scroll's destination, or near it.
	 *
	 * @param player
	 *            The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	@Override
	protected boolean useTeleportScroll(final Player player) {
		// init as home_scroll
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_city");
		int x = 30;
		int y = 40;

		/*
		 * Marked scrolls have a destination which is stored in the infostring,
		 * consisting of a zone name and x and y coordinates
		 */
		final String infostring = getInfoString();

		if (infostring != null) {
			final StringTokenizer st = new StringTokenizer(infostring);
			if (st.countTokens() == 3) {
				// check destination
				final String zoneName = st.nextToken();
				final StendhalRPZone temp = SingletonRepository.getRPWorld().getZone(zoneName);
				if (temp == null) {
					// invalid zone (the scroll may have been marked in an
					// old version and the zone was removed)
					player.sendPrivateText("Oh oh. For some strange reason the scroll did not teleport me to the right place.");
					logger.warn("marked scroll to unknown zone " + infostring
							+ " teleported " + player.getName()
							+ " to Semos instead");
				} else {
					if (player.getKeyedSlot("!visited", zoneName) == null) {
						player.sendPrivateText("Although you have heard a lot of rumors about the destination, "
								+ "you cannot concentrate on it because you have never been there.");
						return false;
					} else {
					        zone = temp;
					        x = Integer.parseInt(st.nextToken());
							y = Integer.parseInt(st.nextToken());
						if (!zone.isTeleportInAllowed(x, y)) {
							player.sendPrivateText("The strong anti magic aura in the destination area prevents the scroll from working!");
							return false;
						}
					}
				}
			}
		}

		// we use the player as teleporter (last parameter) to give feedback
		// if something goes wrong.
		TeleportNotifier.get().notify(player, true);
		return player.teleport(zone, x, y, null, player);
	}

	@Override
	public String describe() {
		String text = super.describe();

		final String infostring = getInfoString();

		if (infostring != null) {
			text += " Upon it is written: " + infostring;
		}
		return (text);
	}

	@Override
	public void setInfoString(final String infostring) {
		super.setInfoString(infostring);
		this.applyDestInfo();
	}

	public void applyDestInfo() {
		if (this.has("infostring")) {
			final String[] infos = this.get("infostring").split(" ");
			if (infos.length > 2) {
				this.put("dest", infos[0] + "," + infos[1] + "," + infos[2]);
			}
		}
	}
}
