/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import games.stendhal.client.GameScreen;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.NotificationType;

import javax.swing.SwingUtilities;

/**
 * A controller for isolating the out-of event dispatch thread calls to the game
 * screen.
 */
class ScreenController implements PositionChangeListener {
	final GameScreen screen;
	// nextFrame() gets called all the time. Avoid needlessly creating new
	// objects for it.
	final Runnable nextFrameRunner = new NextFrameRunner();
	
	/**
	 * Create a new ScreenController.
	 * 
	 * @param screen controlled screen
	 */
	ScreenController(GameScreen screen) {
		this.screen = screen;
	}
	/**
	 * Adds a text bubble at a give position of the specified type. For
	 * non-talking boxes the coordinates are ignored, and the box is attached
	 * to the bottom of the screen.
	 * 
	 * @param x The screen X coordinate.
	 * @param y The screen Y coordinate.
	 * @param text The textual content
	 * @param type The notificationType 
	 * @param isTalking Is it a talking text bubble
	 * @see games.stendhal.common.NotificationType 
	 */
	void addText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		// createTextBox is thread safe, the rest is not
		final Sprite sprite = screen.createTextBox(text, type, isTalking);
		final int textLength = text.length();
		
		if (!isTalking) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					screen.addStaticText(sprite, textLength);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					screen.addTextBox(sprite, x, y, textLength);
				}
			});
		}
	}
	
	/**
	 * Tell the screen to prepare for rendering the next frame. 
	 */
	void nextFrame() {
		SwingUtilities.invokeLater(nextFrameRunner);
	}
	
	/**
	 * Set the map size.
	 * 
	 * @param width
	 * @param height
	 */
	void setWorldSize(final double width, final double height) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				screen.setMaxWorldSize(width, height);
			}
		});
	}
	
	/**
	 * Set the offline status of the client.
	 * 
	 * @param offline
	 */
	void setOffline(final boolean offline) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				screen.setOffline(offline);
			}
		});
	}

	public void positionChanged(final double x, final double y) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				screen.positionChanged(x, y);
			}
		});
	}
	
	private final class NextFrameRunner implements Runnable {
		public void run() {
			screen.nextFrame();
		}
	}
}
