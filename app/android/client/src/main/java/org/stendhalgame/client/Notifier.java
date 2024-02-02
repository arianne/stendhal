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
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;


public class Notifier {

	private static AlertDialog.Builder builder;


	/**
	 * Static methods only.
	 */
	private Notifier() {
		// do nothing
	}

	private static void showDialog() {
		if (builder != null) {
			builder.create().show();
		}
		builder = null;
	}

	private static void createDialog(final Context ctx) {
		builder = new AlertDialog.Builder(ctx);
	}

	private static void createDialog() {
		createDialog(MainActivity.get());
	}

	public static void showMessage(final String msg, final boolean cancelable, final String title) {
		createDialog();

		builder.setCancelable(cancelable);
		builder.setMessage(msg);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});

		showDialog();
	}

	public static void showMessage(final String msg, final boolean cancelable) {
		showMessage(msg, cancelable, null);
	}

	public static void showMessage(final String msg) {
		showMessage(msg, true, null);
	}

	public static void showError(final String msg) {
		showMessage(msg, false, "Error");
	}

	public static void showPrompt(final Context ctx, final String msg, final Action... actions) {
		createDialog(ctx);

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
			@Override
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
				@Override
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
				@Override
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

	public static void showPrompt(final String msg, final Action... actions) {
		showPrompt(MainActivity.get(), msg, actions);
	}

	/**
	 * Wrapper for <code>Toast.makeText</code>.
	 *
	 * @param ctx
	 *     The context over which to show the message.
	 * @param msg
	 *     Text to be displayed.
	 * @param duration
	 *     Duration that text is on screen (default: <code>Toast.LENGTH_LONG</code>).
	 */
	public static void toast(final Context ctx, final String msg, final int duration) {
		Toast.makeText(ctx, msg, duration).show();
	}

	/**
	 * Wrapper for <code>Toast.makeText</code>.
	 *
	 * @param ctx
	 *     The context over which to show the message.
	 * @param msg
	 *     Text to be displayed.
	 */
	public static void toast(final Context ctx, final String msg) {
		toast(ctx, msg, Toast.LENGTH_LONG);
	}

	/**
	 * Wrapper for <code>Toast.makeText</code>.
	 *
	 * Displays a message on the main activity context.
	 *
	 * @param msg
	 *     Text to be displayed.
	 * @param duration
	 *     Duration that text is on screen (default: <code>Toast.LENGTH_LONG</code>).
	 */
	public static void toast(final String msg, final int duration) {
		toast(MainActivity.get(), msg, duration);
	}

	/**
	 * Wrapper for <code>Toast.makeText</code>.
	 *
	 * Displays a message on the main activity context.
	 *
	 * @param msg
	 *     Text to be displayed.
	 */
	public static void toast(final String msg) {
		toast(msg, Toast.LENGTH_LONG);
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
