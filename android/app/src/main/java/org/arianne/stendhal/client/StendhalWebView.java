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


public class StendhalWebView {

	// FIXME: page should be set to character select if focus is lost

	private static StendhalWebView instance;

	private static ClientView clientView;


	public static StendhalWebView get() {
		return instance;
	}

	public StendhalWebView() {
		instance = this;
		clientView = (ClientView) MainActivity.get().findViewById(R.id.clientWebView);

		loadTitleScreen();
	}

	/**
	 * Shows initial splash screen.
	 */
	public void loadTitleScreen() {
		clientView.loadTitleScreen();
	}

	/**
	 * Attempts to connect to client host.
	 */
	public void loadLogin() {
		clientView.loadLogin();
	}

	public static boolean onTitleScreen() {
		return clientView.onTitleScreen();
	}

	public static boolean isGameActive() {
		return clientView.isGameActive();
	}

	public static PageId getCurrentPageId() {
		return clientView.getCurrentPageId();
	}

	public static void playTitleMusic(final String musicId) {
		clientView.playTitleMusic(musicId);
	}

	public static void playTitleMusic() {
		clientView.playTitleMusic(null);
	}

	/**
	 * Reloads current page.
	 */
	public void reload() {
		clientView.reload();
	}
}
