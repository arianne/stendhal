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

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * The 2D view of an animated entity.
 */
public abstract class AnimatedEntity2DView extends Entity2DView {
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
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 */
	public abstract void buildAnimations(Map<String, Sprite []> map, RPObject object);


	/**
	 * Get a named animation set.
	 *
	 *
	 */
	public Sprite [] getAnimation(final String state) {
		return animations.get(state);
	}


	protected String getState() {
		return entity.getState();
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Get the current animation set.
	 *
	 *
	 */
	public Sprite [] getAnimation() {
		String state = getState();
		Sprite[] anim = getAnimation(state);

		if (anim == null) {
			logger.error("getSprites() returned null for " + state);
			return new Sprite[] { SpriteStore.get().getSprite("data/sprites/failsafe.png") };
		}

		return anim;
	}


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	protected Sprite getDefaultSprite() {
		return null;
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
		buildAnimations(animations, object);

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
