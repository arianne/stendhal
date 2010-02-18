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
package games.stendhal.client.entity;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

public class Creature extends AudibleEntity {
	/**
	 * Debug property.
	 */
	public static final Property PROP_DEBUG = new Property();

	/**
	 * Metamorphosis property.
	 */
	public static final Property PROP_METAMORPHOSIS = new Property();

	@Override
	protected void nonCreatureClientAddEventLine(final String text) {
		
		// no logging for Creature "sounds" in the client window
	}

	/**
	 * The current debug info.
	 */
	private String debug;

	/**
	 * The current metamorphosis.
	 */
	private String metamorphosis;

	//
	// Creature
	//

	public String getDebug() {
		return debug;
	}

	/**
	 * Get the metamorphosis in effect.
	 * 
	 * @return The metamorphosis, or <code>null</code>.
	 */
	public String getMetamorphosis() {
		return metamorphosis;
	}

	//
	// Entity
	//

	/**
	 * Get the area the entity occupies.
	 * 
	 * @return A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(getWidth() - 1.0) < 0.1)
				&& (Math.abs(getHeight() - 2.0) < 0.1)) {
			return new Rectangle.Double(getX(), getY() + 1.0, 1.0, 1.0);
		}

		return super.getArea();
	}

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param object
	 *            The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		final String type = getType();

		if (object.has("name")) {
			final String name = object.get("name");

			addSoundsToGroup("attack",
				"punch-1"   , "punch-2", "punch-3",
				"punch-4"   , "punch-5", "punch-6",
				"swingaxe-1", "slap-1" , "arrow-1");

			if (type.startsWith("creature")) {
				if (name.equals("wolf")) {
					addSoundsToGroup("move", "bark-1", "howl-5", "howl-2", "howl-11");
				} else if (name.equals("rat") || name.equals("caverat") || name.equals("venomrat")) {
					addSoundsToGroup("move", "rats-2", "rats-41"/*, "rats-3*/);
				} else if (name.equals("razorrat")) {
					addSoundsToGroup("move", "rats-1");
				} else if (name.equals("gargoyle")) {
					addSoundsToGroup("move", "hyena-1", "hyena-2", "hyena-3");
				} else if (name.equals("boar")) {
					addSoundsToGroup("move", "pig-1", "pig-2");
				} else if (name.equals("bear")) {
					addSoundsToGroup("move", "bear-1", "bear-2", "bear-3");
				} else if (name.equals("giantrat")) {
					addSoundsToGroup("move", "bobcat-1", "leopard-11");
				} else if (name.equals("cobra")) {
					addSoundsToGroup("move", "snake-1");
				} else if (name.equals("kobold")) {
					addSoundsToGroup("move", "panda-1", "panda-2", "racoon-1", "lama-1");
				} else if (name.equals("goblin")) {
					addSoundsToGroup("move", "saur-3", "saur-4");
				} else if (name.equals("troll")) {
					addSoundsToGroup("move", "gorilla-1", "gorilla-2", "gorilla-3", "gorilla-4", "gorilla-5");
				} else if (name.equals("orc")) {
					addSoundsToGroup("move", "lion-11", "lion-22");
				} else if (name.equals("ogre")) {
					addSoundsToGroup("move", "yell-1", "groan-1", "moan-1", "fart-1");
				} else if (name.equals("skeleton")) {
					addSoundsToGroup("move", "bones-1", "evillaugh-3", "evillaugh-5", "ghost-1", "ghost-2");
				} else if (name.equals("cyclops")) {
					addSoundsToGroup("move", "yell-1", "laugh-33", "evillaugh-4", "grunt-1", "grunt-2");
				}
			}
		}

		if (object.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
		} else {
			metamorphosis = null;
		}
	}

	/**
	 * When the entity's position changed.
	 * 
	 * @param x
	 *            The new X coordinate.
	 * @param y
	 *            The new Y coordinate.
	 */
	@Override
	protected void onPosition(final double x, final double y) {
		super.onPosition(x, y);
		playRandomSoundFromGroup("move", 1.0f, 10000);
	}

	@Override
	public void onDamaged(Entity attacker, int damage) {
		super.onDamaged(attacker, damage);
		playRandomSoundFromGroup("attack", 1.0f);
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Debuging?
		 */
		if (changes.has("debug")) {
			debug = changes.get("debug");
			fireChange(PROP_DEBUG);
		}

		if (changes.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
			fireChange(PROP_METAMORPHOSIS);
		}
	}

	/**
	 * The object removed attribute(s).
	 * 
	 * @param object
	 *            The base object.
	 * @param changes
	 *            The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("metamorphosis")) {
			metamorphosis = null;
			fireChange(PROP_METAMORPHOSIS);
		}
	}
}
