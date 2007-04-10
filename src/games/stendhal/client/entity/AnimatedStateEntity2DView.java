/*
 * @(#) games/stendhal/client/entity/AnimatedStateEntity2DView.java
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
public abstract class AnimatedStateEntity2DView extends AnimatedEntity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(AnimatedEntity2DView.class);

	/**
	 * The animated entity this view is for.
	 */
	private AnimatedStateEntity	entity;

	/**
	 * Map of named animations.
	 */
	protected Map<String, Sprite []> animations;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedStateEntity2DView(final AnimatedStateEntity entity) {
		super(entity);

		this.entity = entity;

		animations = new HashMap<String, Sprite []>();
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
	protected abstract void buildAnimations(Map<String, Sprite []> map, RPObject object);


	/**
	 * Get a named animation set.
	 *
	 *
	 */
	protected Sprite [] getAnimation(final String state) {
		return animations.get(state);
	}


	protected String getState() {
		return entity.getState();
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Build animations.
	 *
	 * @param	object		The entity to load animations for.
	 */
	protected void buildAnimations(RPObject object) {
		buildAnimations(animations, object);
	}


	/**
	 * Get the current animation set.
	 *
	 *
	 */
	protected Sprite [] getAnimation() {
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
		return getAnimation()[0];
	}
}
