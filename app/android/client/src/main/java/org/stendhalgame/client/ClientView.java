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

import java.util.Random;

import org.stendhalgame.client.sound.MusicPlayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;


/**
 * Interface to handle web content.
 */
public class ClientView extends WebView {

	/** Image used as title page background. */
	private ImageView splash;

	/** Client URL path. */
	private String clientUrlSuffix = "client";

	/** Determines type of client to connect to. */
	private boolean testClient = false;
	/** Determines type of server to connect to. */
	private boolean testServer = false;
	private Boolean debugging;
	/** ID of page currently loaded. */
	private static PageId currentPage;
	/** ID of page previously loaded. */
	private PageId previousPage;

	private String stateId = "";
	private String seed = "";

	/** Singleton instance. */
	private static ClientView instance;


	/**
	 * Retrieves the static instance.
	 */
	public static ClientView get() {
		return instance;
	}

	/**
	 * Creates a new view.
	 *
	 * @param ctx
	 *   Activity Context to access application assets.
	 */
	public ClientView(final Context ctx) {
		super(ctx);
		onInit();
	}

	/**
	 * Creates a new view.
	 *
	 * @param ctx
	 *   Activity Context to access application assets.
	 * @param attrs
	 *   AttributeSet passed to parent (may be `null`).
	 */
	public ClientView(final Context ctx, final AttributeSet attrs) {
		super(ctx, attrs);
		onInit();
	}

	/**
	 * Creates a new view.
	 *
	 * @param ctx
	 *   Activity Context to access application assets.
	 * @param attrs
	 *   AttributeSet passed to parent (may be `null`).
	 * @param style
	 *   Attribute in the current theme that contains a reference to a style resource that supplies
	 *   default values for the view (can be 0 to not look for defaults).
	 */
	public ClientView(final Context ctx, final AttributeSet attrs, final int style) {
		super(ctx, attrs, style);
		onInit();
	}

	/**
	 * Initializes client WebView interface.
	 *
	 * TODO: allow multiple client view instances
	 */
	private void onInit() {
		// FIXME: may be considered unsafe as this is not technically a singleton
		instance = this;

		setBackgroundColor(Color.TRANSPARENT);

		final WebSettings viewSettings = getSettings();
		viewSettings.setJavaScriptEnabled(true);
		viewSettings.setDomStorageEnabled(true);

		// keep elements in position in portrait mode
		viewSettings.setLoadWithOverviewMode(true);
		viewSettings.setUseWideViewPort(true);

		// zoom controls
		viewSettings.setSupportZoom(true);
		viewSettings.setBuiltInZoomControls(true);
		viewSettings.setDisplayZoomControls(false);

		// allow autoplay of music
		viewSettings.setMediaPlaybackRequiresUserGesture(false);

		if (debugEnabled()) {
			// make WebView debuggable for debug builds
			setWebContentsDebuggingEnabled(true);
		}

		initWebViewClient();
		initDownloadHandler();
	}

