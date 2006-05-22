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

import games.stendhal.client.*;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

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

	private int age;

	private double hearingRange;

	public Player(GameObjects gameObjects, RPObject base)
			throws AttributeNotFoundException {
		super(gameObjects, base);
		setHearingRange(DEFAULT_HEARINGRANGE);
	}

	protected void buildAnimations(RPObject base) {
		SpriteStore store = SpriteStore.get();

		Sprite player;

		try {
			if (outfit == base.getInt("outfit") && outfit != 0) {
				// We avoid creating again the outfit if it is already done.
				// Save CPU cycles.
				return;
			}

			outfit = base.getInt("outfit");
			player = setOutFitPlayer(store, base);
		} catch (Exception e) {
			logger.error("cannot build Animations", e);
			base.put("outfit", 0);
			player = setOutFitPlayer(store, base);
		}

		sprites.put("move_up", store.getAnimatedSprite(player, 0, 4, 1.5, 2));
		sprites
				.put("move_right", store
						.getAnimatedSprite(player, 1, 4, 1.5, 2));
		sprites.put("move_down", store.getAnimatedSprite(player, 2, 4, 1.5, 2));
		sprites.put("move_left", store.getAnimatedSprite(player, 3, 4, 1.5, 2));

		sprites.get("move_up")[3] = sprites.get("move_up")[1];
		sprites.get("move_right")[3] = sprites.get("move_right")[1];
		sprites.get("move_down")[3] = sprites.get("move_down")[1];
		sprites.get("move_left")[3] = sprites.get("move_left")[1];
	}

	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("outfit")) {
			buildAnimations(diff);
		}

		if (diff.has("age")) {
			age = diff.getInt("age");
		}

		// The first time we ignore it.
		if (base != null) {
			if (diff.has("online")) {
				String[] players = diff.get("online").split(",");
				for (String name : players) {
					client.addEventLine(name + " has joined Stendhal.",
							Color.orange);
				}
			}

			if (diff.has("offline")) {
				String[] players = diff.get("offline").split(",");
				for (String name : players) {
					client.addEventLine(name + " has left Stendhal.",
							Color.orange);
				}
			}
		}
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 2);
	}

	/**
	 * the absolute world area (coordinates) where the player can possibly hear
	 * sounds
	 */
	public Rectangle2D getHearingArea() {
		double width = hearingRange * 2;
		return new Rectangle2D.Double(getx() - hearingRange, gety()
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
	public void setHearingRange(double range) {
		hearingRange = range;
	}

	public String[] offeredActions() {
		java.util.Vector<String> vector = new java.util.Vector<String>();
		for (String item : super.offeredActions()) {
			vector.add(item);
		}

		if (getID().equals(client.getPlayer().getID())) {
			vector.add("Set outfit");

			if (client.getPlayer().has("sheep")) {
				vector.add("Leave sheep");
			}
		} else {
			vector.add("Add to Buddies");
		}

		return vector.toArray(new String[0]);
	}

	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Set outfit")) {
			client.getOutfitDialog().setVisible(true);
		} else if (action.equals("Leave sheep")) {
			RPAction rpaction = new RPAction();
			rpaction.put("type", "own");
			rpaction.put("target", "-1");
			client.send(rpaction);
			playSound("sheep-chat-2", 15, 50);
		} else if (action.equals("Add to Buddies")) {
			RPAction rpaction = new RPAction();
			rpaction.put("type", "addbuddy");
			rpaction.put("target", getName());
			client.send(rpaction);
		} else {
			super.onAction(client, action, params);
		}
	}
}
