/*
 * @(#) games/stendhal/client/gui/j2d/entity/SheepFood2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//


/**
 * The 2D view of sheep food.
 */
class SheepFood2DView extends Food2DView {
	/**
	 * Create a 2D view of sheep food.
	 */
	public SheepFood2DView() {
		super.setStates(6);
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
		return 5000;
	}
}
