/*
 * $Id$
 */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

/**
 * Represents the rainbow beans that takes the player to the dream world zone,
 * after which it will teleport player to a random location in 0_semos_plains_s.
 */
public class RainbowBeansScroll extends TimedTeleportScroll {

	private static final long DELAY = 6 * MathHelper.MILLISECONDS_IN_ONE_HOUR;

	/**
	 * Creates a new timed marked RainbowBeansScroll scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public RainbowBeansScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public RainbowBeansScroll(final RainbowBeansScroll item) {
		super(item);
	}

	@Override
	protected boolean useTeleportScroll(final Player player) {
		final String QUEST_SLOT = "rainbow_beans";
		long lastuse = -1;
		if (player.hasQuest(QUEST_SLOT)) {
			final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
			if (tokens.length == 4) {
				// we stored a last time (or -1)
				lastuse = Long.parseLong(tokens[3]);
			}
			final long timeRemaining = (lastuse + DELAY) - System.currentTimeMillis();
			if (timeRemaining > 0) {
				// player used the beans within the last DELAY hours
				// so are not allowed to go yet. but don't reset the last time taken.
				// the private text doesn't get sent because events are lost on zone change. (marauroa bug)
				player.sendPrivateText("You were just sick from overuse of the rainbow beans. Classy!");
				final Item sick = SingletonRepository.getEntityManager().getItem("vomit");
				player.getZone().add(sick);
				sick.setPosition(player.getX(), player.getY() + 1);
				// Success, so that the beans still gets used up, even though
				// the player was not teleported.
				return true;
			} else {
				// don't overwrite the last bought time from Pdiddi, this is in tokens[1]
				player.setQuest(QUEST_SLOT, "bought;" + tokens[1] + ";taken;" + System.currentTimeMillis());
				return super.useTeleportScroll(player);
			}
		} else {
			// players can only buy rainbow beans from Pdiddi who stores the time bought in quest slot
			// so if they didn't have the quest slot they got the beans ''illegally''
			player.sendPrivateText("Those dodgy beans made you sick. Next time buy them from Pdiddi.");
			this.removeOne();
			final Item sick = SingletonRepository.getEntityManager().getItem("vomit");
			player.getZone().add(sick);
			sick.setPosition(player.getX(), player.getY() + 1);
			return false;
		}
	}

	@Override
	protected String getBeforeReturnMessage() {
		return "Your head begins to feel clearer...";
	}

	@Override
	protected String getAfterReturnMessage() {
		return "You find yourself in the forest with a bad headache."
				+ " That was a strange experience.";
	}
}
