/*
 * @(#) games/stendhal/client/entity/AnimatedEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.Sprite;
import marauroa.common.game.RPObject;

/**
 * The 2D view of an animated entity.
 */
public abstract class AnimatedEntity2DView extends Entity2DView {

	/**
	 * The animated entity this view is for.
	 */
	private Entity		entity;

	/**
	 * The frame number.
	 */
	protected int		frame;

	/**
	 * We need to measure time to have a coherent frame rendering,
	 * that is what delta is for.
	 */
	protected long		delta;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedEntity2DView(final Entity entity) {
		super(entity);

		this.entity = entity;

		frame = 0;
		delta = 0L;
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Build animations.
	 *
	 * @param	object		The entity to load animations for.
	 */
	protected abstract void buildAnimations(RPObject object);


	/**
	 * Get the current animation set.
	 *
	 *
	 */
	protected abstract Sprite [] getAnimation();


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	protected Sprite getDefaultSprite() {
		return getAnimation()[0];
	}


	protected boolean isAnimating() {
		return !entity.stopped();
	}


	/**
	 * Get the next sprite we have to show.
	 *
	 *
	 */
	private Sprite nextFrame() {
		Sprite[] anim = getAnimation();

		if (frame >= anim.length) {
			frame = 0;
		}

		Sprite tempSprite = anim[frame];

		if (isAnimating()) {
			frame++;
		}

		return tempSprite;
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 * This builds all the animation sprites and sets the default frame.
	 */
	@Override
	protected void buildRepresentation(final RPObject object) {
		buildAnimations(object);

		sprite = getDefaultSprite();
	}


	/**
	 * Get the sprite image for this entity.
	 *
	 * @return	The image representation.
	 */
	protected Sprite getSprite() {
		if ((System.currentTimeMillis() - delta) > 100L) {
			sprite = nextFrame();
			delta = System.currentTimeMillis();
		}

		return sprite;
	}
}
