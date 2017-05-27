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

import games.stendhal.client.entity.ActiveEntity;
import games.stendhal.common.Direction;

/**
 * The 2D view of an animated entity.
 *
 * @param <T> entity type
 */
abstract class ActiveEntity2DView<T extends ActiveEntity> extends StateEntity2DView<T> {


	//
	// ActiveEntity2DView
	//

	/**
	 * Get the appropriate named state for a direction.
	 *
	 * @param direction
	 *            The direction.
	 *
	 * @return A named state.
	 */
	private Direction getDirectionState(final Direction direction) {
		if (direction == Direction.STOP) {
			return Direction.DOWN;
		}
		return direction;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Get the current model state.
	 *
	 * @param entity
	 * @return The model state.
	 */
	@Override
	protected Direction getState(T entity) {
		return getDirectionState(entity.getDirection());
	}

	//
	// Entity2DView
	//

	/**
	 * Determine if this view is currently animatable.
	 *
	 * @return <code>true</code> if animating enabled.
	 */
	@Override
	protected boolean isAnimating() {
		return !entity.stopped();
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == ActiveEntity.PROP_DIRECTION) {
			proceedChangedState(entity);
		} else if (property == ActiveEntity.PROP_SPEED) {
			animatedChanged = true;
		}
	}
}
