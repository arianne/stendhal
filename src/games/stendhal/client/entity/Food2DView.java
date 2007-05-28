/*
 * @(#) games/stendhal/client/entity/Food2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of food.
 */
public class Food2DView extends StateEntity2DView {
	/**
	 * The food entity.
	 */
	protected Food	food;

	/**
	 * The number of states.
	 */
	protected int	states;


	/**
	 * Create a 2D view of food.
	 *
	 * @param	entity		The entity to render.
	 * @param	states		The number of states.
	 */
	public Food2DView(final Food food, int states) {
		super(food);

		this.food = food;
		this.states = states;
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

		for(int i = 0; i < states; i++) {
			map.put(new Integer(i), store.getSprite(tiles, 0, i, 1.0, 1.0));
		}
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	protected Object getState() {
		return new Integer(food.getAmount());
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
		return 6000;
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

		if(property == Food.PROP_AMOUNT) {
			stateChanged = true;
		}
	}
}
