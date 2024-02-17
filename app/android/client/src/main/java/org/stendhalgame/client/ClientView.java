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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;


public class ClientView extends WebView {

	private static ClientView instance;

	private ImageView splash;

	private String clientUrlSuffix = "client";

	private boolean testClient = false;
	private boolean testServer = false;
	private Boolean debugging;
	private static PageId currentPage;
	private PageId previousPage;

	private String stateId = "";


	/**
	 * Retrieves the static instance.
	 */
	public static ClientView get() {
		return instance;
	}

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
		instance = this;

		setBackgroundColor(android.graphics.Color.TRANSPARENT);

		final WebSettings viewSettings = getSettings();
		viewSettings.setJavaScriptEnabled(true);
		viewSettings.setDomStorageEnabled(true);

		// keep elements in position in portrait mode
		viewSettings.setLoadWithOverviewMode(true);
		viewSettings.setUseWideViewPort(true);

		// disable zoom
		viewSettings.setSupportZoom(false);
		viewSettings.setBuiltInZoomControls(false);
		viewSettings.setDisplayZoomControls(false);

		// allow autoplay of music
		viewSettings.setMediaPlaybackRequiresUserGesture(false);

		if (debugEnabled()) {
			// make WebView debuggable for debug builds
			setWebContentsDebuggingEnabled(true);
		}

		initWebViewClient();
		initDownloadHandler();
		// initialize web client info JavaScript interface
		WebClientInfo.get();
	}

	private void initWebViewClient() {
		// XXX: not sure setting WebChromClient is doing anything, was recommended to
		//      fix touchmove events not registering
		setWebChromeClient(new WebChromeClient());

		setWebViewClient(new WebViewClient() {
			/* handle changing URLs */
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				if (!UrlHelper.isInternal(url)) {
					// open external links in default browser/app
					// FIXME: should we ask for confirmation?
					MainActivity.get().startActivity(new Intent(Intent.ACTION_VIEW, UrlHelper.toUri(url)));
					return true;
				}
				view.loadUrl(UrlHelper.checkClientUrl(url));
				return false;
			}

			@Override
			public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
				MusicPlayer.stopMusic();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(final WebView view, final String url) {
				super.onPageFinished(view, url);

				if (UrlHelper.isClientUrl(url)) {
					setPage(PageId.WEBCLIENT);
					WebClientInfo.get().onClientConnected();
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
	 * Handles downloading screenshot created by web client.
	 */
	private void initDownloadHandler() {
		setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(final String url, final String userAgent,
					final String contentDisposition, final String mimetype,
					final long contentLength) {

				DownloadHandler handler = new DownloadHandler();
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
	 * Verifies intent state & load URI.
	 *
	 * @param intent
	 *   Called login intent with state verification.
	 */
	public void checkLoginIntent(final Intent intent) {
		final Uri intentUri = intent.getData();
		final String intentStateId = intentUri.getQueryParameter("state");
		if (stateId == null || intentStateId == null || "".equals(stateId) || !stateId.equals(intentStateId)) {
			final String err = "There was an error verifying login";
			Logger.error(err + " (\"" + stateId + "\" == \"" + intentStateId + "\")");
			Notifier.showMessage(err);
			if (currentPage == null || PageId.TITLE.equals(previousPage)) {
				// reload title
				loadTitleScreen();
			}
			return;
		}
		loadUrl(UrlHelper.checkClientUrl(UrlHelper.getClientUrl()));
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

	public String getSelectedClient() {
		if (ClientView.onTitleScreen()) {
			return "none";
		}
		return testClient ? "test" : "main";
	}

	public String getSelectedServer() {
		if (ClientView.onTitleScreen()) {
			return "none";
		}
		return testServer ? "test" : "main";
	}

	private void reset() {
		testClient = false;
		testServer = false;
		clientUrlSuffix = "client";
		stateId = "";
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
	private String generateStateId() {
		// ensure state is reset before generating
		stateId = "";

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
				stateId += charList.charAt(rand.nextInt(ccount));
		}
		return stateId;
	}

	@Override
	public void loadUrl(final String url) {
		Logger.debug("loading URL: " + url);
		super.loadUrl(url);
	}

	/**
	 * Shows initial title screen.
	 */
	public void loadTitleScreen() {
		reset();
		setPage(PageId.TITLE);
		setSplashResource(R.drawable.splash);
		loadUrl("about:blank");
		Menu.get().show();
	}

	/**
	 * Attempts to connect to client host.
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
	 *     Resource ID.
	 */
	private void setSplashResource(final int resId) {
		if (splash == null) {
			splash = (ImageView) MainActivity.get().findViewById(R.id.splash);
			splash.setBackgroundColor(android.graphics.Color.TRANSPARENT);
		}

		splash.setImageResource(resId);
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
		// remove splash image
		setSplashResource(android.R.color.transparent);

		String initialPage = UrlHelper.getDefaultServer() + "account/login.html";
		final String customPage = checkCustomServer();
		if (customPage != null) {
			Logger.debug("Connecing to custom page: " + customPage);
			// custom client URL may add character name fragement
			initialPage = UrlHelper.formatCharName(customPage);
		} else {
			if (testServer) {
				Logger.debug("Connecting to test server");
			} else {
				Logger.debug("Connecting to main server");
			}
		}
		final Uri.Builder builder = UrlHelper.toUri(initialPage).buildUpon();
		builder.appendQueryParameter("build", AppInfo.getBuildType());
		builder.appendQueryParameter("version", AppInfo.getBuildVersion());
		builder.appendQueryParameter("state", generateStateId());
		loadUrl(builder.toString());
		setPage(PageId.OTHER);
		// hide menu after exiting title screen
		Menu.get().hide();
	}

	public String checkCustomServer() {
		final String cs = PreferencesActivity.getString("client_url", "").trim();

		if (cs.equals("")) {
			return null;
		}

		return cs;
	}

	public static boolean onTitleScreen() {
		return currentPage == PageId.TITLE;
	}

	public static boolean isGameActive() {
		return currentPage == PageId.WEBCLIENT;
	}

	public static PageId getCurrentPageId() {
		return currentPage;
	}

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
	 *     <code>true</code> if debug flag set.
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
