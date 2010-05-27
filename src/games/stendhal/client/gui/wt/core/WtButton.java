/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Button.java
 *
 * Created on 23. Oktober 2005, 09:47
 */

package games.stendhal.client.gui.wt.core;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.sprite.Sprite;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 * A simple button. It features a onClick() callback. A button can have an image
 * <b>or</b> some text, but not both.
 * 
 * @author mtotz
 */
public class WtButton extends WtPanel {

	/** image for the button. */
	private Sprite image;

	/**
	 * Creates a new Button with text.
	 * 
	 * @param name
	 * @param width
	 * @param height
	 * @param text
	 * @param gameScreen
	 */
	public WtButton(final String name, final int width, final int height, final String text,
			final IGameScreen gameScreen) {
		super(name, 0, 0, width, height, gameScreen);
		initialize();
		final int clientHeight = (getClientHeight() - WtTextPanel.DEFAULT_FONT_SIZE) / 2;
		final WtTextPanel textPanel = new WtTextPanel(name + "text", 2, clientHeight,
				width, height, text, gameScreen);
		addChild(textPanel);
		setTitletext(text);
	}

	/**
	 * Creates a new Button with an image.
	 * 
	 * @param name
	 * @param width
	 * @param height
	 * @param image
	 * @param gameScreen
	 */
	public WtButton(final String name, final int width, final int height, final Sprite image,
			final IGameScreen gameScreen) {
		super(name, 0, 0, width, height, gameScreen);
		initialize();
		this.image = image;
	}

	private void initialize() {
		setMinimizeable(false);
		setTitleBar(false);
		setFrame(true);

	}

	/**
	 * Draw the button contents.
	 * <p>
	 * This is only called while open and not minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(final Graphics2D g, final IGameScreen gameScreen) {
		super.drawContent(g, gameScreen);

		// draw the image if we have one
		if (image != null) {
			image.draw(g, 0, 0);
		}
	}

	/** button has been clicked. */
	@Override
	public synchronized boolean onMouseClick(final Point point) {
		setEmboss(!isEmbossed());
		// tell all registered listeners that we're clicked
		notifyClickListeners(getName(), point);
		return true;
	}

	/** @return true when the button in pressed and false when it is released. */
	public boolean isPressed() {
		return isEmbossed();
	}

	/**
	 * sets the state of the button.
	 * 
	 * @param pressed
	 */
	public void setPressed(final boolean pressed) {
		setEmboss(pressed);
	}
}
