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

import marauroa.common.game.*;
import games.stendhal.client.*;

import java.awt.*;
import java.awt.geom.*;

public class Text extends Entity {
	private final static long STANDARD_PERSISTENCE_TIME = 5000;

	private double tx;

	private double ty;

	private Sprite textImage;

	private long textImageTime;

	private long textPersistTime;

	private String text;

	public Text(GameObjects gameObjects, Sprite text, double x, double y,
			long persistTime) throws AttributeNotFoundException {
		
		this.client = StendhalClient.get();

		textImage = text;
		textImageTime = System.currentTimeMillis();

		if ((textPersistTime = persistTime) == 0)
			textPersistTime = STANDARD_PERSISTENCE_TIME;

		// Speech bubbles should be top right of speaker intensifly@gmx.com
		// this.tx=x+0.7-(textImage.getWidth()/((float)GameScreen.SIZE_UNIT_PIXELS*2.0f));
		this.tx = x + 1;

		// this.ty=y-0.5;
		this.ty = y;
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return text;
	}

	public Text(GameObjects gameObjects, String text, double x, double y,
			Color color, boolean isTalking) throws AttributeNotFoundException {
		
		this.client = StendhalClient.get();

		// Speech bubbles will only be drawn if there's a background color
		// intensifly@gmx.com
		textImage = GameScreen.get().createTextBox(text, 240, color,
				Color.white, isTalking);
		textImageTime = System.currentTimeMillis();
		textPersistTime = Math.max(STANDARD_PERSISTENCE_TIME, text.length()
				* STANDARD_PERSISTENCE_TIME / 50);

		if (isTalking) {
			// Speech bubbles should be top right of speaker intensifly@gmx.com
			this.tx = x + 1;
			this.ty = y;

		} else {
			this.tx = x
					+ 0.7
					- (textImage.getWidth() / ((float) GameScreen.SIZE_UNIT_PIXELS * 2.0f));
			this.ty = y + 1.5;
		}

		this.x = x;
		this.y = y;
		this.text = text;
	}

	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
	}

	public void onChangedRemoved(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
	}

	public String defaultAction() {
		return null;
	}

	public String[] offeredActions() {
		return null;
	}

	public void onAction(StendhalClient client, String action, String... params) {
	}

	public Rectangle2D getArea() {
		return null;
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(tx, ty, 
			(double)textImage.getWidth() / GameScreen.SIZE_UNIT_PIXELS,
			(double)textImage.getHeight() / GameScreen.SIZE_UNIT_PIXELS);
	}

	public void draw(GameScreen screen) {
		screen.draw(textImage, tx, ty);

		if (System.currentTimeMillis() - textImageTime > textPersistTime) {
			GameObjects.getInstance().removeText(this);
		}
	}


	@Override
	public int getZIndex() {
		return 9000;
	}

}
