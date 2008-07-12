/*
 * @(#) games/stendhal/client/gui/j2d/entity/InvisibleEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.InvisibleEntity;

import java.awt.Graphics2D;

/**
 * The 2D view of an invisible entity.
 */
class InvisibleEntity2DView extends Entity2DView {
	/**
	 * Create a 2D view of an entity.
	 * 
	 * @param entity
	 *            The entity to render.
	 */
	public InvisibleEntity2DView(final InvisibleEntity entity) {
		super(entity);
	}

	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(final IGameScreen gameScreen) {
	}

	/**
	 * Draw the entity (NOT!).
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	public void draw(final Graphics2D g2d, final IGameScreen gameScreen) {
		applyChanges(gameScreen);
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
		return 5000;
	}
}
