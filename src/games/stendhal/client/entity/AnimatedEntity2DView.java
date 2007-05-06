/*
 * @(#) games/stendhal/client/entity/AnimatedEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;

/**
 * The 2D view of an animated entity.
 */
public abstract class AnimatedEntity2DView extends Entity2DView {
	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedEntity2DView(final Entity entity) {
		super(entity);
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Build animations.
	 */
	protected abstract void buildAnimations();


	/**
	 * Get the current animated sprite.
	 *
	 *
	 */
	protected abstract AnimatedSprite getAnimatedSprite();


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	protected abstract Sprite getDefaultSprite();


	protected boolean isAnimating() {
		return !entity.stopped();
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 * This builds all the animation sprites and sets the default frame.
	 */
	@Override
	protected void buildRepresentation() {
		buildAnimations();

		sprite = getDefaultSprite();
	}


	/**
	 * Update representation.
	 */
	@Override
	protected void update() {
		super.update();

		AnimatedSprite sprite = getAnimatedSprite();

		if(isAnimating()) {
			sprite.start();
		} else {
			sprite.stop();
			sprite.reset();
		}

		this.sprite = sprite;
	}
}
