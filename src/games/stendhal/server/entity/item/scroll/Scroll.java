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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;
import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Represents a scroll.
 */
public class Scroll extends StackableItem implements UseListener {

	private static final Logger logger = Logger.getLogger(Scroll.class);

	/**
	 * Creates a new scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public Scroll(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Make sure that both are scrolls of the same kind.
	 *
	 * @return	<code>true</code> if the items have the same class and subclass.
	 */
	@Override
	public boolean isStackable(Stackable other) {
		StackableItem otheri = (StackableItem) other;

		return (getItemClass().equals(otheri.getItemClass()) && getItemSubclass().equals(otheri.getItemSubclass()));
	}

	public void onUsed(RPEntity user) {
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
		} else {
			logger.debug("Scroll is too far away");
		}
	}

	/**
	 * Use a scroll.
	 *
	 * @param	player	The player using scroll.
	 *
	 * @return	<code>true</code> if successful,
	 *		<code>false</code> otherwise.
	 */
	protected boolean useScroll(Player player) {
		/*
		 * Default behaviour
		 * XXX - obsolete?? Can never really happen, make abstract?
		 */
		player.sendPrivateText("What a strange scroll! You can't make heads or tails of it.");
		return false;
	}

	@Override
	public String describe() {
		String text = super.describe();
		if (has("infostring") && (get("infostring") != null)) {
			text += " Upon it is written: " + get("infostring");
		}
		return (text);
	}
}
