/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
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

import java.util.Arrays;
import java.util.HashMap;

import android.webkit.JavascriptInterface;


/**
 * Class for getting information from web client.
 */
public class WebClientInfo {

	private HashMap<String, String> parsedInfo;

	/** Singleton instance. */
	private static WebClientInfo instance;


	/**
	 * Retrieves the singleton instance.
	 */
	public static WebClientInfo get() {
		if (WebClientInfo.instance == null) {
			WebClientInfo.instance = new WebClientInfo();
		}
		return WebClientInfo.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private WebClientInfo() {
		ClientView.get().addJavascriptInterface(this, "wci");
		parsedInfo = new HashMap<>();
	}

	@JavascriptInterface
	public String put(final String key, final String value) {
		return parsedInfo.put(key, value);
	}

	public String get(final String key) {
		return parsedInfo.containsKey(key) ? parsedInfo.get(key) : "";
	}

	/**
	 * Parses build and version info from DOM.
	 *
	 * FIXME: overwrites page, need a way to do this in background
	 */
	public void onClientConnected() {
		// Note: could not get info from stored value directly (stendhal.data.build.<key>) nor from
		//       attribute added to main element (document.documentElement.getAttribute("data-build-<key>"))
		//       so info is parsed from element with ID "build-<key>"
		for (final String key: Arrays.asList("build", "version")) {
			ClientView.get().loadUrl("javascript:window.wci.put('" + key + "', document.getElementById('build-" + key + "').innerText);");
		}
	}

	/**
	 * Retrieves build string parsed from web client DOM.
	 */
	public String getBuild() {
		if (!ClientView.isGameActive()) {
			return "not connected";
		}
		final String info = get("build");
		return "".equals(info) ? "not available" : info;
	}

	/**
	 * Retrieves version string parsed from web client DOM.
	 */
	public String getVersion() {
		if (!ClientView.isGameActive()) {
			return "not connected";
		}
		final String info = get("version");
		return "".equals(info) ? "not available" : info;
	}
}
