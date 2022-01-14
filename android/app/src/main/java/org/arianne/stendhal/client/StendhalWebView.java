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

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;


public class StendhalWebView extends AppCompatActivity {

	private boolean testing = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		final WebView clientWebView = (WebView) findViewById(R.id.clientWebView);
		clientWebView.getSettings().setJavaScriptEnabled(true);

		clientWebView.setWebViewClient(new WebViewClient() {
			/* handle changing URLs */
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				/* FIXME: should not allow external URLs */
				view.loadUrl(getServerUrl(url));
				return false;
			}
		});

		clientWebView.loadUrl("https://stendhalgame.org/testclient/stendhal.html");
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
}
