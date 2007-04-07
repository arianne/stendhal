/*
 * @(#) games/stendhal/client/entity/AnimatedEntity2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import marauroa.common.Log4J;

import org.apache.log4j.Logger;

//
//

/**
 * The 2D view of an animated entity.
 */
public class AnimatedEntity2DView extends Entity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(AnimatedEntity2DView.class);

	/**
	 * The animated entity this view is for.
	 */
	private AnimatedEntity	entity;

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
	 * The current sprite.
	 * This moves up to Entity2DView after it gets full impl.
	 */
	protected Sprite	sprite;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedEntity2DView(final AnimatedEntity entity) {
		super(entity);

		this.entity = entity;

		frame = 0;
		delta = 0L;
	}


	//
	// AnimatedEntity2DView
	//

	/** Returns the next Sprite we have to show */
	private Sprite nextFrame() {
		Sprite[] anim = entity.getSprites(entity.getAnimation());

		if (anim == null) {
			logger.error("getSprites() returned null for " + entity.getAnimation());
			return SpriteStore.get().getSprite("data/sprites/failsafe.png");
		}

		if (frame == anim.length) {
			frame = 0;
		}

		Sprite sprite = anim[frame];

		if (!entity.stopped()) {
			frame++;
		}

		return sprite;
	}


	//
	// Entity2DView
	//

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
