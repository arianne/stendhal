/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.stendhalgame.client;

import android.content.res.Configuration;
import android.widget.ImageView;


public class SplashUtil {

	/** Image used as title page background. */
	private final ImageView splash;

	/** Singleton instance. */
	private static SplashUtil instance;


	/**
	 * Retrieves singleton instance.
	 */
	public static SplashUtil get() {
		if (SplashUtil.instance == null) {
			SplashUtil.instance = new SplashUtil();
		}
		return SplashUtil.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private SplashUtil() {
		splash = (ImageView) MainActivity.get().findViewById(R.id.splash);
		setVisible(false);
	}

	/**
	 * Sets the background splash image.
	 *
	 * @param resId
	 *   Resource ID.
	 * @param bgColor
	 *   Coloring to fill behind splash image.
	 */
	private void setImage(final int resId, final int bgColor) {
		splash.setBackgroundColor(bgColor);
		splash.setImageResource(resId);
	}

	/**
	 * Sets splash image dependent on device orientation.
	 */
	public void update() {
		if (!isVisible()) {
			return;
		}
		int resId = R.drawable.splash;
		if (MainActivity.get().getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			resId = R.drawable.splash_portrait;
		}
		// light blue background color
		setImage(resId, 0xff6c9ed1);
	}

	/**
	 * Shows or hides background splash image.
	 *
	 * @param visible
	 *   `true` if image should visible, `false` if not.
	 */
	public void setVisible(final boolean visible) {
		splash.setVisibility(visible ? ImageView.VISIBLE : ImageView.GONE);
		update();
	}

	/**
	 * Checks if splash is visible.
	 */
	public boolean isVisible() {
		return ImageView.VISIBLE == splash.getVisibility();
	}
}
