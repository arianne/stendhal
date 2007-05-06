/**
 * @(#) src/games/stendhal/client/entity/InvisibleEntity.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import marauroa.common.game.RPObject;

/**
 * An generic entity that is not drawn.
 */
public class InvisibleEntity extends Entity {
	/*
	 * The entity height.
	 */
	protected double	height;

	/*
	 * The entity width.
	 */
	protected double	width;


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	@Override
	protected Entity2DView createView() {
		return new InvisibleEntity2DView(this);
	}


	/**
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	@Override
	protected double getHeight() {
		return height;
	}


	/**
	 * Get the entity width.
	 *
	 * @return	The width.
	 */
	@Override
	protected double getWidth() {
		return width;
	}


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		if (object.has("height")) {
			height = object.getDouble("height");
		} else {
			height = 1.0;
		}

		if (object.has("width")) {
			width = object.getDouble("width");
		} else {
			width = 1.0;
		}
	}


	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(RPObject object, RPObject changes) {
		super.onChangedAdded(object, changes);

		if (changes.has("height")) {
			height = changes.getDouble("height");
		}

		if (changes.has("width")) {
			width = changes.getDouble("width");
		}
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("height")) {
			height = 1.0;
		}

		if (changes.has("width")) {
			width = 1.0;
		}
	}
}
