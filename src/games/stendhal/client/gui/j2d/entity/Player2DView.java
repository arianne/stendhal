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


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.client.OutfitStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.ZoneInfo;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.StatusID;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.OutfitColor;
import games.stendhal.client.gui.j2d.Blend;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * The 2D view of a player.
 *
 * @param <T> player type
 */
class Player2DView<T extends Player> extends RPEntity2DView<T> {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Player2DView.class);
	/** Color used for players who have been zombified. */
	private static final Color ZOMBIE_COLOR = new Color(0x083000);

	/**
	 * Sprite representing away.
	 */
	private static final Sprite awaySprite;

	/**
	 * Sprite representing grumpy.
	 */
	private static final Sprite grumpySprite;
	/**
	 * Sprite representing recently killing of other player.
	 */
	private static final Sprite skullSprite;

	static {
		final SpriteStore store = SpriteStore.get();
		final Sprite gotAwaySprite = store.getSprite("data/sprites/ideas/away.png");
		final Sprite gotGrumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
		final Sprite gotPkSprite = store.getSprite("data/sprites/ideas/pk.png");
		skullSprite = store.getAnimatedSprite(gotPkSprite, 16, 200);
		awaySprite = store.getAnimatedSprite(gotAwaySprite, 2000);
		grumpySprite = store.getAnimatedSprite(gotGrumpySprite, 2000);
	}

	private boolean ignored = false;

	/**
	 * Create a new Player2DView.
	 */
	public Player2DView() {
		addIconManager(new AbstractStatusIconManager(Player.PROP_AWAY, awaySprite,
				HorizontalAlignment.RIGHT, VerticalAlignment.TOP) {
					@Override
					boolean show(T player) {
						return player.isAway();
					}} );
		addIconManager(new AbstractStatusIconManager(Player.PROP_GRUMPY, grumpySprite,
				HorizontalAlignment.LEFT, VerticalAlignment.TOP) {
					@Override
					boolean show(T player) {
						return player.isGrumpy();
					}} );
		addIconManager(new AbstractStatusIconManager(Player.PROP_PLAYER_KILLER, skullSprite,
				HorizontalAlignment.LEFT, VerticalAlignment.TOP) {
					@Override
					boolean show(T player) {
						return player.isBadBoy();
					}} );
	}

	//
	// RPEntity2DView
	//

	/**
	 * Draw the entity status bar.
	 *
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	@Override
	protected void drawStatusBar(final Graphics2D g2d, final int x,
			final int y, final int width) {
		/*
		 * Shift bar slightly to avoid overlap with smaller entities
		 */
		drawTitle(g2d, x, y + 6, width);
		Composite comp = g2d.getComposite();
		// Draw in full color for ignored players. Avoid making ghosts visible
		if (ignored && !entity.isGhostMode()) {
			g2d.setComposite(AlphaComposite.SrcAtop);
			drawHPbar(g2d, x, y + 6, width);
			g2d.setComposite(comp);
		} else {
			drawHPbar(g2d, x, y + 6, width);
		}
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		final OutfitStore store = OutfitStore.get();
		Sprite outfit;

		try {
			OutfitColor color = OutfitColor.get(entity.getRPObject());
			ZoneInfo info = ZoneInfo.get();

			final String strcode = entity.getExtOutfit();
			final int code = entity.getOldOutfitCode();

			if (strcode == null) {
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

				outfit = store.getAdjustedOutfit(sb.toString(), color, info.getZoneColor(), info.getColorMethod());
			} else {
				outfit = store.getAdjustedOutfit(strcode, color, info.getZoneColor(), info.getColorMethod());
			}

			if (entity.hasStatus(StatusID.ZOMBIE)) {
				outfit = SpriteStore.get().modifySprite(outfit, ZOMBIE_COLOR, Blend.TrueColor, null);
			}
		} catch (final RuntimeException e) {
			logger.warn("Cannot build outfit. Setting failsafe outfit.", e);
			outfit = store.getFailsafeOutfit();
		}

		return addShadow(outfit);
	}

	@Override
	protected AlphaComposite getComposite() {
		// Check for ghostmode to avoid ignored ghostmode admins becoming visible
		if (User.isIgnoring(entity.getName()) && !entity.isGhostMode()) {
			return AlphaComposite.DstOut;
		}
		return super.getComposite();
	}

	/**
	 * Determine is the user can see this entity while in ghostmode.
	 *
	 * @return <code>true</code> if the client user can see this entity while in
	 *         ghostmode.
	 */
	@Override
	protected boolean isVisibleGhost() {
		/*
		 * Admins see all
		 */
		return User.isAdmin();
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
		if (!entity.isGhostMode()) {
			super.buildActions(list);

			boolean hasBuddy = User.hasBuddy(entity.getName());
			if (!hasBuddy) {
				list.add(ActionType.ADD_BUDDY.getRepresentation());
			}

			if (User.isIgnoring(entity.getName())) {
				list.add(ActionType.UNIGNORE.getRepresentation());
			} else if (!hasBuddy)  {
				list.add(ActionType.IGNORE.getRepresentation());
			}
			if (StendhalClient.serverVersionAtLeast("0.88")) {
				list.add(ActionType.TRADE.getRepresentation());
			}
			if (StendhalClient.serverVersionAtLeast("0.92")) {
				list.add(ActionType.INVITE.getRepresentation());
			}
			if (StendhalClient.serverVersionAtLeast("1.24")) {
				if(System.getProperty("stendhal.pvpchallenge") != null) {
					list.add(ActionType.CHALLENGE.getRepresentation());
					list.add(ActionType.ACCEPT_CHALLENGE.getRepresentation());
				}
			}
		}
	}

	/**
	 * Draw the entity.
	 *
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
		boolean newIgnoreStatus = User.isIgnoring(entity.getName());
		if (newIgnoreStatus != ignored) {
			visibilityChanged = true;
			ignored = newIgnoreStatus;
			markChanged();
		}

		super.draw(g2d, x, y, width, height);
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
		if (at == null) {
			super.onAction(null);
			return;
		}
		switch (at) {
		case ADD_BUDDY:
		case CHALLENGE:
		case ACCEPT_CHALLENGE:
		case IGNORE:
		case INVITE:
		case UNIGNORE:
		case TRADE:
			at.send(at.fillTargetInfo(entity));
			break;
		default:
			super.onAction(at);
			break;
		}
	}

	@Override
	public boolean isInteractive() {
		if (entity.isGhostMode() && !isVisibleGhost()) {
			return false;
		}
		return super.isInteractive();
	}

	@Override
	public StendhalCursor getCursor() {
		if (isInteractive()) {
			return StendhalCursor.LOOK;
		} else {
			return null;
		}
	}
}
