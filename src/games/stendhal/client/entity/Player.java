/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.WorldObjects;
import games.stendhal.server.StendhalQuestSystem;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** A Player entity */
public class Player extends RPEntity {
	public static final double DEFAULT_HEARINGRANGE = 20;

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Player.class);

	private int outfit;

	private int outfitOrg;

	private double hearingRange;

	public Player(RPObject base) throws AttributeNotFoundException {
		super(base);
		hearingRange = DEFAULT_HEARINGRANGE;
		// setHearingRange(DEFAULT_HEARINGRANGE);
	}

	@Override
	protected void buildAnimations(RPObject base) {
		SpriteStore store = SpriteStore.get();

		Sprite sprite;

		try {
			if (base.has("outfit_org")) {
				outfitOrg = base.getInt("outfit_org");
			} else {
				outfitOrg = 0;
			}
			if ((outfit == base.getInt("outfit")) && (outfit != 0)) {
				// We avoid creating again the outfit if it is already done.
				// Save CPU cycles.
				return;
			}
			outfit = base.getInt("outfit");
			sprite = getOutfitSprite(store, base);
		} catch (Exception e) {
			logger.error("cannot build Animations", e);
			// use default outfit
			base.put("outfit", 0);
			sprite = getOutfitSprite(store, base);
		}

		sprites.put("move_up", store.getAnimatedSprite(sprite, 0, 4, 1.5, 2));
		sprites
				.put("move_right", store
						.getAnimatedSprite(sprite, 1, 4, 1.5, 2));
		sprites.put("move_down", store.getAnimatedSprite(sprite, 2, 4, 1.5, 2));
		sprites.put("move_left", store.getAnimatedSprite(sprite, 3, 4, 1.5, 2));

		sprites.get("move_up")[3] = sprites.get("move_up")[1];
		sprites.get("move_right")[3] = sprites.get("move_right")[1];
		sprites.get("move_down")[3] = sprites.get("move_down")[1];
		sprites.get("move_left")[3] = sprites.get("move_left")[1];
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("outfit")) {
			buildAnimations(diff);
		}

		// We redo here to mantain player whose name has an underscore
		if (diff.has("name")) {
			setName(diff.get("name"));
		}

		// The first time we ignore it.
		if (base != null) {
			if (diff.has("online")) {
				String[] players = diff.get("online").split(",");
				for (String name : players) {
					StendhalClient.get().addEventLine(name + " has joined Stendhal.",
							Color.orange);
				}
			}

			if (diff.has("offline")) {
				String[] players = diff.get("offline").split(",");
				for (String name : players) {
					StendhalClient.get().addEventLine(name + " has left Stendhal.",
							Color.orange);
				}
			}
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 2);
	}

	/**
	 * the absolute world area (coordinates) where the player can possibly hear
	 * sounds
	 */
	public Rectangle2D getHearingArea() {
		double width = hearingRange * 2;
		return new Rectangle2D.Double(getX() - hearingRange, getY()
				- hearingRange, width, width);
	}

	/**
	 * Sets the hearing range as radius distance from a player's position,
	 * expressed in coordinate units. This reflects an abstract hearing capacity
	 * of this unit and influences the result of <code>getHearingArea()</code>.
	 * 
	 * @param range
	 *            double approx. hearing area radius in coordinate units
	 */
	// public void setHearingRange(double range) {
	// hearingRange = range;
	// }

	@Override
	public void onAction(ActionType at, String... params) {
	   
		// ActionType at =handleAction(action);
		RPAction rpaction;
		switch (at) {
		case SET_OUTFIT:
			int outfitTemp = outfit;
			if (outfitOrg > 0) {
				outfitTemp = outfitOrg;
			}
			StendhalClient.get().getOutfitDialog(outfitTemp).setVisible(true);
			break;
		case LEAVE_SHEEP:
			rpaction = new RPAction();
			rpaction.put("type", at.toString());
			rpaction.put("target", "-1");
			at.send(rpaction);
			playSound("sheep-chat-2", 15, 50);
			break;
		case ADD_BUDDY:
			rpaction = new RPAction();
			rpaction.put("type", at.toString());
			rpaction.put("target", getName());
			at.send(rpaction);
			break;
		default:
			super.onAction(at, params);
			break;
		}

	}

	@Override
	public void onEnter(int x, int y) {
		if ((StendhalClient.get().getPlayer() != null)
				&& StendhalClient.get().getPlayer().getID().equals(getID())) {
			WorldObjects.firePlayerMoved(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see games.stendhal.client.entity.RPEntity#buildOfferedActions(java.util.List)
	 */
	@Override
	protected void buildOfferedActions(List<String> list) {
		// TODO Auto-generated method stub
		super.buildOfferedActions(list);

		if (getID().equals(StendhalClient.get().getPlayer().getID())) {
			list.add(ActionType.SET_OUTFIT.getRepresentation());
			if (list.contains(ActionType.ATTACK.getRepresentation()));
				list.remove(ActionType.ATTACK.getRepresentation());
			if (StendhalClient.get().getPlayer().has("sheep")) {
				list.add(ActionType.LEAVE_SHEEP.getRepresentation());
			}
		} else {
			list.add(ActionType.ADD_BUDDY.getRepresentation());
		}

	}

}