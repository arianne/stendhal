/*
 * @(#) games/stendhal/client/entity/Door2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import marauroa.common.game.RPAction;

import games.stendhal.client.GameScreen;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

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
			Sprite tiles = store.getSprite(translate(name));

			width = (double) tiles.getWidth() / GameScreen.SIZE_UNIT_PIXELS;
			height = (double) tiles.getHeight() / GameScreen.SIZE_UNIT_PIXELS / 2.0;

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
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		if (door.isOpen()) {
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());

		}
	}


	/**
	 * Get the height.
	 *
	 * @return	The height in tile units.
	 */
	@Override
	public double getHeight() {
		return height;
	}


	/**
	 * Get the width.
	 *
	 * @return	The width in tile units.
	 */
	@Override
	public double getWidth() {
		return width;
	}


	/**
	 * Get the X offset alignment adjustment.
	 *
	 * @return	The X offset (in world units).
	 */
	@Override
	protected double getXOffset() {
		return xoffset;
	}


	/**
	 * Get the Y offset alignment adjustment.
	 *
	 * @return	The Y offset (in world units).
	 */
	@Override
	protected double getYOffset() {
		return yoffset;
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


	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
			case OPEN:
			case CLOSE:
				RPAction rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", door.getID().getObjectID());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
