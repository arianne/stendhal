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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;


public class StendhalWebView {

	// FIXME: page should be set to character select if focus is lost

	private static DebugLog logger = DebugLog.get();
	private static Notifier notifier = Notifier.get();

	private static StendhalWebView instance;

	private boolean testing = false;
	private boolean gameActive = false;
	private Boolean debugging;

	private final Context ctx;
	private WebView clientView;
	private ImageView splash;
	//private InputMethodManager imm;

	//private String currentHTML;

	private final int doubleTapThreshold = 300;
	private long timestampTouchUp = 0;
	private long timestampTouchUpPrev = 0;
	private int tapCount = 0;



	public static StendhalWebView get() {
		return instance;
	}

	public StendhalWebView(final Context ctx) {
		instance = this;
		this.ctx = ctx;
		final AppCompatActivity mainActivity = (AppCompatActivity) ctx;

		// FIXME: need to manually initialize WebView to override onCreateInputConnection
		clientView = (WebView) mainActivity.findViewById(R.id.clientWebView);
		clientView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
		/*
		clientView = new WebView(mainActivity) {
			@Override
			public InputConnection onCreateInputConnection(final EditorInfo outAttrs) {
				outAttrs.inputType = InputType.TYPE_NULL;
				return new BaseInputConnection(getView(), false);
			}
		};
		*/

		splash = (ImageView) mainActivity.findViewById(R.id.splash);
		splash.setBackgroundColor(android.graphics.Color.TRANSPARENT);

		if (debugEnabled()) {
			// make WebView debuggable for debug builds
			clientView.setWebContentsDebuggingEnabled(true);
		}

		final WebSettings viewSettings = clientView.getSettings();

		viewSettings.setJavaScriptEnabled(true);

		// keep elements in position in portrait mode
		viewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// <-- SINGLE_COLUMN deprecated
		viewSettings.setLoadWithOverviewMode(true);
		viewSettings.setUseWideViewPort(true);

		// disable zoom
		viewSettings.setSupportZoom(false);
		viewSettings.setBuiltInZoomControls(false);
		viewSettings.setDisplayZoomControls(false);

		/*
		clientView.addJavascriptInterface(new JSInterface() {
			@Override
			protected void onFire() {
				currentHTML = getHTML();
			}
		}, "JSI");
		*/

		initWebViewClient();
		initTouchHandler();
		initKeyboardHandler();

		loadTitleScreen();
	}

	/**
	 * Shows initial splash screen.
	 */
	public void loadTitleScreen() {
		splash.setImageResource(R.drawable.splash);

		clientView.loadUrl("about:blank");

		final Menu m = Menu.get();
		m.show();
		MainActivity.onInitialPage = true;
	}

	private void initWebViewClient() {
		clientView.setWebViewClient(new WebViewClient() {
			/* handle changing URLs */
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				if (!isInternalUrl(url)) {
					// FIXME: should we ask for confirmation?
					((Activity) ctx).startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				}

				view.loadUrl(checkClientUrl(url));
				gameActive = isClientUrl(clientView.getUrl());
				return false;
			}
		});
	}

	private void initTouchHandler() {
		clientView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				/*
				if (gameActive && event.getAction() == MotionEvent.ACTION_UP) {
clientView.loadUrl("javascript:window.JSI.fire('<html>'+document.activeElement.innerHTML+'</html>');");

					if (debugEnabled()) {
						ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
						executorService.schedule(new Runnable() {
							@Override
							public void run() {
								DebugLog.notify(currentHTML, (Activity) ctx);
							}
						}, 800, TimeUnit.MILLISECONDS);
					}
				}
				*/

				if (gameActive) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							tapCount++;
							break;
						case MotionEvent.ACTION_UP:
							timestampTouchUpPrev = timestampTouchUp;
							timestampTouchUp = System.currentTimeMillis();

							if (tapCount > 1) {
								tapCount = 0;
								if (timestampTouchUp - timestampTouchUpPrev <= doubleTapThreshold) {
									logger.debug("consumed double-tap event");
									// disables double-tap zoom
									// FIXME: need a workaround if double-taps should be used in game
									return true;
								}
							}

							break;
					}
				}

				return false;
			}
		});
	}

	private void initKeyboardHandler() {
		//imm = (InputMethodManager) ((Activity) ctx).getSystemService(Context.INPUT_METHOD_SERVICE);

		clientView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(final View view, final int keyCode, final KeyEvent event) {
				// FIXME: cannot catch soft keyboard events without overriding WebView.onCreateInputConnection

				return false;
			}
		});
	}

	/**
	 * Checks if a URL is a link to one of the web clients.
	 *
	 * @param url
	 *     URL to be checked.
	 * @return
	 *     <code>true</code> if url links to "client" or "testclient".
	 */
	private boolean isClientUrl(final String url) {
		return url.contains("stendhalgame.org/client/")
			|| url.contains("stendhalgame.org/testclient/");
	}

	/**
	 * Formats client URL for currently selected server.
	 *
	 * @param url
	 *     URL to be checked.
	 * @return
	 *     URL to be loaded.
	 */
	private String checkClientUrl(String url) {
		if (isClientUrl(url)) {
			if (testing) {
				url = url.replace("/client/", "/testclient/");
			} else {
				url = url.replace("/testclient/", "/client/");
			}
		}

		return url;
	}

	/**
	 * Checks if requested URL is whitelisted to be opened within WebView client.
	 *
	 * @param url
	 *     URL to be checked.
	 * @return
	 *     <code>true</code> if URL is under domain stendhalgame.org or localhost.
	 */
	private boolean isInternalUrl(final String url) {
		final String stripped = url.replaceAll("^https://", "")
			.replaceAll("^http://", "").replaceAll("^www\\.", "");

		return stripped.startsWith("stendhalgame.org") || stripped.startsWith("localhost");
	}

	/**
	 * Opens a message dialog for user to choose between main & test servers.
	 */
	private void selectServer() {
		final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) ctx);
		builder.setMessage("Select a server");
		builder.setCancelable(false);

		builder.setPositiveButton("Main", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				testing = false;
				dialog.cancel();
				onSelectServer();
			}
		});

		builder.setNegativeButton("Testing", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				testing = true;
				dialog.cancel();
				onSelectServer();
			}
		});

		final AlertDialog selectServer = builder.create();
		selectServer.show();
	}

	private void onSelectServer() {
		// remove splash image
		splash.setImageResource(android.R.color.transparent);

		// initial page
		clientView.loadUrl("https://stendhalgame.org/account/mycharacters.html");

		if (testing) {
			logger.debug("Connecting to test server");
		} else {
			logger.debug("Connecting to main server");

			notifier.showMessage("CAUTION: This software is in early development and not recommended"
				+ " for use on the main server. Proceed with caution.", false);
		}

		MainActivity.onInitialPage = false;
	}

	/**
	 * Attempts to connect to client host.
	 */
	public void loadLogin() {
		if (debugEnabled()) {
			// debug builds support choosing between main & test server
			selectServer();
		} else {
			onSelectServer();
		}
	}

	public boolean isGameActive() {
		return gameActive;
	}

	/**
	 * Reloads current page.
	 */
	public void reload() {
		clientView.reload();
	}

	/**
	 * Checks if this is a debug build.
	 *
	 * @return
	 *     <code>true</code> if debug flag set.
	 */
	private boolean debugEnabled() {
		if (debugging != null) {
			return debugging;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			debugging = (((Activity) ctx).getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} else {
			debugging = false;
		}

		return debugging;
	}
}
