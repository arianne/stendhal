/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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
 * Handling reaching an achievement client side
 *
 * @author madmetzger
 */
class ReachedAchievementEvent extends Event<RPEntity>{
	@Override
	public void execute() {
		String achievementTitle = event.get("title");
		String achievementDescription = event.get("description");
		String achievementCategory = event.get("category");
		entity.onReachAchievement(achievementTitle, achievementDescription, achievementCategory);
	}
}
