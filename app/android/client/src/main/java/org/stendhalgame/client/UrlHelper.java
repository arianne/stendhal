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

import android.net.Uri;


class UrlHelper {

	private static final String defaultServer = "https://stendhalgame.org/";


	private UrlHelper() {
		// static methods only
	}

	/**
	 * Creates a URI from a string HTTP URL.
	 *
	 * @return
	 *   `android.net.Uri`.
	 */
	public static Uri toUri(String url) {
		if (!url.startsWith("https://") && !url.startsWith("http://") && !url.startsWith("about:")) {
			// Uri.getHost returns `null` if localhost not prefixed with protocol
			url = "http://" + url;
		}
		return Uri.parse(url);
	}

	/**
	 * Retrieves the HTTP URL of the default server (stendhalgame.org).
	 *
	 * @return
	 *   String URL.
	 */
	public static String getDefaultServer() {
		return UrlHelper.defaultServer;
	}

	/**
	 * Retrieves the URI of the default server (stendhalgame.org).
	 *
	 * @return
	 *   `android.net.Uri` of default server.
	 */
	public static Uri getDefaultServerUri() {
		return UrlHelper.toUri(UrlHelper.defaultServer);
	}

	/**
	 * Retrieves the default host.
	 *
	 * @return
	 *   Host portion of the default URI.
	 */
	public static String getDefaultHost() {
		return UrlHelper.getDefaultServerUri().getHost();
	}

	/**
	 * Removes protocol & "www" prefixes from a URL.
	 *
	 * FIXME: rename to "trimProtocol"
	 *
	 * @return
	 *   Trimmed URL string.
	 */
	public static String stripHost(final String url) {
		if (url == null) {
			return "";
		}
		return url.replaceAll("^https://", "").replaceAll("^http://", "")
			.replaceAll("^www\\.", "");
	}

	/**
	 * Extracts character name from URL fragment identifier & converts to query string.
	 *
	 * Deprecated. Should use `UrlHelper.formatCharName(Uri, Uri.Builder).
	 *
	 * @param url
	 *   HTTP string to be formatted.
	 * @return
	 *   Formatted URL.
	 */
	@Deprecated
	public static String formatCharName(String url) {
		final int idx = url.indexOf("#");
		if (idx > -1) {
			url = url.substring(0, idx) + "?char=" + url.substring(idx+1);
		}
		return url;
	}

	/**
	 * Converts URL fragment identifier to character name query string parameter.
	 *
	 * @param uri
	 *   URI being checked for fragment identifier.
	 * @param builder
	 *   URI builder to update.
	 */
	public static void formatCharName(final Uri uri, final Uri.Builder builder) {
		final String name = uri.getFragment();
		if (name != null) {
			builder.appendQueryParameter("char", name);
		}
	}

	/**
	 * Formats client URL for currently selected server.
	 *
	 * @param url
	 *   URL to be checked.
	 * @return
	 *   URL to be loaded.
	 */
	public static String checkClientUrl(String url) {
		final Uri uri = UrlHelper.toUri(url);
		final Uri.Builder builder = uri.buildUpon();
		if (UrlHelper.isClientUrl(url)) {
			final ClientView client = MainActivity.get().getActiveClientView();
			final boolean testClient = client.isTestClient();
			String replaceSuffix = "/testclient/";
			if (testClient) {
				replaceSuffix = "/client/";
			}
			// ensure website directs to configured client
			builder.path(uri.getPath().replace(replaceSuffix, "/" + client.getClientUrlSuffix() + "/"));
			UrlHelper.formatCharName(uri, builder);
			if (testClient && !client.isTestServer() && uri.getQueryParameter("server") == null) {
				// connect test client to main server
				builder.appendQueryParameter("server", "main");
			}
		}
		return builder.toString();
	}

