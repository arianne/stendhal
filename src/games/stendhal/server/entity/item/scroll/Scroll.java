/* $Id$
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

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import org.apache.log4j.Logger;
import marauroa.common.game.RPObject;

/**
 * Represents a scroll.
 */
public class Scroll extends StackableItem implements UseListener {

	private static final Logger logger = Logger.getLogger(Scroll.class);

	/**
	 * Creates a new scroll.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Scroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public Scroll(final Scroll item) {
		super(item);
	}

	/**
	 * Make sure that both are scrolls of the same kind.
	 * 
	 * @return <code>true</code> if the items have the same class and
	 *         subclass.
	 */
	@Override
	public boolean isStackable(final Stackable other) {
		final StackableItem otheri = (StackableItem) other;

		return (getItemClass().equals(otheri.getItemClass()) && getItemSubclass().equals(
				otheri.getItemSubclass()));
	}

	public final boolean onUsed(final RPEntity user) {
		RPObject base = this;

		// Find the top container
		while (base.isContained()) {
			base = base.getContainer();
		}

		if (user.nextTo((Entity) base)) {
			if (useScroll((Player) user)) {
				this.removeOne();
				user.notifyWorldAboutChanges();
			}
			return true;
		} else {
			logger.debug("Scroll is too far away");
			return false;
		}
	}

	/**
	 * Use a scroll.
	 * 
	 * @param player
	 *            The player using scroll.
	 * 
	 * @return <code>true</code> if successful, <code>false</code>
	 *         otherwise.
	 */
	protected boolean useScroll(final Player player) {
		player.sendPrivateText("What a strange scroll! You can't make heads or tails of it.");
		return false;
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
}
