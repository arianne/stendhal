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
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.group.GroupPanelController;

/**
 * The group has changed (players added, removed, etc)
 *
 * @author hendrik
 */
class GroupChangeEvent extends Event<RPEntity> {
	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		if (event.has("members")) {
			User.updateGroupStatus(event.getList("members"), event.get("lootmode"));
			GroupPanelController.get().update(event.getList("members"), event.get("leader"), event.get("lootmode"));
		} else {
			User.updateGroupStatus(null, null);
			GroupPanelController.get().update(null, null, null);
		}
	}
}
