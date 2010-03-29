/*
 * @(#) games/stendhal/client/gui/j2d/entity/Blood2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.Blood;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.Map;

/**
 * The 2D view of blood.
 */
class Blood2DView extends StateEntity2DView {

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		final SpriteStore store = SpriteStore.get();
		final Sprite tiles = store.getSprite("data/sprites/combat/blood_red.png");

		final int theight = tiles.getHeight();
		int i = 0;

		for (int y = 0; y < theight; y += IGameScreen.SIZE_UNIT_PIXELS) {
			map.put(Integer.valueOf(i++), store.getTile(tiles, 0, y,
					IGameScreen.SIZE_UNIT_PIXELS,
					IGameScreen.SIZE_UNIT_PIXELS));
		}
	}

	/**
	 * Get the current entity state.
	 * 
	 * @return The current state.
	 */
	@Override
	protected Object getState() {
		return ((Blood) entity).getAmount();
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
		return 2000;
	}

	@Override
	public boolean isInteractive() {
		return false;
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
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == Blood.PROP_AMOUNT) {
			proceedChangedState();
		}
	}


	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.WALK;
	}

}
