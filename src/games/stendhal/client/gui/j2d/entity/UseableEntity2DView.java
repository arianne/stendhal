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
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;

/**
 * The 2D view of a useable entity
 */
class UseableEntity2DView extends Entity2DView {

	private ActionType action;

	/**
	 * creates a new UseableEntity2DView
	 */
	public UseableEntity2DView() {
		this.action = ActionType.USE;
	}

	/**
	 * creates a new USeableEntity2DView
	 *
	 * @param action use action
	 */
	public UseableEntity2DView(ActionType action) {
		this.action = action;
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildRepresentation() {
		final SpriteStore store = SpriteStore.get();
		Sprite sprite = store.getSprite(translate("useable/" + entity.getType()));

		/*
		 * Entities are [currently] always 1x1. Extra columns are animation.
		 * Extra rows are ignored.
		 */
		final int imageWidth = sprite.getWidth();
		final int width = (int) entity.getWidth();
		final int height = (int) entity.getHeight();

		if (imageWidth > IGameScreen.SIZE_UNIT_PIXELS) {
			sprite = store.getAnimatedSprite(sprite,
					0, 0,
					imageWidth / IGameScreen.SIZE_UNIT_PIXELS / width,
					IGameScreen.SIZE_UNIT_PIXELS * width,
					IGameScreen.SIZE_UNIT_PIXELS * height,
					100);
		} 

		setSprite(sprite);
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
		return 3000;
	}

	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.ACTIVITY;
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
		list.add(action.getRepresentation());

		super.buildActions(list);
	}

	//
	// EntityView
	//

	/**
	 * Perform the default action.
	 */
	@Override
	public void onAction() {
		onAction(action);
	}

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		if (at == this.action) {
			at.send(at.fillTargetInfo(entity.getRPObject()));
		} else {
			super.onAction(at);
		}
	}
}
