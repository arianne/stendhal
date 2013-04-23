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
import games.stendhal.client.gui.group.GroupPanelController;

/**
 * The player was invited or the invitation expired
 *
 * @author hendrik
 */
class GroupInviteEvent extends Event<RPEntity> {
	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		if (event.has("expire")) {
			GroupPanelController.get().expireInvite(event.get("leader"));
		} else {
			GroupPanelController.get().receiveInvite(event.get("leader"));
		}
	}
}
