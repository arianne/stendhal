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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.Timer;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * A component that draws an animated sprite.
 */
class AnimatedIcon extends JComponent {
	private final Sprite[] sprite;
	private final Timer timer;
	private int current = 0;

	/*
	 * AnimatedSprite is designed to be used in a drawing loop,
	 * where it updates itself at the wanted intervals. That's
	 * rather wasteful for an image outside the game area, so handle
	 * our own timing (and stop the timer if the icon is not visible).
	 */

	/**
	 * Timer task to update and draw the icon.
	 */
	private final ActionListener timerTask = new ActionListener() {
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
	 * Create an <code>AnimatedIcon</code> from a Sprite. Each animation frame
	 * will be a rectangle of the same height that the original sprite.
	 *
	 * @param baseSprite animation frames image
	 * @param delay delay between the frames
	 */
	AnimatedIcon(Sprite baseSprite, int delay) {
		setOpaque(false);

		int height = baseSprite.getHeight();
		int frames = baseSprite.getWidth() / height;

		this.sprite = new Sprite[frames];
		timer = new Timer(delay, timerTask);

		setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

		final SpriteStore store = SpriteStore.get();

		for (int i = 0; i < frames; i++) {
			sprite[i] = store.getTile(baseSprite, i * height, 0, height, height);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Insets insets = getInsets();

		return new Dimension(sprite[0].getWidth() + insets.left + insets.right,
				sprite[0].getHeight() + insets.top + insets.bottom);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		Insets insets = getInsets();
		sprite[current].draw(g, insets.left, insets.top);
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
