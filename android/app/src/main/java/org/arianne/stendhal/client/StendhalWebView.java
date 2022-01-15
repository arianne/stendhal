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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;


public class StendhalWebView extends AppCompatActivity {

	private boolean testing = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		final WebView clientView = (WebView) findViewById(R.id.clientWebView);
		final WebSettings viewSettings = clientView.getSettings();

		viewSettings.setJavaScriptEnabled(true);

		// keep elements in position in portrait mode
		viewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		viewSettings.setLoadWithOverviewMode(true);
		viewSettings.setUseWideViewPort(true);

		clientView.setWebViewClient(new WebViewClient() {
			/* handle changing URLs */
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				/* FIXME: should not allow external URLs */
				view.loadUrl(getServerUrl(url));
				return false;
			}
		});

		// TODO: "main" should be default & "testing" used in separate build
		setServer();

		clientView.loadUrl("https://stendhalgame.org/account/mycharacters.html");
	}

	private String getServerUrl(String url) {
		if (url.contains("stendhalgame.org/client/") || url.contains("stendhalgame.org/testclient/")) {
			if (testing) {
				url = url.replace("/client/", "/testclient/");
			} else {
				url = url.replace("/testclient/", "/client/");
			}
		}

		return url;
	}

	private void setServer() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Select a server");
		builder.setCancelable(false);

		builder.setPositiveButton("Main", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				testing = false;
				dialog.cancel();
			}
		});

		builder.setNegativeButton("Testing", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				testing = true;
				dialog.cancel();
			}
		});

		final AlertDialog selectServer = builder.create();
		selectServer.show();
	}

	@Override
	public void onBackPressed() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Quit Stendhal?");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				finish();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		final AlertDialog confirmQuit = builder.create();
		confirmQuit.show();
	}
}
