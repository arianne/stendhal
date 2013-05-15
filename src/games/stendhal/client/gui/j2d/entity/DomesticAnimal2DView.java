/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.ActiveEntity;
import games.stendhal.client.entity.DomesticAnimal;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Direction;

import java.util.Map;


/**
 * The 2D view of a domestic animal.
 * 
 * @param <T> type of domestic animal
 */
abstract class DomesticAnimal2DView<T extends DomesticAnimal> extends RPEntity2DView<T> {
	/**
	 * The down facing big state.
	 */
	private static final String STATE_BIG_DOWN = "big:move_down";

	/**
	 * The up facing big state.
	 */
	private static final String STATE_BIG_UP = "big:move_up";

	/**
	 * The left facing big state.
	 */
	private static final String STATE_BIG_LEFT = "big:move_left";

	/**
	 * The right facing big state.
	 */
	private static final String STATE_BIG_RIGHT = "big:move_right";

	/**
	 * The idea property changed.
	 */
	private volatile boolean ideaChanged;
	
	/**
	 * The current idea sprite.
	 */
	private Sprite ideaSprite;


	/**
	 * Create a 2D view of a animal.
	 */
	public DomesticAnimal2DView() {
		ideaSprite = null;
		ideaChanged = false;
	}

	//
	// StateEntity
	//

	@Override
	protected Sprite getSprite(final Object state) {
		if (entity.getWeight() < getBigWeight()) {
			return super.getSprite(state);
		}
		switch (((ActiveEntity) entity).getDirection()) {
		case LEFT:
			return sprites.get(STATE_BIG_LEFT);

		case RIGHT:
			return sprites.get(STATE_BIG_RIGHT);

		case UP:
			return sprites.get(STATE_BIG_UP);

		case DOWN:
			return sprites.get(STATE_BIG_DOWN);

		default:
			return sprites.get(STATE_BIG_DOWN);
		}
	}

	//
	// DomesticAnimal2DView
	//

	/**
	 * Get the weight at which the animal becomes big.
	 * 
	 * @return A weight.
	 */
	protected abstract int getBigWeight();

	/**
	 * Get the approriete idea sprite.
	 * 
	 * @return The sprite representing the current idea, or null.
	 */
	private Sprite getIdeaSprite() {
		final String idea = entity.getIdea();

		if (idea == null) {
			return null;
		}

		return SpriteStore.get().getSprite(
				"data/sprites/ideas/" + idea + ".png");
	}

	//
	// RPEntity2DView
	//

	/**
	 * Populate named state sprites.
	 * 
	 * @param map
	 *            The map to populate.
	 * @param tiles
	 *            The master sprite.
	 * @param width
	 *            The image width (in pixels).
	 * @param height
	 *            The image height (in pixels).
	 */
	@Override
	protected void buildSprites(final Map<Object, Sprite> map,
			final Sprite tiles, final int width, final int height) {
		int y = 0;
		map.put(Direction.UP, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.RIGHT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.DOWN, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(Direction.LEFT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_UP, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_RIGHT, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_DOWN, createWalkSprite(tiles, y, width, height));

		y += height;
		map.put(STATE_BIG_LEFT, createWalkSprite(tiles, y, width, height));
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		ZoneInfo info = ZoneInfo.get();
		return SpriteStore.get().getModifiedSprite(translate(entity.getType()), 
				info.getZoneColor(), info.getColorMethod());
	}

	/**
	 * Get the number of tiles in the Y axis of the base sprite.
	 * 
	 * @return The number of tiles.
	 */
	@Override
	protected int getTilesY() {
		return 8;
	}

	//
	// ActiveEntity2DView
	//

	//
	// Entity2DView
	//

	/**
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (ideaChanged) {
			ideaChanged = false;
			detachSprite(ideaSprite);
			ideaSprite = getIdeaSprite();
			if (ideaSprite != null) {
				attachSprite(ideaSprite, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 8, -8);
			}
		}
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == DomesticAnimal.PROP_IDEA) {
			ideaChanged = true;
		} else if (property == DomesticAnimal.PROP_WEIGHT) {
			proceedChangedState(entity);
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
		if (isReleased()) {
			return;
		}
		switch (at) {
		case OWN:
			at.send(at.fillTargetInfo(entity));
			break;

		default:
			super.onAction(at);
			break;
		}
	}


	/**
	 * gets the mouse cursor image to use for this entity
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
}
