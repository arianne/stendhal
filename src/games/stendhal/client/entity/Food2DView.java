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

import marauroa.common.game.RPObject;

import games.stendhal.client.AnimatedSprite;
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
	 * @param	object		The entity to load sprites for.
	 */
	protected void buildSprites(Map<String, AnimatedSprite> map, RPObject object) {
		String resource = translate(object.get("type"));

		SpriteStore store = SpriteStore.get();

		for(int i = 0; i < states; i++) {
			map.put(Integer.toString(i),
				store.getAnimatedSprite(resource, i, 1, 1, 1, 0L, false));
		}
	}


	/**
	 * Get the default state name.
	 * <strong>All sub-classes MUST provide a <code>0</code>
	 * named animation, or override this method</strong>.
	 */
	@Override
	protected String getDefaultState() {
		return "0";
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
	public int getZIndex() {
		return 6000;
	}
}
