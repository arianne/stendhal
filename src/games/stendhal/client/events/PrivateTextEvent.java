/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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
 * Private chat
 *
 * @author hendrik
 */
class PrivateTextEvent extends Event<RPEntity> {
	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		entity.onPrivateListen(event.get("texttype"), event.get("text"));
	}
}
