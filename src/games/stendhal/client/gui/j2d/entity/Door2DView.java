/*
 * @(#) games/stendhal/client/gui/j2d/entity/Door2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import marauroa.common.game.RPAction;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Door;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;
import java.util.Map;

/**
 * The 2D view of a door.
 */
public class Door2DView extends StateEntity2DView {
	/*
	 * The closed state.
	 */
	protected static final String STATE_CLOSED = "close";

	/*
	 * The open state.
	 */
	protected static final String STATE_OPEN = "open";

	/*
	 * The drawn width.
	 */
	protected int width;

	/*
	 * The drawn height.
	 */
	protected int height;

	/**
	 * The door entity.
	 */
	private Door door;

	/**
	 * Create a 2D view of a door.
	 *
	 * @param entity
	 *            The entity to render.
	 */
	public Door2DView(final Door door) {
		super(door);

		this.door = door;

		width = IGameScreen.SIZE_UNIT_PIXELS;
		height = IGameScreen.SIZE_UNIT_PIXELS;
	}

	//
	// StateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param map
	 *            The map to populate.
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map) {
		String name = door.getEntityClass();

		SpriteStore store = SpriteStore.get();

		if (name == null) {
			width = IGameScreen.SIZE_UNIT_PIXELS;
			height = IGameScreen.SIZE_UNIT_PIXELS;

			Sprite emptySprite = store.getEmptySprite(width, height);

			map.put(STATE_OPEN, emptySprite);
			map.put(STATE_CLOSED, emptySprite);
		} else {
			Sprite tiles = store.getSprite(translate(name));

			width = tiles.getWidth();
			height = tiles.getHeight() / 2;

			map.put(STATE_OPEN, store.getTile(tiles, 0, 0, width, height));
			map.put(STATE_CLOSED, store.getTile(tiles, 0, height, width, height));
		}

		calculateOffset(width, height);
	}

	/**
	 * Get the current entity state.
	 *
	 * @return The current state.
	 */
	@Override
	protected Object getState() {
		return door.isOpen() ? STATE_OPEN : STATE_CLOSED;
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 *
	 * @param list
	 *            The list to populate.
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
	 * @return The height (in pixels).
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width.
	 *
	 * @return The width (in pixels).
	 */
	@Override
	public int getWidth() {
		return width;
	}

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
	 * Translate a resource name into it's sprite image path.
	 *
	 * @param name
	 *            The resource name.
	 *
	 * @return The full resource name.
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == Entity.PROP_CLASS) {
			representationChanged = true;
		} else if (property == Door.PROP_OPEN) {
			stateChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case OPEN:
		case CLOSE:
			RPAction rpaction = new RPAction();

			rpaction.put("type", at.toString());
			door.fillTargetInfo(rpaction);

			at.send(rpaction);
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
