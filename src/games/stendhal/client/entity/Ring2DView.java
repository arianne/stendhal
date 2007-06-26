/*
 * @(#) games/stendhal/client/entity/Chest2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


/**
 * The 2D view of a ring.
 */
public class Ring2DView extends Item2DView {
	/**
	 * The ring entity.
	 */
	private Ring ring;

	/**
	 * The working sprite.
	 */
	private Sprite working;

	/**
	 * The broken sprite.
	 */
	private Sprite broken;

	/**
	 * The state changed.
	 */
	private boolean	stateChanged;


	/**
	 * Create a 2D view of a chest.
	 *
	 * @param	ring		The entity to render.
	 */
	public Ring2DView(final Ring ring) {
		super(ring);
		this.ring=ring;

		setSprite(getStateSprite());
		stateChanged = false;
	}


	//
	// Entity2DView
	//

	/**
	 * Populate named state sprites.
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildRepresentation() {
		SpriteStore store = SpriteStore.get();

		Sprite tiles = store.getSprite(translate(getClassResourcePath()));
		working = store.getSprite(tiles, 0, 0, 1.0, 1.0);
		broken = store.getSprite(tiles, 0, 1, 1.0, 1.0);

		setSprite(getStateSprite());
		stateChanged = false;
	}


	/**
	 * Get the appropriete sprite for the current state.
	 *
	 * @return	A sprite.
	 */
	protected Sprite getStateSprite() {
		if(ring.isWorking()) {
			return working;
		} else {
			return broken;
		}
	}

	//
	// Entity2DView
	//

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
		return 5000;
	}


	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if(stateChanged) {
			setSprite(getStateSprite());
			stateChanged = false;
		}
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Ring.PROP_WORKING) {
			stateChanged = true;
		}
	}
}
