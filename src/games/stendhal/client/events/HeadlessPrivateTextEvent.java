/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import games.stendhal.client.entity.RPEntity;

/**
 * Same as PrivateTextEvent, but does not draw on canvas.
 *
 * FIXME: Should be able to be done in PrivateTextEvent using attributes.
 */
public class HeadlessPrivateTextEvent extends Event<RPEntity> {

	@Override
	public void execute() {
		entity.onPrivateListen(event.get("texttype"), event.get("text"), true);
	}

}
