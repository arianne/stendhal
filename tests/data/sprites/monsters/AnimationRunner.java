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
package data.sprites.monsters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

public class AnimationRunner implements ActionListener {
	
	private int direction = 1;
	
	private Timer timer;

	private BufferedImage[] frames;

	private final ImageViewerSwing ivs;

	private int currentframe;

	private int number_of_frames;

	public AnimationRunner(final ImageViewerSwing ivs) {

		this.ivs = ivs;

		timer = new Timer(200, this);
	}

	// Set up the components in the GUI.

	public synchronized void startAnimation(final BufferedImage[] frames) {
		this.frames = frames;
		number_of_frames = frames.length;
		timer.start();

	}

	public synchronized void stopAnimation() {

		timer.stop();

	}

	

	@Override
	public void actionPerformed(final ActionEvent e) {
		ivs.setImage(frames[currentframe]);

		if (currentframe == number_of_frames - 1) {
			direction = -1;
		}
		if (currentframe == 0) {
			direction = 1;
		}
		currentframe += direction;

	}

	public void tearDown() {
		timer.removeActionListener(this);
		timer = null;
	}
}
