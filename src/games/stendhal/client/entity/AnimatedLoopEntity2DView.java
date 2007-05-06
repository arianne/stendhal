/*
 * @(#) games/stendhal/client/entity/AnimatedLoopEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of an entity that always loops images.
 */
public class AnimatedLoopEntity2DView extends Entity2DView {
	/**
	 * The number of frames.
	 */
	protected int	frames;


	/**
	 * Create a 2D view of an animated loop visual.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedLoopEntity2DView(final Entity entity, int frames) {
		super(entity);

		this.frames = frames;
	}


	//
	// AnimatedLoopEntity2DView
	//

	/**
	 * Populate animation.
	 */
	protected Sprite getAnimatedSprite() {
		String resource = translate(entity.getType());
		SpriteStore store = SpriteStore.get();

		return new AnimatedSprite(store.getSprites(resource, 0, frames, 1, 1), 100L);
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 * This the animation sprite.
	 */
	@Override
	protected void buildRepresentation() {
		sprite = getAnimatedSprite();
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
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
