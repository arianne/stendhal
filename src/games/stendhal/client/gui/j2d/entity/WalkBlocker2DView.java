package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.gui.styled.cursor.StendhalCursor;

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


	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	public StendhalCursor getCursor() {
		return StendhalCursor.NORMAL;
	}
}
