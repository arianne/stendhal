/*
 * @(#) games/stendhal/client/gui/j2d/entity/Item2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of an game board.
 */
class GameBoard2DView extends Entity2DView {

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		final SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate(getClassResourcePath()));
		setSprite(sprite);
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
		return 1000;
	}

	/**
	 * Translate a resource name into it's sprite image path.
	 * 
	 * @param name
	 *            The resource name.
	 * 
	 * @return The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/gameboard/" + name + ".png";
	}


	@Override
	public boolean isInteractive() {
		return false;
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.WALK;
	}

}
