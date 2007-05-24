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

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of food.
 */
public class Food2DView extends AnimatedStateEntity2DView {
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

		this.states = states;
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<Object, Sprite> map) {
		String resource = translate(entity.getType());

		SpriteStore store = SpriteStore.get();

		for(int i = 0; i < states; i++) {
			map.put(Integer.toString(i),
				store.getAnimatedSprite(resource, i, 1, 1, 1, 0L, false));
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
		return 6000;
	}
}
