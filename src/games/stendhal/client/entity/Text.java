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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class Text {
	private static final long STANDARD_PERSISTENCE_TIME = 5000;

	private double x;

	private double y;

	private double tx;

	private double ty;

	private Sprite textImage;

	private long textImageTime;

	private long textPersistTime;

	private String text;

	public Text(final GameObjects gameObjects, final Sprite textSprite, final double x, final double y, final long persistTime) {
		textImage = textSprite;
		textImageTime = System.currentTimeMillis();
			 
		if (persistTime == 0) {
			textPersistTime = STANDARD_PERSISTENCE_TIME;
		} else {
			textPersistTime= persistTime;
		}

		// Speech bubbles should be top right of speaker intensifly@gmx.com
		// this.tx=x+0.7-(textImage.getWidth()/((float)GameScreen.SIZE_UNIT_PIXELS*2.0f));
		this.tx = x + 1;

		// this.ty=y-0.5;
		this.ty = y;
		this.x = x;
		this.y = y;
	}

	public Text(final GameObjects gameObjects, final String text, final double x, final double y, final Color color, final boolean isTalking) {

		// Speech bubbles will only be drawn if there's a background color
		// intensifly@gmx.com
		textImage = GameScreen.get().createTextBox(text, 240, color, Color.white, isTalking);
		textImageTime = System.currentTimeMillis();
		textPersistTime = Math.max(STANDARD_PERSISTENCE_TIME, text.length() * STANDARD_PERSISTENCE_TIME / 50);

		if (isTalking) {
			// Speech bubbles should be top right of speaker intensifly@gmx.com
			this.tx = x + 1;
			this.ty = y;

		} else {
			this.tx = x + 0.7 - (textImage.getWidth() / (GameScreen.SIZE_UNIT_PIXELS * 2.0f));
			this.ty = y + 1.5;
		}

		this.x = x;
		this.y = y;
		this.text = text;
	}

	public void draw(final GameScreen screen) {
		screen.draw(textImage, tx, ty);

		if (System.currentTimeMillis() - textImageTime > textPersistTime) {
			GameObjects.getInstance().removeText(this);
		}
	}


	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(tx, ty, (double) textImage.getWidth() / GameScreen.SIZE_UNIT_PIXELS,
		        (double) textImage.getHeight() / GameScreen.SIZE_UNIT_PIXELS);
	}

	public double getX() {
		return x;
	}


	public double getY() {
		return y;
	}


	//
	// Object
	//

	@Override
	public String toString() {
		return text;
	}
}