	/**
	 * Initializes method overrides to handle page loading.
	 */
	private void initWebViewClient() {
		// XXX: not sure setting WebChromClient is doing anything, was recommended to
		//      fix touchmove events not registering
		setWebChromeClient(new WebChromeClient());

		setWebViewClient(new WebViewClient() {
			/**
			 * Handles pages loaded indirectly.
			 *
			 * @param view
			 *   The default WebView instance handling the request.
			 * @param request
			 *   Web request including the URL to be loaded.
			 * @return
			 *   `true` to abort default loading.
			 */
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final WebResourceRequest request) {
				Uri uri = request.getUrl();
				final Uri.Builder builder = uri.buildUpon();
				if (UrlHelper.isLoginUri(uri)) {
					builder.appendQueryParameter("build", AppInfo.getBuildType());
					builder.appendQueryParameter("version", AppInfo.getBuildVersion());
					builder.appendQueryParameter("state", stateId);
					builder.appendQueryParameter("seed", seed);
				}
				uri = builder.build();
				if (!UrlHelper.isInternalUri(uri)) {
					// open external links in default browser/app
					// FIXME: should we ask for confirmation?
					MainActivity.get().startActivity(new Intent(Intent.ACTION_VIEW, uri));
					return true;
				}
				view.loadUrl(UrlHelper.checkClientUrl(uri.toString()));
				return true;
			}

			/**
			 * Called before a URL is loaded.
			 *
			 * @param view
			 *   WebView instance handling page contents.
			 * @param url
			 *   HTTP string of target page.
			 * @param favicon
			 *   Favicon bitmap image.
			 */
			@Override
			public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
				MusicPlayer.stopMusic();
				super.onPageStarted(view, url, favicon);
			}

			/**
			 * Called after a URL is loaded.
			 *
			 * @param view
			 *   WebView instance handling page contents.
			 * @param url
			 *   HTTP string of target page.
			 */
			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);
				if (UrlHelper.isClientUrl(url)) {
					setPage(PageId.WEBCLIENT);
				} else if (url.equals("") || url.equals("about:blank")) {
					setPage(PageId.TITLE);
					if (PreferencesActivity.getBoolean("title_music", true)) {
						playTitleMusic();
					}
				} else {
					setPage(PageId.OTHER);
				}
				Menu.get().updateButtons();
				Logger.debug("page id: " + currentPage);
			}
		});
	}

	/**
	 * deprecated?
	 */
	@Override
	public boolean dispatchKeyEvent(final KeyEvent event) {
		final boolean ret = super.dispatchKeyEvent(event);
		// hide keyboard when "enter" pressed
		if (ClientView.isGameActive() && event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			((InputMethodManager) MainActivity.get()
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getWindowToken(), 0);
		}
		return ret;
	}

	/**
	 * Initializes interface for handling screenshots.
	 */
	private void initDownloadHandler() {
		setDownloadListener(new DownloadListener() {
			/**
			 * Handles downloading screenshot created by web client.
			 *
			 * @param url
			 *   Content URL to the screenshot image.
			 * @param userAgent
			 *   User agent used for download.
			 * @param contentDisposition
			 *   Content-disposition HTTP header, if present.
			 * @param mimetype
			 *   Content type reported by server (web client creates PNG image).
			 * @param contentLength
			 *   File size reported by server.
			 */
			@Override
			public void onDownloadStart(final String url, final String userAgent,
					final String contentDisposition, final String mimetype, final long contentLength) {
				final DownloadHandler handler = new DownloadHandler();
				handler.download(url, mimetype);
				if (handler.getResult()) {
					final String msg = "Downloaded file: " + handler.getMessage();
					Logger.debug(msg);
					Notifier.toast(msg);
				} else {
					final String msg = handler.getMessage();
					Logger.error(msg);
					Notifier.toast("ERROR: " + msg);
				}
			}
		});
	}

	/**
	 * Verifies intent state & parses & loads target URL.
	 *
	 * @param intent
	 *   Called login intent with state verification.
	 */
	public void checkLoginIntent(final Intent intent) {
		final Uri intentUri = intent.getData();
		final String url = intentUri.getQueryParameter("url");
		final String loginseed = intentUri.getQueryParameter("loginseed");
		final String intentStateId = intentUri.getQueryParameter("state");
		if (stateId == null || intentStateId == null || url == null || "".equals(stateId) || !stateId.equals(intentStateId)) {
			final String err = "There was an error verifying login";
			Logger.error(err + " (\"" + stateId + "\" == \"" + intentStateId + "\")");
			Notifier.showMessage(err);
			if (currentPage == null || PageId.TITLE.equals(previousPage)) {
				// reload title
				loadTitleScreen();
			}
			return;
		}
		String completeUrl = url + "&loginseed=" + loginseed + seed;
		loadUrl(UrlHelper.checkClientUrl(completeUrl));
	}

	/**
	 * Determines if URL to load is test web client.
	 */
	public boolean isTestClient() {
		return testClient;
	}

	/**
	 * Determines if connection is to test server.
	 */
	public boolean isTestServer() {
		return testServer;
	}

	/**
	 * Retrieves a string representing the selected client.
	 *
	 * @return
	 *   One of "main", "test", or "none".
	 */
	public String getSelectedClient() {
		if (ClientView.onTitleScreen()) {
			return "none";
		}
		return testClient ? "test" : "main";
	}

	/**
	 * Retrieves a string representing the selected server.
	 *
	 * @return
	 *   One of "main", "test", or "none".
	 */
	public String getSelectedServer() {
		if (ClientView.onTitleScreen()) {
			return "none";
		}
		return testServer ? "test" : "main";
	}

	/**
	 * Resets selected client & server values to default.
	 */
	private void reset() {
		testClient = false;
		testServer = false;
		clientUrlSuffix = "client";
		stateId = "";
		seed = "";
	}

	/**
	 * Retrieves client type string.
	 *
	 * @return
	 *   Either "client" or "testclient".
	 */
	public String getClientUrlSuffix() {
		return clientUrlSuffix;
	}

	/**
	 * Generates a random string for identifying state.
	 */
	private String generateRandomString() {
		// ensure state is reset before generating
		String result = "";

		// useable characters
		String charList = "0123456789";
		for (char c = 'A'; c <= 'Z'; c++) {
			charList += c;
		}
		for (char c = 'a'; c <= 'z'; c++) {
			charList += c;
		}

		// generate random state
		final Random rand = new Random();
		final int ccount = charList.length();
		for (int idx = 0; idx <= 20 ; idx++) {
			result += charList.charAt(rand.nextInt(ccount));
		}
		return result;
	}

	/**
	 * Opens location in client WebView.
	 *
	 * @param url
	 *   URL to load.
	 */
	@Override
	public void loadUrl(final String url) {
		Logger.debug("Loading URL: " + url);
		super.loadUrl(url);
	}

	/**
	 * Shows initial title screen.
	 */
	public void loadTitleScreen() {
		reset();
		setPage(PageId.TITLE);
		onUpdateTitleOrient(MainActivity.get().getOrientation());
		loadUrl("about:blank");
		Menu.get().show();
	}

	/**
	 * Attempts to connect to client host.
	 *
	 * FIXME: rename as it may cause confusion in regards to loading page "account/login.html"
	 */
	public void loadLogin() {
		if (debugEnabled() && PreferencesActivity.getString("client_url").trim().equals("")) {
			// debug builds support choosing between main & test client/server
			selectClient();
		} else {
			onSelectServer();
		}
	}

	/**
	 * Sets the background image.
	 *
	 * @param resId
	 *   Resource ID.
	 * @param bgColor
	 *   Coloring to fill behind splash image.
	 */
	private void setSplashResource(final int resId, final int bgColor) {
		if (splash == null) {
			splash = (ImageView) MainActivity.get().findViewById(R.id.splash);
		}
		splash.setBackgroundColor(bgColor);
		splash.setImageResource(resId);
	}

	/**
	 * Sets the background image.
	 *
	 * @param resId
	 *   Resource ID.
	 */
	private void setSplashResource(final int resId) {
		setSplashResource(resId, Color.TRANSPARENT);
	}

	/**
	 * Sets splash image dependent on device orientation.
	 *
	 * @param orient
	 *   Current screen orientation either portrait (1) or landscape (2).
	 */
	public void onUpdateTitleOrient(final int orient) {
		int splash = R.drawable.splash;
		if (orient == Configuration.ORIENTATION_PORTRAIT) {
			splash = R.drawable.splash_portrait;
		}
		// light blue background color
		setSplashResource(splash, 0xff6c9ed1);
	}

	/**
	 * Opens a message dialog for user to choose between main & test clients.
	 */
	private void selectClient() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(ClientView.get().getContext());
		builder.setMessage("Select client");

		builder.setPositiveButton("Main", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				testClient = false;
				clientUrlSuffix = "client";
				// skip server confirmation as only test client has this option
				onSelectServer();
				dialog.cancel();
			}
		});

		builder.setNegativeButton("Test", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				testClient = true;
				clientUrlSuffix = "testclient";
				selectServer();
				dialog.cancel();
			}
		});

		builder.create().show();
	}

	/**
	 * Opens a message dialog for user to choose between main & test servers.
	 */
	private void selectServer() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(ClientView.get().getContext());
		builder.setMessage("Select server");

		builder.setPositiveButton("Main", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				testServer = false;
				dialog.cancel();
				onSelectServer();
			}
		});

		builder.setNegativeButton("Test", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				testServer = true;
				dialog.cancel();
				onSelectServer();
			}
		});

		builder.create().show();
	}

	/**
	 * Connects to server & loads initial page.
	 */
	private void onSelectServer() {
		// create a unique state
		stateId = generateRandomString();
		seed = generateRandomString();
		// remove splash image & background coloring
		setSplashResource(android.R.color.transparent);

		final String initialPage = UrlHelper.getInitialPageUrl();
		Logger.debug("Loading initial page: " + initialPage);
		loadUrl(initialPage);
		setPage(PageId.OTHER);
		// hide menu after exiting title screen
		Menu.get().hide();
	}

	/**
	 * Checks if user has specified a URL pointing to a custom server/client.
	 *
	 * @return
	 *   Server address specified in preferences or `null`.
	 */
	public String checkCustomServer() {
		final String cs = PreferencesActivity.getString("client_url", "").trim();
		if (cs.equals("")) {
			return null;
		}
		return cs;
	}

	/**
	 * Checks if title page is currently loaded.
	 *
	 * @return
	 *   `true` if current page matches `PageId.TITLE`.
	 */
	public static boolean onTitleScreen() {
		return currentPage == PageId.TITLE;
	}

	/**
	 * Checks if web client page is currently loaded.
	 *
	 * @return
	 *   `true` if current page matches `PageId.WEBCLIENT`.
	 */
	public static boolean isGameActive() {
		return currentPage == PageId.WEBCLIENT;
	}

	/**
	 * Retrieves ID of current page.
	 *
	 * @return
	 *   Current `PageId`.
	 */
	public static PageId getCurrentPageId() {
		return currentPage;
	}

	/**
	 * Plays selected music.
	 *
	 * @param musicId
	 *   String identifier of music to play or `null` to play music configured in preferences.
	 */
	public static void playTitleMusic(String musicId) {
		if (musicId == null) {
			musicId = PreferencesActivity.getString("song_list");
		}
		int id = R.raw.title_01;
		switch (musicId) {
			case "title_02":
				id = R.raw.title_02;
				break;
			case "title_03":
				id = R.raw.title_03;
				break;
			case "title_04":
				id = R.raw.title_04;
				break;
			case "title_05":
				id = R.raw.title_05;
				break;
		}
		Logger.debug("playing music: " + musicId);
		MusicPlayer.playMusic(id, true);
	}

	/**
	 * Plays music configured in preferences.
	 */
	public static void playTitleMusic() {
		playTitleMusic(null);
	}

	/**
	 * Sets current page ID.
	 *
	 * @param newPage
	 *   Page ID to be used.
	 */
	private void setPage(final PageId newPage) {
		previousPage = ClientView.currentPage;
		ClientView.currentPage = newPage;
		if (previousPage == null) {
			previousPage = ClientView.currentPage;
		}
	}

	/**
	 * Checks if this is a debug build.
	 *
	 * @return
	 *   `true` if debug flag set.
	 */
	public boolean debugEnabled() {
		if (debugging != null) {
			return debugging;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			debugging = (MainActivity.get().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} else {
			debugging = false;
		}
		return debugging;
	}
}
