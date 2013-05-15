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


import games.stendhal.client.OutfitStore;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.OutfitColor;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.util.List;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * The 2D view of an NPC.
 * 
 * @param <T> type of NPC
 */
class NPC2DView<T extends NPC> extends RPEntity2DView<T> {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(NPC2DView.class);
	

	//
	// RPEntity2DView
	//

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		final SpriteStore store = SpriteStore.get();
		ZoneInfo info = ZoneInfo.get();

		try {
			final int code = ((RPEntity) entity).getOutfit();

			if (code != RPEntity.OUTFIT_UNSET) {
				return OutfitStore.get().getAdjustedOutfit(code, OutfitColor.PLAIN,
						info.getZoneColor(), info.getColorMethod());
			} else {
				// This NPC's outfit is read from a single file.
				return store.getModifiedSprite(translate("npc/"
						+ entity.getEntityClass()), info.getZoneColor(),
						info.getColorMethod());
			}
		} catch (final Exception e) {
			logger.error("Cannot build animations", e);
			return store.getModifiedSprite(translate(entity.getEntityClass()),
					info.getZoneColor(), info.getColorMethod());
		}
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		}
	}

	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);
		// NPC can't be pushed
		list.remove(ActionType.PUSH.getRepresentation());
		if (User.isAdmin()) {
			list.add(ActionType.ADMIN_VIEW_NPC_TRANSITIONS.getRepresentation());
		}
	}

	/**
	 * Draw the entity.
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	protected void drawTop(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
		super.drawTop(g2d, x, y, width, height);
        
		if (entity.getIdea() != null) {
			Sprite sprite = SpriteStore.get().getSprite("data/sprites/ideas/" + entity.getIdea() + ".png");
			sprite.draw(g2d, x + (width * 3 / 4), y - 10);
		}

		
	}
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case ADMIN_VIEW_NPC_TRANSITIONS:
			final RPAction action = new RPAction();
			action.put("type", "script");
			action.put("target", "DumpTransitionsEx.class");
			action.put("args", this.getEntity().getTitle());
			at.send(action);
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
