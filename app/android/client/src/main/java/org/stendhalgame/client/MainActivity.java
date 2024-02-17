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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends AppCompatActivity {

	private static MainActivity instance;

	private ConstraintLayout layout;
	private ClientView client;
	private Menu menu;


	public static MainActivity get() {
		return instance;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			instance = this;

			// initialize debug logging mechanism
			Logger.init(getExternalFilesDir(null), this);

			updateOrientation();

			setContentView(R.layout.activity_main);
			layout = (ConstraintLayout) findViewById(R.id.content);
			client = (ClientView) findViewById(R.id.clientWebView);
			menu = new Menu(client.getContext());

			client.loadTitleScreen();
		} catch (final Exception e) {
			// TODO: add option to save to file or copy to clipboard the error
			e.printStackTrace();
			Logger.error(e.toString());
			Logger.error("// -- //");
			final StringBuilder sb = new StringBuilder();
			for (final StackTraceElement ste: e.getStackTrace()) {
				final String traceLine = ste.toString();
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(traceLine);
				Logger.error(traceLine);
			}
			Logger.error("// -- //");
			Notifier.showPrompt(
				"An unhandled exception has occurred: \"" + e.getMessage() + "\""
				+ "\n\nYou can report this error at: https://stendhalgame.org/development/bug.html"
				+ "\n\nStack trace:\n" + sb.toString(),
				new Notifier.Action() {
					@Override
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
	 * Sets screen orientation to user setting or locks in landscape or portrait.
	 */
	public void updateOrientation() {
		final String value = PreferencesActivity.getString("orientation");
		int orient = ActivityInfo.SCREEN_ORIENTATION_USER;
		switch (value) {
			case "landscape":
				orient = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
				break;
			case "portrait":
				orient = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				break;
		}
		// set orientation for main & sub activities
		setRequestedOrientation(orient);
		final PreferencesActivity preferencesActivity = PreferencesActivity.get();
		if (preferencesActivity != null) {
			preferencesActivity.setRequestedOrientation(orient);
		}
	}

	/**
	 * Opens a dialog confirm exiting activity.
	 */
	public void onRequestQuit() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(client.getContext());
		builder.setMessage("Quit Stendhal?");

		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		final AlertDialog confirmQuit = builder.create();
		confirmQuit.show();
	}

	//~ @Override
	//~ protected void onResume() {
		//~ super.onResume();
	//~ }

	/**
	 * Updates direction pad position when screen orientation changes.
	 */
	//~ @Override
	//~ public void onConfigurationChanged(final Configuration config) {
		//~ super.onConfigurationChanged(config);
	//~ }

	@Override
	public void finish() {
		Logger.debug(MainActivity.class.getName() + ".finish() called");

		super.finish();
	}

	@Override
	protected void onDestroy() {
		Logger.debug(MainActivity.class.getName() + ".onDestroy() called");

		super.onDestroy();
	}

	public void showSettings() {
		startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
	}
}
