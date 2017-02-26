/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package data.sprites.monsters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class AnimationRunner implements ActionListener {
	private int direction = 1;
	private final Timer timer;
	private BufferedImage[] frames;
	private final JLabel viewer;
	private int currentframe;

	public AnimationRunner(JLabel viewer) {
		this.viewer = viewer;
		timer = new Timer(100, this);
	}

	public synchronized void startAnimation(final BufferedImage[] frames) {
		this.frames = frames;
		timer.start();
	}

	public synchronized void stopAnimation() {
		timer.stop();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		viewer.setIcon(new ImageIcon(frames[currentframe]));

		if (currentframe == frames.length - 1) {
			direction = -1;
		}
		if (currentframe == 0) {
			direction = 1;
		}
		currentframe += direction;
	}
}
