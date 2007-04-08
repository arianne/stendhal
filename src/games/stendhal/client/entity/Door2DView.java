/*
 * @(#) games/stendhal/client/entity/Door2DView.java
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

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a door.
 */
public class Door2DView extends AnimatedEntity2DView {
	protected double	width;
	protected double	height;


	/**
	 * Create a 2D view of a door.
	 *
	 * @param	entity		The entity to render.
	 */
	public Door2DView(final Door door) {
		super(door);

		width = 1.0;
		height = 1.0;
	}


	//
	// <AnimatedStateEntity2DView>
	//

	/**
	 * Populate named state animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 */
	public void buildAnimations(Map<String, Sprite []> map, RPObject object) {
		SpriteStore store = SpriteStore.get();

		String clazz = object.get("class");
		String direction = null;

		switch (object.getInt("dir")) {
			case 4:
				direction = "w";
				break;
			case 2:
				direction = "e";
				break;
			case 1:
				direction = "n";
				break;
			case 3:
				direction = "s";
				break;
		}

		if (direction.equals("n") || direction.equals("s")) {
			width = 3.0;
			height = 2.0;
		} else {
			width = 2.0;
			height = 3.0;
		}

		map.put("open", store.getAnimatedSprite("data/sprites/doors/" + clazz + "_" + direction + ".png", 0, 1, width, height));

		map.put("close", store.getAnimatedSprite("data/sprites/doors/" + clazz + "_" + direction + ".png", 1, 1, width, height));
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * This method gets the default image.
	 * <strong>All sub-classes MUST provide a <code>close</code>
	 * named animation, or override this method</strong>.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	@Override
	protected Sprite getDefaultSprite() {
		return getAnimation("close")[0];
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
		return new Rectangle.Double(getX(), getY(), width, height);
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
		return 5000;
	}
}