	/**
	 * Checks if a URL is a link to one of the web clients.
	 *
	 * @param url
	 *   HTTP URL to be checked.
	 * @return
	 *   `true` if `url` links to "client" or "testclient".
	 */
	public static boolean isClientUrl(final String url) {
		final String custom_client = PreferencesActivity.getString("client_url").trim();
		if (!custom_client.equals("")) {
			return url.contains(custom_client);
		}
		final String defaultHost = UrlHelper.stripHost(UrlHelper.getDefaultServerUri().getHost());
		return url.contains(defaultHost + "/client/") || url.contains(defaultHost + "/testclient/");
	}

	/**
	 * Retrieves client URL.
	 *
	 * @return
	 *   Client URL string.
	 */
	public static String getClientUrl() {
		final String custom_client = PreferencesActivity.getString("client_url").trim();
		if (!custom_client.equals("")) {
			return custom_client;
		}
		return UrlHelper.defaultServer + "client/stendhal.html";
	}

	/**
	 * Retrieves URL string for initial page to be loaded from server.
	 *
	 * If custom client URL is used then client URL is returned.
	 *
	 * @return
	 *   HTTP URL string.
	 */
	public static String getInitialPageUrl() {
		final String custom_client = PreferencesActivity.getString("client_url").trim();
		if (!custom_client.equals("")) {
			return custom_client;
		}
		return UrlHelper.defaultServer + "account/mycharacters.html";
	}

	/**
	 * Checks if requested URL is whitelisted to be opened within WebView client.
	 *
	 * @param uri
	 *   `android.net.Uri` to be checked.
	 * @return
	 *   `true` if URI is under default domain (stendhalgame.org) or localhost.
	 */
	public static boolean isInternalUri(final Uri uri) {
		if (UrlHelper.isLoginUri(uri)) {
			// open login page in external browser
			return false;
		}
		final String defaultHost = UrlHelper.stripHost(UrlHelper.getDefaultHost());
		final String host = UrlHelper.stripHost(uri.getHost());
		if (defaultHost.equals(host)) {
			// allow pages from stendhalgame.org
			return true;
		}
		final String cs = MainActivity.get().getActiveClientView().checkCustomServer();
		if (cs != null) {
			return UrlHelper.stripHost(cs).equals(host);
		}
		return "localhost".equals(host);
	}

	/**
	 * Checks if requested URL is whitelisted to be opened within WebView client.
	 *
	 * @param url
	 *   HTTP URL string to be checked.
	 * @return
	 *   `true` if URL is under default domain (stendhalgame.org) or localhost.
	 */
	public static boolean isInternal(final String url) {
		return UrlHelper.isInternalUri(UrlHelper.toUri(url));
	}

	/**
	 * Checks if a URI matches the intent URL scheme.
	 *
	 * @param uri
	 *   URI to be check.
	 * @return
	 *   `true` if URI's host is the intent scheme.
	 */
	public static boolean isIntentUri(final Uri uri) {
		return AppInfo.getIntentUrlScheme().equals(UrlHelper.stripHost(uri.getHost()));
	}

	/**
	 * Checks if a URL matches the intent URL scheme.
	 *
	 * @param url
	 *   HTTP URL string to be check.
	 * @return
	 *   `true` if URI's host is the intent scheme.
	 */
	public static boolean isIntentUrl(final String url) {
		return UrlHelper.isIntentUri(UrlHelper.toUri(url));
	}

	/**
	 * Checks if a URI represents a login page.
	 *
	 * @param uri
	 *   Page URI to check.
	 * @return
	 *   `true` if URI path equals "/account/login.html" or `id` parameter of query string equals
	 *   "content/account/login".
	 */
	public static boolean isLoginUri(final Uri uri) {
		if ("/account/login.html".equals(uri.getPath())) {
			return true;
		}
		if (!uri.isHierarchical()) {
			return false;
		}
		final String id = uri.getQueryParameter("id");
		if (id == null) {
			return false;
		}
		return "content/account/login".equals(id);
	}
}
