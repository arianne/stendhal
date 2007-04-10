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

/**
 * This animated entity supports multiple states.
 */
public abstract class AnimatedStateEntity extends Entity {
	/** actual animation */
	protected String animation;


	//
	// AnimatedStateEntity
	//

	/**
	 * Get the current entity state.
	 */
	public String getState() {
		return animation;
	}
}
