/*
 * @(#) games/stendhal/client/gui/j2d/entity/AnimatedLoopEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of an entity that always loops images.
 */
public class AnimatedLoopEntity2DView extends Entity2DView {
	/**
	 * Create a 2D view of an animated loop visual.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedLoopEntity2DView(final Entity entity) {
		super(entity);
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation() {
		SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate(entity.getType()));

		/*
		 * Entities are [currently] always 1x1.
		 * Extra columns are animation.
		 * Extra rows are ignored.
		 */
		int width = sprite.getWidth();

		if(width > GameScreen.SIZE_UNIT_PIXELS) {
			setSprite(store.getAnimatedSprite(sprite, 0, width / GameScreen.SIZE_UNIT_PIXELS, 1.0, 1.0, 100, true));
		} else if(sprite.getHeight() > GameScreen.SIZE_UNIT_PIXELS) {
			setSprite(store.getSprite(sprite, 0, 0, 1.0, 1.0));
//			logger.info("WARNING: Multi-row image for: " + entity.getType());
		} else {
			setSprite(sprite);
		}
	}


	/**
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 3000;
	}
}
