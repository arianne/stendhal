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

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class Sign extends Entity {

	private static final long STANDARD_PERSISTENCE_TIME = 5000;

	private String text;

	private Sprite textImage;

	private long textPersistTime;

	// Give Signs same color on Screen and Log window. intensifly@gmx.com
	private static final Color signColor = new Color(0x006400); // dark green



	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);
		GameScreen screen = GameScreen.get();

		if (diff.has("text")) {
			text = diff.get("text");

			Graphics g2d = screen.expose();

			String[] lines = text.split("\n");

			int lineLengthPixels = 0;
			for (String line : lines) {
				int val = g2d.getFontMetrics().stringWidth(line);
				if (val > lineLengthPixels) {
					lineLengthPixels = val;
				}
			}

			GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
			        .getDefaultConfiguration();
			int width = lineLengthPixels + 4;
			int height = 16 * lines.length;

			Image image = gc.createCompatibleImage(width, height, Transparency.BITMASK);

			Graphics g = image.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			g.setColor(signColor);
			g.drawRect(0, 0, width - 1, height - 1);

			int j = 0;
			for (String line : lines) {
				g.setColor(signColor);
				// Give 1 more pixel distance to top sign border.
				// intensifly@gmx.com
				g.drawString(line, 2, 12 + j * 16);
				j++;
			}

			textImage = new Sprite(image);

			textPersistTime = Math.max(STANDARD_PERSISTENCE_TIME, text.length() * STANDARD_PERSISTENCE_TIME / 50);
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	protected void loadSprite(final RPObject object) {
		SpriteStore store = SpriteStore.get();
		String name = null;

		if (object.has("class")) {
			name = object.get("class");
		} else {
			name = "default";
		}

		sprite = store.getSprite("data/sprites/signs/" + name + ".png");
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.READ;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		// we don't want "Look", we use "Read" instead.
		// super.buildOfferedActions(list);
		list.add(ActionType.READ.getRepresentation());
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// =handleAction(action);
		switch (at) {
			case READ:
				GameObjects.getInstance().addText(this, textImage, textPersistTime);
				if (text.contains("\n")) {
					// The sign's text has multiple lines. Add a linebreak after
					// "you read" so that it is easier readable.
					StendhalUI.get().addEventLine("You read:\n\"" + text + "\"", signColor);
				} else {
					StendhalUI.get().addEventLine("You read: \"" + text + "\"", signColor);
				}
				break;

			default:
				super.onAction(at, params);
				break;
		}

	}

	@Override
	public int getZIndex() {
		return 5000;
	}
}
