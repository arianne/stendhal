/***************************************************************************
 *                     Copyright © 2022 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.arianne.stendhal.client;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;


public class ClientView extends WebView {

	private ImageView splash;


	public ClientView(final Context ctx) {
		super(ctx);
		onInit();
	}

	public ClientView(final Context ctx, final AttributeSet attrs) {
		super(ctx, attrs);
		onInit();
	}

	public ClientView(final Context ctx, final AttributeSet attrs, final int style) {
		super(ctx, attrs, style);
		onInit();
	}

	public ClientView(final Context ctx, final AttributeSet attrs, final int style,
			final boolean prvtBrowse) {
		super(ctx, attrs, style, prvtBrowse);
		onInit();
	}

	private void onInit() {
		setBackgroundColor(android.graphics.Color.TRANSPARENT);

		final WebSettings viewSettings = getSettings();
		viewSettings.setJavaScriptEnabled(true);

		// keep elements in position in portrait mode
		viewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// <-- SINGLE_COLUMN deprecated
		viewSettings.setLoadWithOverviewMode(true);
		viewSettings.setUseWideViewPort(true);

		// disable zoom
		viewSettings.setSupportZoom(false);
		viewSettings.setBuiltInZoomControls(false);
		viewSettings.setDisplayZoomControls(false);
	}

	/**
	 * Shows initial title screen.
	 */
	public void loadTitleScreen() {
		setSplashResource(R.drawable.splash);
		loadUrl("about:blank");
		Menu.get().show();
	}

	/**
	 * Sets the background image.
	 *
	 * @param resId
	 *     Resource ID.
	 */
	public void setSplashResource(final int resId) {
		if (splash == null) {
			splash = (ImageView) MainActivity.get().findViewById(R.id.splash);
			splash.setBackgroundColor(android.graphics.Color.TRANSPARENT);
		}

		splash.setImageResource(resId);
	}
}
