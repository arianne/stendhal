/***************************************************************************
 *                   Copyright © 2003-2022 - Arianne                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import java.util.List;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;


public class TrainingDummy2DView extends NPC2DView<NPC> {

	/**
	 * Places "attack" as first option in action context menu.
	 */
	@Override
	protected void reorderActions(final List<String> list) {
		if (list.remove(ActionType.ATTACK.getRepresentation())) {
			list.add(0, ActionType.ATTACK.getRepresentation());
		}
	}

	@Override
	public void onAction() {
		onAction(ActionType.ATTACK);
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.ATTACK;
	}
}
