/*
 * @(#) games/stendhal/client/entity/Chest2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a chest.
 */
public class Chest2DView extends StateEntity2DView {
	/*
	 * The closed state.
	 */
	protected final static String	STATE_CLOSED	= "close";

	/*
	 * The open state.
	 */
	protected final static String	STATE_OPEN	= "open";

	/**
	 * The chest entity.
	 */
	protected Chest		chest;


	/**
	 * Create a 2D view of a chest.
	 *
	 * @param	chest		The entity to render.
	 */
	public Chest2DView(final Chest chest) {
		super(chest);

		this.chest = chest;
	}


	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		SpriteStore store = SpriteStore.get();

		Sprite tiles = store.getSprite(translate(entity.getType()));

		map.put(STATE_CLOSED, store.getSprite(tiles, 0, 0, 1.0, 1.0));
		map.put(STATE_OPEN, store.getSprite(tiles, 0, 1, 1.0, 1.0));
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	protected Object getState() {
		return chest.isOpen() ? STATE_OPEN : STATE_CLOSED;
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

		if(property == Chest.PROP_OPEN) {
			stateChanged = true;
		}
	}
}
