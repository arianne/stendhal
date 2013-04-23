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

import java.awt.Color;

import games.stendhal.client.GameScreen;
import games.stendhal.client.gui.j2d.BackgroundPainter;
import games.stendhal.client.gui.j2d.TextBoxFactory;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.common.NotificationType;

import javax.swing.SwingUtilities;

/**
 * A controller for isolating the out-of event dispatch thread calls to the game
 * screen.
 */
class ScreenController implements PositionChangeListener {
	/** The maximum width of text in text boxes, speech bubbles and similar. */
	private static final int BUBBLE_TEXT_WIDTH = 240;
	
	/** Image used for drawing tutorial box backgrounds. */
	private static final String TUTORIAL_BACKGROUND = "data/gui/tutorial_background.png";
	/** Depends on TUTORIAL_BACKGROUND. */
	private static final int TUTORIAL_LEFT_TILE_WIDTH = 48;
	/** Depends on TUTORIAL_BACKGROUND. */
	private static final int TUTORIAL_CENTER_TILE_WIDTH = 8;
	/** Depends on TUTORIAL_BACKGROUND. */
	private static final int TUTORIAL_TOP_TILE_HEIGHT = 32;
	/** Depends on TUTORIAL_BACKGROUND. */
	private static final int TUTORIAL_CENTER_TILE_HEIGHT = 8;
	
	private final GameScreen screen;
	// nextFrame() gets called all the time. Avoid needlessly creating new
	// objects for it.
	private final Runnable nextFrameRunner = new NextFrameRunner();
	private TextBoxFactory textBoxFactory;
	
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
		final Sprite sprite = createTextBox(text, type, isTalking);
		final int textLength = text.length();
		
		if (!isTalking) {
			final int priority = getPriority(type);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					screen.addStaticText(sprite, textLength, priority);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					screen.addTextBox(sprite, x, y, textLength);
				}
			});
		}
	}
	
	/**
	 * Get the importance of a message to keep it above others
	 * 
	 * @param type type of the message
	 * @return priority
	 */
	private int getPriority(NotificationType type) {
		// Tutorial above most messages, admin messages above everything
		// else
		switch (type) {
		case TUTORIAL:
			return 1;
		case SUPPORT:
			return 2;
		default:
			return 0;
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
			@Override
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
			@Override
			public void run() {
				screen.setOffline(offline);
			}
		});
	}

	@Override
	public void positionChanged(final double x, final double y) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				screen.positionChanged(x, y);
			}
		});
	}
	
	private final class NextFrameRunner implements Runnable {
		@Override
		public void run() {
			screen.nextFrame();
		}
	}
	

	/**
	 * Create a text box with the appropriate text color for a notification
	 * type.
	 * 
	 * @param text
	 * @param type
	 * @param isTalking if <code>true</code> create a text box with a bubble
	 * 	handle
	 * @return text sprite
	 */
	private Sprite createTextBox(final String text, final NotificationType type,
			final boolean isTalking) {
		// Special handling for pretty tutorial events
		if (type == NotificationType.TUTORIAL) {
			BackgroundPainter painter = new BackgroundPainter(TUTORIAL_BACKGROUND,
					TUTORIAL_LEFT_TILE_WIDTH, TUTORIAL_CENTER_TILE_WIDTH,
					TUTORIAL_TOP_TILE_HEIGHT, TUTORIAL_CENTER_TILE_HEIGHT);
			return getTextFactory().createFancyTextBox(text, type.getColor(),
					BUBBLE_TEXT_WIDTH, 45, 6, 6, 6, painter);
		}
		return getTextFactory().createTextBox(text, BUBBLE_TEXT_WIDTH, type.getColor(), Color.white, isTalking);
	}
	
	
	/**
	 * Lazy initialize the text box factory.
	 *  
	 * @return factory
	 */
	private TextBoxFactory getTextFactory() {
		if (textBoxFactory == null) {
			textBoxFactory = new TextBoxFactory();
		}
		
		return textBoxFactory;
	}
}
