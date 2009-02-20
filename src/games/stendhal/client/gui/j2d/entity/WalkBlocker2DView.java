package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.WalkBlocker;

/**
 * The 2D view of a walk blocker
 */
class WalkBlocker2DView extends InvisibleEntity2DView {
	/**
	 * Create a 2D view of a walk blocker.
	 * 
	 * @param walkBlocker
	 *            The entity to render.
	 */
	public WalkBlocker2DView(final WalkBlocker walkBlocker) {
		super(walkBlocker);
	}

	//
	// Entity2DView
	//

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
}
