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
package games.stendhal.server.entity;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.LootableSlot;
import games.stendhal.server.events.UseListener;

import java.util.Iterator;



import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

/**
 * A chest is an unmovable container. It can be opened and closed. While
 * it is open, every player can put items in and take them out later. A player
 * can take out items that another player put in.
 */
public class Chest extends Entity implements UseListener {

	private boolean open;

	public static void generateRPClass() {
		RPClass chest = new RPClass("chest");
		chest.isA("entity");
		chest.addAttribute("open", Type.FLAG);
		chest.addRPSlot("content", 30);
	}

	public Chest(RPObject object) {
		super(object);
		put("type", "chest");

		if (!hasSlot("content")) {
			RPSlot slot = new LootableSlot(this);
			addSlot(slot);
		}

		update();
	}

	public Chest() {
		super();
		put("type", "chest");
		open = false;

		RPSlot slot = new LootableSlot(this);
		addSlot(slot);
	}

	

	@Override
	public void update() {
		super.update();
		open = false;
		if (has("open")) {
			open = true;
		}
	}

	public void open() {
		this.open = true;
		put("open", "");
	}

	public void close() {
		this.open = false;
		if (has("open")) {
			remove("open");
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void add(PassiveEntity entity) {
		RPSlot content = getSlot("content");
		content.add(entity);
	}

	@Override
	public int size() {
		return getSlot("content").size();
	}

	public Iterator<RPObject> getContent() {
		RPSlot content = getSlot("content");
		return content.iterator();
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		if (player.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				open();
			}

			notifyWorldAboutChanges();
		}
	}

	@Override
	public String describe() {
		String text = "You see a chest.";
		if (hasDescription()) {
			text = getDescription();
		}
		text += " It is " + (isOpen() ? "open" : "closed") + ".";
		if (isOpen()) {
			text += " You can #inspect this item to see its contents.";
		}
		return (text);
	}
}
