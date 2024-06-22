/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature.impl.idle;

import games.stendhal.server.entity.npc.NPC;


public abstract class AbstractIdleBehaviour implements IdleBehaviour {

	@Override
	public abstract void perform(NPC npc);

	@Override
	public void onMoved(NPC npc) {
		// does nothing in this implementation
	}

	@Override
	public void reset() {
		// does nothing in this implementation
	}

	@Override
	public boolean handleSimpleCollision(NPC npc, int nx, int ny) {
		return false;
	}

	@Override
	public boolean handleObjectCollision(NPC npc) {
		return false;
	}
}
