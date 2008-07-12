/*
 * @(#) games/stendhal/client/gui/j2d/entity/AnimatedLoopEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Fire;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import org.apache.log4j.Logger;

/**
 * The 2D view of an entity that always loops images.
 */
class AnimatedLoopEntity2DView extends Entity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger
			.getLogger(AnimatedLoopEntity2DView.class);

	/**
	 * Create a 2D view of an animated loop visual.
	 * 
	 * @param entity
	 *            The entity to render.
	 */
	public AnimatedLoopEntity2DView(final Entity entity) {
		super(entity);
	}

	/**
	 * Create a 2D view of an animated loop visual.
	 * 
	 * @param entity
	 *            The entity to render.
	 */
	public AnimatedLoopEntity2DView(final Fire entity) {
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
		final SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate(entity.getType()));

		/*
		 * Entities are [currently] always 1x1. Extra columns are animation.
		 * Extra rows are ignored.
		 */
		final int width = sprite.getWidth();

		if (width > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getAnimatedSprite(sprite, 0, 0, width
					/ IGameScreen.SIZE_UNIT_PIXELS,
					IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
					100);
		} else if (sprite.getHeight() > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getTile(sprite, 0, 0, IGameScreen.SIZE_UNIT_PIXELS,
					IGameScreen.SIZE_UNIT_PIXELS);
			logger.warn("Multi-row image for: " + translate(entity.getType()));
		}

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
		return 3000;
	}
}
