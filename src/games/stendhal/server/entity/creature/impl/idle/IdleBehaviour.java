/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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


public interface IdleBehaviour {

	void perform(NPC npc);

	/**
	 * Called when entity's position has changed.
	 *
	 * @param npc
	 *   Moving entity.
	 */
	void onMoved(NPC npc);

	/**
	 * There may be need to reset certain attributes.
	 */
	void reset();

	/**
	 * Can be called to execute tile collision behavior.
	 *
	 * @param npc
	 *   Entity being acted on.
	 * @param nx
	 *   Horizontal position where entity collides.
	 * @param ny
	 *   Vertical position where entity collides.
	 * @return
	 *   {@code true} if collision handling should not propagate.
	 */
	boolean handleSimpleCollision(NPC npc, int nx, int ny);

	/**
	 * Can be called to execute entity collision behavior.
	 *
	 * @param npc
	 *   Entity being acted on.
	 * @return
	 *   {@code true} if collision handling should not propagate.
	 */
	boolean handleObjectCollision(NPC npc);
}
