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


public class Notifier {

	private AlertDialog.Builder builder;

	private static Notifier instance;


	public static Notifier get() {
		if (instance == null) {
			instance = new Notifier();
		}

		return instance;
	}

	private Notifier() {}

	private void showDialog() {
		if (builder != null) {
			builder.create().show();
		}

		builder = null;
	}

	private void createDialog() {
		builder = new AlertDialog.Builder(MainActivity.get());
	}

	public void showMessage(final String msg, final boolean cancelable, final String title) {
		createDialog();

		builder.setCancelable(cancelable);
		builder.setMessage(msg);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		showDialog();
	}

	public void showMessage(final String msg, final boolean cancelable) {
		showMessage(msg, cancelable, null);
	}

	public void showMessage(final String msg) {
		showMessage(msg, true, null);
	}

	public void showPrompt(final String msg, final Action... actions) {
		createDialog();

		builder.setCancelable(false);
		builder.setMessage(msg);

		String labelY = null;
		if (actions.length > 0 & actions[0] != null) {
			labelY = actions[0].getLabel();
		}
		if (labelY == null) {
			if (actions.length > 1) {
				labelY = "Yes";
			} else {
				labelY = "Ok";
			}
		}

		builder.setPositiveButton(labelY, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();

				if (actions.length > 0 && actions[0] != null) {
					actions[0].call();
				}
			}
		});

		if (actions.length > 1) {
			String labelN = null;
			if (actions[1] != null) {
				labelN = actions[1].getLabel();
			}
			if (labelN == null) {
				labelN = "No";
			}

			builder.setNegativeButton(labelN, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();

					if (actions[1] != null) {
						actions[1].call();
					}
				}
			});
		}

		if (actions.length > 2) {
			String labelO = null;
			if (actions[2] != null) {
				labelO = actions[2].getLabel();
			}
			if (labelO == null) {
				labelO = "Other";
			}

			builder.setNeutralButton(labelO, new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();

					if (actions[2] != null) {
						actions[2].call();
					}
				}
			});
		}

		showDialog();
	}

	public static abstract class Action {
		protected String label;


		public String getLabel() {
			return label;
		}

		public void call() {
			onCall();
		}

		protected abstract void onCall();
	}
}
