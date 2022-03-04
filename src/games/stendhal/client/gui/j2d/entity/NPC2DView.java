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


import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.client.OutfitStore;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.OutfitColor;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import marauroa.common.game.RPAction;

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
	/**
	 * The idea property changed.
	 */
	private volatile boolean ideaChanged = true;
	/**
	 * The current idea sprite.
	 */
	private Sprite ideaSprite;

	private List<String> animatedSprites = Arrays.asList("love");

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

		Sprite sprite;

		try {
			final RPEntity npc = entity;
			final int code = npc.getOldOutfitCode();
			final String strcode = npc.getExtOutfit();

			final OutfitColor color = OutfitColor.get(npc.getRPObject());

			if (strcode != null) {
				sprite = OutfitStore.get().getAdjustedOutfit(strcode, color, info.getZoneColor(), info.getColorMethod());
			} else if (code != RPEntity.OUTFIT_UNSET) {
				final int body = code % 100;
				final int dress = code / 100 % 100;
				final int head = (int) (code / Math.pow(100, 2) % 100);
				final int hair = (int) (code / Math.pow(100, 3) % 100);
				final int detail = (int) (code / Math.pow(100, 4) % 100);

				final StringBuilder sb = new StringBuilder();
				sb.append("body=" + body);
				sb.append(",dress=" + dress);
				sb.append(",head=" + head);
				sb.append(",hair=" + hair);
				sb.append(",detail=" + detail);

				sprite = OutfitStore.get().getAdjustedOutfit(sb.toString(), color, info.getZoneColor(),
						info.getColorMethod());
			} else {
				// This NPC's outfit is read from a single file.
				sprite = store.getModifiedSprite(translate("npc/"
						+ entity.getEntityClass()), info.getZoneColor(),
						info.getColorMethod());
			}
		} catch (final Exception e) {
			logger.error("Cannot build animations", e);
			sprite = store.getModifiedSprite(translate(entity.getEntityClass()),
					info.getZoneColor(), info.getColorMethod());
		}

		return addShadow(sprite);
	}

	@Override
	void entityChanged(final Object property) {
		super.entityChanged(property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		} else if (property == NPC.PROP_IDEA) {
			ideaChanged = true;
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

	/**
	 * Get the appropriate idea sprite.
	 *
	 * @return The sprite representing the current idea, or null.
	 */
	private Sprite getIdeaSprite() {
		final String idea = entity.getIdea();

		if (idea == null) {
			return null;
		}

		final SpriteStore ss = SpriteStore.get();
		Sprite ideaSprite = ss.getSprite("data/sprites/ideas/" + idea + ".png");
		if (animatedSprites.contains(idea)) {
			ideaSprite = ss.getAnimatedSprite(ideaSprite, 100);
		}

		return ideaSprite;
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
	 * Gets the mouse cursor image to use for this entity.
	 *
	 * @return StendhalCursor
	 */
	@Override
	public StendhalCursor getCursor() {
		return StendhalCursor.LOOK;
	}
}
