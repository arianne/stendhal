/* $Id$ */
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
package games.stendhal.server.entity.mapstuff.chest;

import java.util.Iterator;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.ChestSlot;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A chest is an unmovable container. It can be opened and closed. While it is
 * open, every player can put items in and take them out later. A player can
 * take out items that another player put in.
 */
public class Chest extends Entity implements UseListener {
	private static final String CHEST_RPCLASS_NAME = "chest";

	/**
	 * Whether the chest is open.
	 */
	private boolean open;

	/**
	 * Creates a new chest.
	 */
	public Chest() {
		setRPClass(CHEST_RPCLASS_NAME);
		put("type", CHEST_RPCLASS_NAME);
		open = false;

		final RPSlot slot = new ChestSlot(this);
		addSlot(slot);
	}

	/**
	 * Creates a new chest.
	 *
	 * @param object
	 *            RPObject
	 */
	public Chest(final RPObject object) {
		super(object);
		setRPClass(CHEST_RPCLASS_NAME);
		put("type", CHEST_RPCLASS_NAME);

		if (!hasSlot("content")) {
			final RPSlot slot = new ChestSlot(this);
			addSlot(slot);
		}

		update();
	}

	public static void generateRPClass() {
		if (!RPClass.hasRPClass(CHEST_RPCLASS_NAME)) {
			final RPClass chest = new RPClass(CHEST_RPCLASS_NAME);
			chest.isA("entity");
			chest.addAttribute("open", Type.FLAG);
			chest.addRPSlot("content", 30);
		}
	}


	//
	// Chest
	//

	@Override
    public String getDescriptionName(final boolean definite) {
	    return Grammar.article_noun(CHEST_RPCLASS_NAME, definite);
    }

	@Override
	public void update() {
		super.update();
		open = false;
		if (has("open")) {
			open = true;
		}
	}

	/**
	 * Open the chest.
	 */
	public void open() {
		this.open = true;
		put("open", "");
	}

	/**
	 * Close the chest.
	 */
	public void close() {
		this.open = false;

		if (has("open")) {
			remove("open");
		}
	}

	/**
	 * Determine if the chest is open.
	 *
	 * @return <code>true</code> if the chest is open.
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Adds a passive entity (like an item) to the chest.
	 *
	 * @param entity
	 *            entity to add
	 */
	public void add(final PassiveEntity entity) {
		final RPSlot content = getSlot("content");
		content.add(entity);
	}

	@Override
	public int size() {
		return getSlot("content").size();
	}

	/**
	 * Returns the content.
	 *
	 * @return iterator for the content
	 */
	public Iterator<RPObject> getContent() {
		final RPSlot content = getSlot("content");
		return content.iterator();
	}

	//
	// UseListener
	//

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				open();
			}

			notifyWorldAboutChanges();
			return true;
		}
		if (user instanceof Player) {
			final Player player = (Player) user;
			player.sendPrivateText("You cannot reach the chest from there.");
		}
		return false;
	}

	//
	// Entity
	//

	@Override
	public String describe() {
		String text = "You see a chest.";

		if (hasDescription()) {
			text = getDescription();
		}

		if (isOpen()) {
			text += " It is open.";
			text += " You can right click and inspect this item to see its contents.";
		} else {
			text += " It is closed.";
		}

		return (text);
	}
}
