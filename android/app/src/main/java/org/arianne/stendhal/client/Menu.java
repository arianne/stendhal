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
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;


public class Menu {

	final Context ctx;
	final Toolbar nav;


	public Menu(final Context ctx) {
		this.ctx = ctx;

		nav = (Toolbar) ((Activity) ctx).findViewById(R.id.menu_main);
		nav.setVisibility(View.GONE);

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

	/**
	 * Sets actions when buttons are pressed.
	 */
	private void initButtonHandlers() {
		final MainActivity activity = (MainActivity) ctx;

		final Button btn_quit = (Button) activity.findViewById(R.id.btn_quit);
		btn_quit.setOnClickListener(new View.OnClickListener() {
			public void onClick(final View v) {
				nav.setVisibility(View.GONE);
				activity.onRequestQuit();
			}
		});
	}
}
