package games.stendhal.client.gui.j2d.entity;

//
//


/**
 * The 2D view of a walk blocker.
 */
class WalkBlocker2DView extends InvisibleEntity2DView {

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
