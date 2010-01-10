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

import games.stendhal.client.sound.SoundLayer;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Rand;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

public class Creature extends RPEntity {
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

			if (type.startsWith("creature")) {
				if (name.equals("wolf")) {
					moveSounds = new String[] { "bark-1.wav", "howl-5.wav",
							"howl-2.wav", "howl-11.wav" };

				} else if (name.equals("rat") || name.equals("caverat")
						|| name.equals("venomrat")) {
					moveSounds = new String[2];
					moveSounds[0] = "rats-2.wav";
					moveSounds[1] = "rats-41.wav";
					// moveSounds[2]="rats-3.wav";

				} else if (name.equals("razorrat")) {
					moveSounds = new String[1];
					moveSounds[0] = "rats-1.wav";

				} else if (name.equals("gargoyle")) {
					moveSounds = new String[3];
					moveSounds[0] = "hyena-1.wav";
					moveSounds[1] = "hyena-2.aiff";
					moveSounds[2] = "hyena-3.wav";

				} else if (name.equals("boar")) {
					moveSounds = new String[2];
					moveSounds[0] = "pig-1.wav";
					moveSounds[1] = "pig-2.wav";

				} else if (name.equals("bear")) {
					moveSounds = new String[3];
					moveSounds[0] = "bear-1.aiff";
					moveSounds[1] = "bear-2.wav";
					moveSounds[2] = "bear-3.wav";

				} else if (name.equals("giantrat")) {
					moveSounds = new String[2];
					moveSounds[0] = "bobcat-1.wav";
					moveSounds[1] = "leopard-11.wav";

				} else if (name.equals("cobra")) {
					moveSounds = new String[1];
					moveSounds[0] = "snake-1.wav";

				} else if (name.equals("kobold")) {
					moveSounds = new String[4];
					moveSounds[0] = "panda-1.wav";
					moveSounds[1] = "panda-2.aiff";
					moveSounds[2] = "racoon-1.aiff";
					moveSounds[3] = "lama-1.wav";

				} else if (name.equals("goblin")) {
					moveSounds = new String[2];
					moveSounds[0] = "saur-3.au";
					moveSounds[1] = "saur-4.wav,x";

				} else if (name.equals("troll")) {
					moveSounds = new String[5];
					moveSounds[0] = "gorilla-1.wav";
					moveSounds[1] = "gorilla-2.wav";
					moveSounds[2] = "gorilla-3.wav";
					moveSounds[3] = "gorilla-4.au";
					moveSounds[4] = "gorilla-5.aiff";

				} else if (name.equals("orc")) {
					moveSounds = new String[2];
					moveSounds[0] = "lion-11.wav";
					moveSounds[1] = "lion-22.wav";

				} else if (name.equals("ogre")) {
					moveSounds = new String[4];
					moveSounds[0] = "yell-1.wav";
					moveSounds[1] = "groan-1.wav";
					moveSounds[2] = "moan-1.wav";
					moveSounds[3] = "fart-1.wav";

				} else if (name.equals("skeleton")) {
					moveSounds = new String[5];
					moveSounds[0] = "bones-1.aiff";
					moveSounds[1] = "evillaugh-3.wav";
					moveSounds[2] = "evillaugh-5.wav";
					moveSounds[3] = "ghost-1.wav";
					moveSounds[4] = "ghost-2.wav";

				} else if (name.equals("cyclops")) {
					moveSounds = new String[4];
					moveSounds[0] = "laugh-33.wav";
					moveSounds[1] = "evillaugh-4.wav";
					moveSounds[2] = "grunt-1.wav";
					moveSounds[3] = "grunt-2.wav";

				}
			}
		}

		if (object.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
		} else {
			metamorphosis = null;
		}
	}

	private long soundWait;

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

		if ((soundWait < System.currentTimeMillis()) && (Rand.rand(100) < 5)) {
			if (moveSounds != null) {
				SoundMaster.play(SoundLayer.CREATURE_NOISE, moveSounds[Rand.rand(moveSounds.length)], x, y);
			}

			soundWait = System.currentTimeMillis() + 1000L;
		}
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
