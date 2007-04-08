/*
 * @(#) games/stendhal/client/entity/AnimatedEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.common.Direction;

/**
 * The 2D view of an animated entity.
 */
public class AnimatedEntity2DView extends Entity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(AnimatedEntity2DView.class);

	/**
	 * The current named animation.
	 */
	protected String	animation;

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
	 * Map of named animations.
	 */
	protected Map<String, Sprite []> animations;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedEntity2DView(final AnimatedEntity entity) {
		super(entity);

		this.entity = entity;

		animations = new HashMap<String, Sprite []>();
		frame = 0;
		delta = 0L;
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Populate the named animations.
	 *
	 * @param	object		The entity to load animations for.
	 */
	protected void buildAnimations(RPObject object) {
		// TEMP - eventually make abstract
	}


	/**
	 * Get a named animation set.
	 *
	 *
	 */
	public Sprite [] getAnimation(final String state) {
		// XXX - For now use entity
		return entity.getSprites(state);
//		return animations.get(state);
	}


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	protected Sprite getDefaultSprite() {
		return null;
	}


	/** Returns the next Sprite we have to show */
	private Sprite nextFrame() {
		Sprite[] anim = getAnimation(entity.getState());

		if (anim == null) {
			logger.error("getSprites() returned null for " + entity.getAnimation());
			return SpriteStore.get().getSprite("data/sprites/failsafe.png");
		}

		if (frame >= anim.length) {
			frame = 0;
		}

		Sprite tempSprite = anim[frame];

		if (!entity.stopped()) {
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
