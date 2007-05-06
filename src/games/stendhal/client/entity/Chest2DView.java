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


import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a chest.
 */
public class Chest2DView extends AnimatedStateEntity2DView {
	/**
	 * Create a 2D view of a chest.
	 *
	 * @param	chest		The entity to render.
	 */
	public Chest2DView(final Chest chest) {
		super(chest);
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
	protected void buildSprites(Map<String, AnimatedSprite> map) {
		String resource = translate(entity.getType());
		SpriteStore store = SpriteStore.get();

		map.put("close", store.getAnimatedSprite(resource, 0, 1, 1, 1, 0L, false));
		map.put("open", store.getAnimatedSprite(resource, 1, 1, 1, 1, 0L, false));
	}


	/**
	 * Get the default state name.
	 * <strong>All sub-classes MUST provide a <code>close</code>
	 * named animation, or override this method</strong>.
	 */
	@Override
	protected String getDefaultState() {
		return "close";
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
}
