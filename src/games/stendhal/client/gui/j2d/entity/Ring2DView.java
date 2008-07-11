/*
 * @(#) games/stendhal/client/gui/j2d/entity/Chest2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Ring;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

/**
 * The 2D view of a ring.
 */
class Ring2DView extends Item2DView {
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
	private boolean stateChanged;

	/**
	 * Create a 2D view of a chest.
	 * 
	 * @param ring
	 *            The entity to render.
	 */
	public Ring2DView(final Ring ring) {
		super(ring);
		this.ring = ring;

		setSprite(getStateSprite());
		stateChanged = false;
	}

	//
	// Entity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 */
	@Override
	protected void buildRepresentation() {
		SpriteStore store = SpriteStore.get();
		Sprite tiles = store.getSprite(translate(getClassResourcePath()));

		working = store.getTile(tiles, 0, 0, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS);
		broken = store.getTile(tiles, 0, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS);

		setSprite(getStateSprite());
		stateChanged = false;
	}

	/**
	 * Get the appropriate sprite for the current state.
	 * 
	 * @return A sprite.
	 */
	protected Sprite getStateSprite() {
		if (ring.isWorking()) {
			return working;
		} else {
			return broken;
		}
	}

	//
	// Entity2DView
	//

	/**
	 * Determines on top of which other entities this entity should be drawn.
	 * Entities with a high Z index will be drawn on top of ones with a lower Z
	 * index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return The drawing index.
	 */
	@Override
	public int getZIndex() {
		return 5000;
	}

    /**                                                                                                                                                      
     * Build a list of entity specific actions. <strong>NOTE: The first entry                                                                                
     * should be the default.</strong>                                                                                                                       
     *                                                                                                                                                       
     * @param list                                                                                                                                           
     *            The list to populate.                                                                                                                      
     */
	@Override
		protected void buildActions(final List<String> list) {
        list.add(ActionType.USE.getRepresentation());

		super.buildActions(list);
    }

	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (stateChanged) {
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == Ring.PROP_WORKING) {
			stateChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.LOOK);
	}
}
