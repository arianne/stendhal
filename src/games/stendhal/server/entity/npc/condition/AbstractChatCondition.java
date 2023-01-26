/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.npc.ChatCondition;


public abstract class AbstractChatCondition implements ChatCondition {

	private static int nextUniqueHashModifier = 5000;


	public static int getNextUniqueHashModifier() {
		final int modifier = nextUniqueHashModifier;
		nextUniqueHashModifier++;
		return modifier;
	}
}
