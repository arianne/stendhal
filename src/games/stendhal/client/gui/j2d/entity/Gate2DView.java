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

import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class Gate2DView extends Entity2DView {
	static final HashMap<String, Sprite[]> sprites = new HashMap<String, Sprite[]>();

	private Sprite openSprite, closedSprite;

	@Override
	public void initialize(IEntity entity) {
		super.initialize(entity);
		final RPObject rpobject = entity.getRPObject();
		final String baseImage = rpobject.get("image");
		final String orientation = rpobject.get("orientation"); 
		
		String imageName = "data/sprites/doors/" + baseImage + "_" + orientation +".png";
		Sprite[] s = sprites.get(imageName);
		if (s == null) {
			Sprite sprite = SpriteStore.get().getSprite(imageName);
			s = new Sprite[2];
			s[0] = sprite.createRegion(0, 0, 96, 96, null);
			s[1] = sprite.createRegion(0, 96, 96, 96, null);
			sprites.put(imageName, s);
		}
		
		openSprite = s[0];
		closedSprite = s[1];
	}

	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());
	}

	@Override
	protected void buildRepresentation(IEntity entity) {
		// not needed
	}

	@Override
	public void onAction() {
		onAction(ActionType.USE);
	}

	@Override
	public void onAction(final ActionType at) {
		if (entity == null) {
			Logger.getLogger(Entity2DView.class).error(
					"View already released - action not processed: " + at);
			return;
		}

		switch (at) {
		case USE:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;
		default:
			super.onAction(at);
			break;
		}
	}
	
	/**
	 * Check if the gate is open.
	 * 
	 * @return <code>true</code> iff the gate is open
	 */
	private boolean isOpen() {
		return entity.getResistance() == 0;
	}
	
	@Override
	protected void drawEntity(final Graphics2D g2d, final int x, final int y, final int width,
			final int height) {
		if (isOpen()) {
			openSprite.draw(g2d, x - 32, y - 32);
		} else {
			closedSprite.draw(g2d, x - 32, y - 32);
		}
	}


	@Override
	public int getZIndex() {
		return 0;
	}
}

