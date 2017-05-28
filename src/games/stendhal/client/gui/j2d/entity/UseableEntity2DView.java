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
package games.stendhal.client.gui.j2d.entity;

import java.util.List;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.StatefulEntity;

/**
 * The 2D view of a useable entity
 *
 * @param <T> type of useable entity
 */
class UseableEntity2DView<T extends StatefulEntity> extends VariableSpriteEntity2DView<T> {

	private ActionType action;

	/**
	 * creates a new UseableEntity2DView
	 */
	public UseableEntity2DView() {
		this.action = ActionType.USE;
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if ((property == IEntity.PROP_CLASS) || (property == IEntity.PROP_STATE)) {
			representationChanged = true;
		}
	}

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 *
	 * Also, players can only interact with the topmost entity.
	 *
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 3000;
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		if (!entity.getRPObject().has("menu")) {
			list.add(action.getRepresentation());
		}

		super.buildActions(list);
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(action);
	}

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(ActionType at) {
		if (at == null) {
			at = action;
		}
		if (isReleased()) {
			return;
		}
		if (at.getActionCode().equals(this.action.getActionCode())) {
			at.send(at.fillTargetInfo(entity));
		} else {
			super.onAction(at);
		}
	}
}
