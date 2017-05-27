/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.events;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * An event that tells the client to display an image sequence at an entity or
 * location.
 */
public class ImageEffectEvent extends RPEvent {
	/** For determining the image to show. */
	private static final String IMAGE_ATTR = "image";
	/** For binding the effect to an entity if set. To a location if not set. */
	private static final String ATTACH_ATTR = "attached";
	/**
	 * The x coordinate of the effect for non entity bound effects. If not set,
	 * the current entity coordinates are used.
	 */
	private static final String X_ATTR = "x";
	/**
	 * The y coordinate of the effect for non entity bound effects. If not set,
	 * the current entity coordinates are used.
	 */
	private static final String Y_ATTR = "y";


	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.IMAGE);
		rpclass.addAttribute(IMAGE_ATTR, Type.STRING);
		rpclass.addAttribute(ATTACH_ATTR, Type.FLAG);
		rpclass.addAttribute(X_ATTR, Type.INT);
		rpclass.addAttribute(Y_ATTR, Type.FLAG);
	}

	/**
	 * Create a new ImageEffectEvent.
	 *
	 * @param image image name
	 * @param attached if <code>true</code>, the effect will be bound for its
	 * life time to the source entity
	 */
	public ImageEffectEvent(String image, boolean attached) {
		super(Events.IMAGE);
		put(IMAGE_ATTR, image);
		if (attached) {
			put(ATTACH_ATTR, "");
		}
	}

	/**
	 * Create a new ImageEffectEvent at a specified location.
	 *
	 * @param image image name
	 * @param x x coordinate of the effect
	 * @param y y coordinate of the effect
	 */
	public ImageEffectEvent(String image, int x, int y) {
		this(image, false);
		put(X_ATTR, x);
		put(Y_ATTR, y);
	}
}
