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
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

	private static MainActivity instance;

	private StendhalWebView client;
	private Menu menu;

	// flag to check if we have left the initial page
	public static boolean onInitialPage = true;


	public static MainActivity get() {
		return instance;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			instance = this;
			setContentView(R.layout.activity_main);

			// initialize debug logging mechanism
			DebugLog.init(getExternalFilesDir(null), this);

			// initialize settings store
			Settings.init(getExternalFilesDir(null));

			menu = new Menu(this);
			client = new StendhalWebView(this);
		} catch (final Exception e) {
			DebugLog.error(e.getMessage());
			Notifier.get().showPrompt(
				"An unhandled exception has occurred: " + e.getMessage(),
				new Notifier.Action() {
					protected void onCall() {
						finish();
					}
				});
		}
	}

	@Override
	public void onBackPressed() {
		menu.toggleVisibility();
	}

	/**
	 * Attempts to connect to client host.
	 */
	public void loadLogin() {
		client.loadLogin();
	}

	/**
	 * Opens a dialog confirm exiting activity.
	 */
	public void onRequestQuit() {
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

		if (!onInitialPage) {
			builder.setNeutralButton("Main Page", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					client.loadInitialScreen();
					dialog.cancel();
				}
			});
		}

		final AlertDialog confirmQuit = builder.create();
		confirmQuit.show();
	}

	@Override
	public void finish() {
		DebugLog.debug(MainActivity.class.getName() + ".finish() called");

		super.finish();
	}

	@Override
	protected void onDestroy() {
		DebugLog.debug(MainActivity.class.getName() + ".onDestroy() called");

		// XXX: settings only get saved if app exited via menu
		Settings.commitToFile();
		super.onDestroy();
	}
}
