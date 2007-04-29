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

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a door.
 */
public class Door2DView extends AnimatedStateEntity2DView {
	/*
	 * The drawing X offset from the entity position.
	 */
	protected double	xoffset;

	/*
	 * The drawing Y offset from the entity position.
	 */
	protected double	yoffset;

	/*
	 * The drawn width.
	 */
	protected double	width;

	/*
	 * The drawn height.
	 */
	protected double	height;

	/**
	 * The door entity.
	 */
	private Door		door;


	/**
	 * Create a 2D view of a door.
	 *
	 * @param	entity		The entity to render.
	 */
	public Door2DView(final Door door) {
		super(door);

		this.door = door;

		xoffset = 0.0;
		yoffset = 0.0;
		width = 1.0;
		height = 1.0;
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
		String name = object.get("class");

		switch(door.getOrientation()) {
			case LEFT:
				name += "_w";
				xoffset = 0.0;
				yoffset = -1.0;
				width = 2.0;
				height = 3.0;
				break;

			case RIGHT:
				name += "_e";
				xoffset = -1.0;
				yoffset = -1.0;
				width = 2.0;
				height = 3.0;
				break;

			case UP:
				name += "_n";
				xoffset = -1.0;
				yoffset = 0.0;
				width = 3.0;
				height = 2.0;
				break;

			case DOWN:
				name += "_s";
				xoffset = -1.0;
				yoffset = -1.0;
				width = 3.0;
				height = 2.0;
				break;

			default:
				xoffset = 0.0;
				yoffset = 0.0;
				width = 1.0;
				height = 1.0;
		}

		String resource = "data/sprites/doors/" + name + ".png";
		SpriteStore store = SpriteStore.get();

		map.put("open", store.getAnimatedSprite(resource, 0, 1, width, height, 0L, false));
		map.put("close", store.getAnimatedSprite(resource, 1, 1, width, height, 0L, false));
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
		return new Rectangle.Double(getX() + xoffset, getY() + yoffset, width, height);
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
