/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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


import games.stendhal.client.IGameScreen;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.BreakableRing;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;


/**
 * The 2D view of a ring.
 */
class BreakableRing2DView extends Item2DView<BreakableRing> {

	/**
	 * The working sprite.
	 */
	private Sprite working;

	/**
	 * The broken sprite.
	 */
	private Sprite broken;

	/**
	 * The state changed.
	 */
	private volatile boolean stateChanged;

	/**
	 * Create a 2D view of a chest.
	 */
	public BreakableRing2DView() {
		super();

		stateChanged = false;
	}

	@Override
	public void initialize(final BreakableRing entity) {
		super.initialize(entity);
		setSprite(getStateSprite());
	}

	//
	// Entity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 */
	@Override
	protected void buildRepresentation(BreakableRing entity) {
		final SpriteStore store = SpriteStore.get();
		Sprite tiles;
		if (isContained()) {
			tiles = store.getSprite(translate(getClassResourcePath()));
		} else {
			ZoneInfo info = ZoneInfo.get();
			tiles = store.getModifiedSprite(translate(getClassResourcePath()),
					info.getZoneColor(), info.getColorMethod());
		}

		working = store.getTile(tiles, 0, 0, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS);
		broken = store.getTile(tiles, 0, IGameScreen.SIZE_UNIT_PIXELS,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS);

		setSprite(getStateSprite());
		stateChanged = false;
	}

	/**
	 * Get the appropriate sprite for the current state.
	 *
	 * @return A sprite.
	 */
	private Sprite getStateSprite() {
		if (entity.isWorking()) {
			return working;
		} else {
			return broken;
		}
	}

	//
	// Entity2DView
	//

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
	 * Handle updates.
	 */
	@Override
	protected void update() {
		super.update();

		if (stateChanged) {
			stateChanged = false;
			setSprite(getStateSprite());
		}
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == BreakableRing.PROP_WORKING) {
			stateChanged = true;
		}
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(ActionType.LOOK);
	}
}
