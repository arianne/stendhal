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
	 * Map of named sprites.
	 */
	protected Map<String, AnimatedSprite>	sprites;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedStateEntity2DView(final AnimatedStateEntity entity) {
		super(entity);

		this.entity = entity;

		sprites = new HashMap<String, AnimatedSprite>();
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
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load sprites for.
	 */
	protected void buildSprites(Map<String, AnimatedSprite> map, RPObject object) {
		Map<String, Sprite []> animations = new HashMap<String, Sprite []>();

		buildAnimations(animations, object);

		/*
		 * Just map the other list for now
		 */
		for(String name : animations.keySet()) {
			sprites.put(name, new AnimatedSprite(animations.get(name), 100L));
		}
	}


	/**
	 * Get a named animated sprite.
	 *
	 *
	 */
	protected AnimatedSprite getAnimatedSprite(final String state) {
		return sprites.get(state);
	}


	/**
	 * Get the default state name.
	 */
	protected abstract String getDefaultState();


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
		buildSprites(sprites, object);
	}


	/**
	 * Get the current animated sprite.
	 *
	 *
	 */
	protected AnimatedSprite getAnimatedSprite() {
		String state = getState();
		AnimatedSprite sprite = getAnimatedSprite(state);

		if (sprite == null) {
			logger.error("No sprite found for: " + state);
			return new AnimatedSprite(new Sprite[] { SpriteStore.get().getSprite("data/sprites/failsafe.png") }, 1000000L);
		}

		sprite.reset();

		return sprite;
	}


	/**
	 * This method gets the default image.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	protected Sprite getDefaultSprite() {
		AnimatedSprite sprite = getAnimatedSprite(getDefaultState());

		sprite.stop();
		sprite.reset();

		return sprite;
	}
}
