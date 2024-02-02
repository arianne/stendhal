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
import android.webkit.ValueCallback;


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
		parsedInfo = new HashMap<>();
	}

	public String put(final String key, final String value) {
		return parsedInfo.put(key, value);
	}

	public String get(final String key) {
		return parsedInfo.containsKey(key) ? parsedInfo.get(key) : "";
	}

	/**
	 * Parses build and version info from DOM.
	 */
	public void onClientConnected() {
		final ClientView client = ClientView.get();
		for (final String key: Arrays.asList("build", "version")) {
			final String script = "(function(){return document.documentElement.getAttribute(\"data-build-" + key +"\")})();";
			ClientView.get().evaluateJavascript(script, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String html) {
					// remove leading & trailing quotes
					final int len = html.length();
					if (len > 2) {
						html = html.substring(1, html.length()-1);
					}
					put(key, html);
				}
			});
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
