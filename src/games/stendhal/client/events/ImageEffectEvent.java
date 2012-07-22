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
package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.ImageEventProperty;

/**
 * Events that draw a temporary animation.
 */
class ImageEffectEvent extends Event<Entity> {
	@Override
	public void execute() {
		// An image effect that should be bound to the entity view
		if (event.has("attached")) {
			entity.fireChange(new ImageEventProperty(event.get("image")));
		}
		// non attached events need to be implemented (create an entity view
		// whose only purpose is to contain the effect, and removes itself once
		// the ImageEffect detaches the effect sprite)
	}
}
