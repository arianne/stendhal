/***************************************************************************
 *                (C) Copyright 2003-2015 - Faiumoni e.V.                  *
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
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.wt.core.WtWindowManager;

/**
 * Class for determining the used transparency mode. The behavior depends on the
 * setting ui.transparency. Mode "bitmask" means bitmask transparency, and
 * "translucent" means that full alpha transparency is used. Mode "auto", means
 * that the appropriate mode is decided based on a speed test.
 */
public final class TransparencyMode {
	/** The transparency mode that should be used for images. */
	public static final int TRANSPARENCY = getMode();

	/**
	 * Prevent instantiation.
	 */
	private TransparencyMode() {
	}

	/**
	 * Determine the mode.
	 *
	 * @return transparency mode
	 */
	private static int getMode() {
		String preference = WtWindowManager.getInstance().getProperty("ui.transparency", "auto");
		if ("bitmask".equals(preference)) {
			return Transparency.BITMASK;
		}
		if ("translucent".equals(preference)) {
			return Transparency.TRANSLUCENT;
		}
		// auto, and any broken value
		return autoMode();
	}

	/**
	 * Determine the appropriate transparency mode based on a speed test.
	 *
	 * @return transparency mode
	 */
	private static int autoMode() {
		long time = speedTest();
		if (time < 300000000) {
			return Transparency.TRANSLUCENT;
		}
		Logger.getLogger(TransparencyMode.class).info("The system is slow - disabling translucency.");
		return Transparency.BITMASK;
	}

	/**
	 * Run a rendering speed test.
	 *
	 * @return time used for running the test
	 */
	private static long speedTest() {
		BufferedImage buf = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = buf.createGraphics();
		g.setColor(new Color(255, 232, 12, 128));
		long start = System.nanoTime();
		for (int i = 0; i < 1000; i++) {
			g.fillRect(0, 0, 32, 32);
		}

		return System.nanoTime() - start;
	}
}
