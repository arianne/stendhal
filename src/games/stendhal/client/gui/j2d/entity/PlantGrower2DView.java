/*
 * @(#) games/stendhal/client/gui/j2d/entity/PlantGrower2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.entity.PlantGrower;

/**
 * The 2D view of a plant grower.
 */
public class PlantGrower2DView extends Entity2DView {
	/**
	 * Create a 2D view of a plant grower.
	 * 
	 * @param plantGrower
	 *            The entity to render.
	 */
	public PlantGrower2DView(final PlantGrower plantGrower) {
		super(plantGrower);
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
