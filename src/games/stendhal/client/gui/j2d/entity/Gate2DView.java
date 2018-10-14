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

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteCache;
import games.stendhal.client.sprite.SpriteStore;
import marauroa.common.game.RPObject;

class Gate2DView extends Entity2DView<IEntity> {
	private static final HashMap<String, Sprite[]> sprites = new HashMap<String, Sprite[]>();

	private Sprite openSprite, closedSprite;

	/**
	 * Get the zone color adjusted sprite
	 *
	 * @param base base sprite
	 * @return color adjusted sprite, or original sprite if no adjusting is
	 * 	needed
	 */
	private Sprite getModifiedSprite(Sprite base) {
		ZoneInfo info = ZoneInfo.get();
		if ((info.getColorMethod() == null) || (info.getZoneColor() == null)) {
			return base;
		}

		SpriteStore store = SpriteStore.get();
		String ref = store.createModifiedRef(base.getReference().toString(),
				info.getZoneColor(), info.getColorMethod());
		Sprite rval = SpriteCache.get().get(ref);
		if (rval == null) {
			rval = store.modifySprite(base, info.getZoneColor(),
					info.getColorMethod(), ref);
		}

		return rval;
	}


	@Override
	protected void buildActions(final List<String> list) {
		list.add(ActionType.USE.getRepresentation());
	}

	@Override
	protected void buildRepresentation(IEntity entity) {
		final RPObject rpobject = entity.getRPObject();
		final String baseImage = rpobject.get("image");
		final String orientation = rpobject.get("orientation");

		SpriteStore store = SpriteStore.get();

		String imageName = "data/sprites/doors/" + baseImage + "_" + orientation +".png";
		Sprite[] s = sprites.get(imageName);
		if (s == null) {
			Sprite sprite = store.getSprite(imageName);
			s = new Sprite[2];
			s[0] = sprite.createRegion(0, 0, 96, 96, imageName + "[0]");
			s[1] = sprite.createRegion(0, 96, 96, 96, imageName + "[1]");
			sprites.put(imageName, s);
		}

		openSprite = getModifiedSprite(s[0]);
		closedSprite = getModifiedSprite(s[1]);
	}

	@Override
	public void onAction() {
		onAction(ActionType.USE);
	}

	@Override
	public void onAction(final ActionType at) {
		if (isReleased()) {
			Logger.getLogger(Gate2DView.class).debug(
					"View already released - action not processed: " + at);
			return;
		}

		switch (at) {
		case USE:
			at.send(at.fillTargetInfo(entity));
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
		return 5000;
	}
}
