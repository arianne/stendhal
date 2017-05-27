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


import java.awt.Color;
import java.awt.Graphics2D;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.j2d.entity.helpers.DrawingHelper;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.client.sprite.TextSprite;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * The 2D view of a spell.
 */
class Spell2DView extends Entity2DView<IEntity> {

	private static final Logger logger = Logger.getLogger(Spell2DView.class);

	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 */
	@Override
	protected void buildRepresentation(IEntity entity) {
		String translate = translate(getClassResourcePath());
		logger.debug("Sprite path: " + translate);
		setSprite(SpriteStore.get()
				.getSprite(translate));
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
		return 7000;
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
		String translatedname = translateName(name);
		return "data/sprites/spells/" + translatedname + ".png";
	}

	private String translateName(String name) {
		return name.replaceAll(" ", "_");
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
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
		onAction(ActionType.USE);
	}

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
		case USE:
			j2DClient.get().switchToSpellState(this.entity.getRPObject());
			break;

		default:
			super.onAction(at);
			break;
		}
	}

	/**
	 * Additionally draws the position of this spell in the spells slot
	 */
	@Override
	protected void draw(Graphics2D g2d, int x, int y, int width, int height) {
		super.draw(g2d, x, y, width, height);
		TextSprite positionSprite = TextSprite.createTextSprite(getPositionInSlot(), Color.WHITE);
		DrawingHelper.drawAlignedSprite(g2d, positionSprite, HorizontalAlignment.LEFT, VerticalAlignment.TOP, x, y, width, height);
	}


	private String getPositionInSlot() {
		RPSlot slot = this.entity.getRPObject().getContainerSlot();
		Integer position = Integer.valueOf(1);
		for (RPObject spell : slot) {
			if(spell.equals(this.entity.getRPObject())) {
				break;
			}
			position = Integer.valueOf(position.intValue() + 1);
		}
		return position.toString();
	}

	@Override
	public boolean isMovable() {
		return true;
	}
}
