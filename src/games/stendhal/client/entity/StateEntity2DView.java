/*
 * @(#) games/stendhal/client/entity/StateEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * The 2D view of an animated entity.
 */
public abstract class StateEntity2DView extends Entity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(StateEntity2DView.class);

	/**
	 * Map of named sprites.
	 */
	protected Map<Object, Sprite>	sprites;

	/**
	 * The model state value changed.
	 */
	protected boolean	stateChanged;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public StateEntity2DView(final Entity entity) {
		super(entity);

		sprites = new HashMap<Object, Sprite>();
		stateChanged = false;
	}


	//
	// StateEntity2DView
	//

	/**
	 * Build animations.
	 */
	protected void buildAnimations() {
		buildSprites(sprites);
	}


	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	protected abstract void buildSprites(final Map<Object, Sprite> map);


	/**
	 * Get a keyed state sprite.
	 *
	 * @param	state		The state.
	 *
	 * @return	The appropriete sprite for the given state.
	 */
	protected Sprite getSprite(final Object state) {
		return sprites.get(state);
	}


	/**
	 * Get the current model state.
	 *
	 * @return	The model state.
	 */
	protected abstract Object getState();


	/**
	 * Get the current animated sprite.
	 *
	 * @return	The appropriete sprite for the current state.
	 */
	protected Sprite getStateSprite() {
		Object state = getState();
		Sprite sprite = getSprite(state);

		if (sprite == null) {
			logger.error("No sprite found for: " + state);
			return SpriteStore.get().getFailsafe();
		}

		return sprite;
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

		setSprite(getStateSprite());
		stateChanged = false;
	}


	/**
	 * Handle updates.
	 */
	@Override
	public void update() {
		super.update();

		if(stateChanged) {
			setSprite(getStateSprite());
			stateChanged = false;
		}
	}
}
