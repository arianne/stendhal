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

import games.stendhal.common.Direction;
import games.stendhal.client.GameScreen;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a door.
 */
public class Door2DView extends StateEntity2DView {
	/*
	 * The closed state.
	 */
	protected final static String	STATE_CLOSED	= "close";

	/*
	 * The open state.
	 */
	protected final static String	STATE_OPEN	= "open";

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
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		String name = door.getEntityClass();

		SpriteStore store = SpriteStore.get();

		if(name == null) {
			Sprite emptySprite = store.getEmptySprite();

			width = 1.0;
			height = 1.0;

			xoffset = 0.0;
			yoffset = 0.0;

			map.put(STATE_OPEN, emptySprite);
			map.put(STATE_CLOSED, emptySprite);
		} else {
			/*
			 * TODO: Remove orientation adjustement on name
			 * just prior to 0.62 release.
			 */
			Direction orientation = door.getOrientation();

			if(orientation != null) {
				switch(orientation) {
					case LEFT:
						name += "_w";
						break;

					case RIGHT:
						name += "_e";
						break;

					case UP:
						name += "_n";
						break;

					case DOWN:
						name += "_s";
						break;
				}
			}

			Sprite tiles = store.getSprite(translate(name));

			width = tiles.getWidth() / GameScreen.SIZE_UNIT_PIXELS;
			height = tiles.getHeight() / GameScreen.SIZE_UNIT_PIXELS / 2;

			xoffset = -(width - 1.0) / 2.0;
			yoffset = -(height - 1.0) / 2.0;

			map.put(STATE_OPEN, store.getSprite(tiles, 0, 0, width, height));
			map.put(STATE_CLOSED, store.getSprite(tiles, 0, 1, width, height));
		}
	}


	/**
	 * Get the current entity state.
	 *
	 * @return	The current state.
	 */
	@Override
	protected Object getState() {
		return door.isOpen() ? STATE_OPEN : STATE_CLOSED;
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
	@Override
	public int getZIndex() {
		return 5000;
	}


	/**
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param	name		The resource name.
	 *
	 * @return	The full resource name.
	 */
	@Override
	protected String translate(final String name) {
		return "data/sprites/doors/" + name + ".png";
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

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if(property == Door.PROP_OPEN) {
			stateChanged = true;
		}
	}
}
