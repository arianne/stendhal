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
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toolbar;


public class Menu {

	private static Menu instance;

	private final Context ctx;
	private final Toolbar nav;

	private Button btn_login;


	public static Menu get() {
		return instance;
	}

	public Menu(final Context ctx) {
		instance = this;
		this.ctx = ctx;

		nav = (Toolbar) ((Activity) ctx).findViewById(R.id.menu_main);

		nav.setTag(nav.getVisibility());
		nav.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (nav.getVisibility() == View.VISIBLE) {
					if (!MainActivity.onInitialPage) {
						btn_login.setVisibility(View.GONE);
					} else {
						btn_login.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		initButtonHandlers();
	}

	public Toolbar getInternal() {
		return nav;
	}

	public void setVisibility(final int vType) {
		nav.setVisibility(vType);
	}

	public void toggleVisibility() {
		if (nav.getVisibility() == View.GONE) {
			nav.setVisibility(View.VISIBLE);
		} else {
			nav.setVisibility(View.GONE);
		}
	}

	public void show() {
		nav.setVisibility(View.VISIBLE);
	}

	public void hide() {
		nav.setVisibility(View.GONE);
	}

	/**
	 * Sets actions when buttons are pressed.
	 */
	private void initButtonHandlers() {
		final MainActivity activity = (MainActivity) ctx;

		btn_login = (Button) activity.findViewById(R.id.btn_login);
		btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				nav.setVisibility(View.GONE);
				activity.loadLogin();
			}
		});

		final Button btn_settings = (Button) activity.findViewById(R.id.btn_settings);
		btn_settings.setOnClickListener(new ClickListener() {
			public void onClick(final View v) {
				super.onClick(v);
			}
		});

		final Button btn_about = (Button) activity.findViewById(R.id.btn_about);
		btn_about.setOnClickListener(new ClickListener() {
			public void onClick(final View v) {
				super.onClick(v);

				final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) ctx);
				builder.setMessage("WebView client version: "
					+ BuildConfig.VERSION_NAME + "\nServer version: unavailable");
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});

				builder.create().show();
			}
		});

		final Button btn_quit = (Button) activity.findViewById(R.id.btn_quit);
		btn_quit.setOnClickListener(new ClickListener() {
			public void onClick(final View v) {
				super.onClick(v);
				activity.onRequestQuit();
			}
		});
	}

	private class ClickListener implements View.OnClickListener {
		public void onClick(final View v) {
			if (!MainActivity.onInitialPage) {
				nav.setVisibility(View.GONE);
			}
		}
	}
}
