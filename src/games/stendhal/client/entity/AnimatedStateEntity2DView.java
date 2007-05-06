/*
 * @(#) games/stendhal/client/entity/AnimatedStateEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;

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
	 * Map of named sprites.
	 */
	protected Map<String, AnimatedSprite>	sprites;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedStateEntity2DView(final Entity entity) {
		super(entity);

		sprites = new HashMap<String, AnimatedSprite>();
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	protected abstract void buildSprites(Map<String, AnimatedSprite> map);


	/**
	 * Get a named state sprite.
	 *
	 *
	 */
	protected AnimatedSprite getSprite(final String state) {
		return sprites.get(state);
	}


	/**
	 * Get the default state name.
	 */
	protected abstract String getDefaultState();


	protected String getState() {
		return getEntity().getState();
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Build animations.
	 */
	@Override
	protected void buildAnimations() {
		buildSprites(sprites);
	}


	/**
	 * Get the current animated sprite.
	 *
	 *
	 */
	@Override
	protected AnimatedSprite getAnimatedSprite() {
		String state = getState();
		AnimatedSprite sprite = getSprite(state);

		if (sprite == null) {
			logger.error("No sprite found for: " + state);
			return new AnimatedSprite(new Sprite[] { SpriteStore.get().getSprite("data/sprites/failsafe.png") }, 0L, false);
		}

		sprite.reset();

		return sprite;
	}


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	@Override
	protected Sprite getDefaultSprite() {
		AnimatedSprite sprite = getSprite(getDefaultState());

		sprite.stop();
		sprite.reset();

		return sprite;
	}
}
