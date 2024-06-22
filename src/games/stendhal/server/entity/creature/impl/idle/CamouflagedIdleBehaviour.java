/***************************************************************************
 *                    Copyright © 2013-2024 - Stendhal                     *
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
import games.stendhal.server.entity.npc.behaviour.impl.idle.AbstractIdleBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.idle.IdleBehaviour;


/**
 * An idle behaviour, where the idling creature stays invisible
 *
 * @author madmetzger
 */
class CamouflagedIdleBehaviour extends AbstractIdleBehaviour {

	private final IdleBehaviour base = new Patroller();

	@Override
	public void perform(NPC creature) {
		creature.setVisibility(50);
		this.base.perform(creature);
	}

}
