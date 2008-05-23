/*
 * @(#) games/stendhal/client/gui/j2d/entity/ActiveEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.ActiveEntity;
import games.stendhal.client.entity.Entity;
import games.stendhal.common.Direction;

/**
 * The 2D view of an animated entity.
 */
public abstract class ActiveEntity2DView extends StateEntity2DView {
	/**
	 * The down facing state.
	 */
	protected static final String STATE_DOWN = "move_down";

	/**
	 * The up facing state.
	 */
	protected static final String STATE_UP = "move_up";

	/**
	 * The left facing state.
	 */
	protected static final String STATE_LEFT = "move_left";

	/**
	 * The right facing state.
	 */
	protected static final String STATE_RIGHT = "move_right";

	/**
	 * The active entity.
	 */
	private ActiveEntity activeEntity;

	/**
	 * Create a 2D view of an entity.
	 * 
	 * @param activeEntity
	 *            The entity to render.
	 */
	public ActiveEntity2DView(final ActiveEntity activeEntity) {
		super(activeEntity);

		this.activeEntity = activeEntity;
	}

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
	protected String getDirectionState(final Direction direction) {
		switch (direction) {
		case LEFT:
			return STATE_LEFT;

		case RIGHT:
			return STATE_RIGHT;

		case UP:
			return STATE_UP;

		case DOWN:
			return STATE_DOWN;

		default:
			return STATE_DOWN;
		}
	}

	//
	// StateEntity2DView
	//

	/**
	 * Get the current model state.
	 * 
	 * @return The model state.
	 */
	@Override
	protected Object getState() {
		return getDirectionState(activeEntity.getDirection());
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
		return !activeEntity.stopped();
	}

	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 * 
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == ActiveEntity.PROP_DIRECTION) {
			stateChanged = true;
		} else if (property == ActiveEntity.PROP_SPEED) {
			animatedChanged = true;
		}
	}
}
