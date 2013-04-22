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
package games.stendhal.client.gui.stats;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A component that draws an animated sprite.
 */
public class AnimatedIcon extends JComponent {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 2855060585196386211L;

	private static final int TILE_SIZE = 32;

	final Sprite[] sprite;
	final int height;
	final Timer timer;
	final Dimension size;
	int current = 0;

	/*
	 * AnimatedSprite is designed to be used in a drawing loop,
	 * where it updates itself at the wanted intervals. That's 
	 * rather wasteful for an image outside the game area, so handle
	 * our own timing (and stop the timer if the icon is not visible).
	 */

	/**
	 * Timer task to update and draw the icon.
	 */
	final ActionListener timerTask = new ActionListener() {
		@Override
		public void actionPerformed(final ActionEvent e) {
			current++;
			if (current >= sprite.length) {
				current = 0;
			}
			AnimatedIcon.this.repaint();
		}
	};

	/**
	 * Create an <code>AnimatedIcon</code> from a Sprite.
	 * 
	 * @param baseSprite animation frames image
	 * @param yOffset empty space from the top of the image
	 * @param height height of the image
	 * @param frames number of animation frames
	 * @param delay delay between the frames
	 */
	public AnimatedIcon(Sprite baseSprite, int yOffset, int height, int frames, int delay) {
		setOpaque(false);

		this.sprite = new Sprite[frames];
		this.height = height;
		timer = new Timer(delay, timerTask);

		size = new Dimension(TILE_SIZE, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		final SpriteStore store = SpriteStore.get();

		for (int i = 0; i < frames; i++) {
			sprite[i] = store.getTile(baseSprite, i * TILE_SIZE, yOffset, TILE_SIZE, height);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		sprite[current].draw(g, 0, 0);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			timer.start();
		} else {
			timer.stop();
		}
	}
}
