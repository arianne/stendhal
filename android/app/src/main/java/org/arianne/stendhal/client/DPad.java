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

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import java.util.LinkedList;
import java.util.List;


/**
 * A directional pad on the screen.
 *
 * TODO: Option to use joystick style d-pad. Currently setting "dpad_joy"
 *   is disabled in preferences manager.
 */
public class DPad {

	private static DPad instance;

	private static Context ctx;

	private final List<ArrowView> arrows;
	private final ConstraintLayout layout;

	private enum Dir {
		LEFT,
		RIGHT,
		UP,
		DOWN;
	}


	public static DPad get() {
		if (instance == null) {
			instance = new DPad();
		}

		return instance;
	}

	private DPad() {
		ctx = (Context) MainActivity.get();

		arrows = new LinkedList<>();
		layout = new ConstraintLayout(ctx);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		final ArrowView arrow_l = new ArrowView(ctx, R.drawable.dpad_arrow_left, Dir.LEFT);
		final ArrowView arrow_r = new ArrowView(ctx, R.drawable.dpad_arrow_right, Dir.RIGHT);
		final ArrowView arrow_u = new ArrowView(ctx, R.drawable.dpad_arrow_up, Dir.UP);
		final ArrowView arrow_d = new ArrowView(ctx, R.drawable.dpad_arrow_down, Dir.DOWN);

		arrows.add(arrow_l);
		arrows.add(arrow_r);
		arrows.add(arrow_u);
		arrows.add(arrow_d);

		for (final ArrowView av: arrows) {
			layout.addView(av);
		}
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	public void setVisibility(final int vis) {
		for (final ArrowView av: arrows) {
			av.setVisibility(vis);
		}
	}

	public void setPosition(final int x, final int y) {
		ArrowView av = arrows.get(0);
		av.setX(x);
		av.setY(y + 60);

		av = arrows.get(1);
		av.setX(x + 150);
		av.setY(y + 60);

		av = arrows.get(2);
		av.setX(x + 60);
		av.setY(y);

		av = arrows.get(3);
		av.setX(x + 60);
		av.setY(y + 150);
	}

	public void onRefreshView() {
		final Point size = new Point();
		final Display disp = MainActivity.get().getWindowManager().getDefaultDisplay();
		disp.getSize(size);

		int x = 100;
		int y = size.y - 300;

		setPosition(x, y);

		if (PreferencesActivity.getSharedPreferences().getBoolean("show_dpad", false)) {
			setVisibility(View.VISIBLE);
		} else {
			setVisibility(View.INVISIBLE);
		}
	}


	private static class ArrowView extends ImageView {

		private final Dir dir;

		private boolean keyDown = false;


		public ArrowView(final Context ctx, final int id, final Dir dir) {
			super(ctx);

			this.dir = dir;

			setBackgroundColor(android.graphics.Color.TRANSPARENT);
			setImageResource(id);

			setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(final View view, final MotionEvent event) {
					final int action = event.getAction();

					Integer newAction = null;
					Integer keyCode = null;

					if (action == MotionEvent.ACTION_DOWN && !keyDown) {
						newAction = KeyEvent.ACTION_DOWN;
						keyDown = true;
					} else if (action == MotionEvent.ACTION_UP && keyDown) {
						newAction = KeyEvent.ACTION_UP;
						keyDown = false;
					}

					if (newAction != null) {

						switch (dir) {
							case LEFT:
								keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
								break;
							case RIGHT:
								keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
								break;
							case UP:
								keyCode = KeyEvent.KEYCODE_DPAD_UP;
								break;
							case DOWN:
								keyCode = KeyEvent.KEYCODE_DPAD_DOWN;
								break;
						}

						MainActivity.get().dispatchKeyEvent(new KeyEvent(newAction, keyCode));
						return true; // consume event
					}

					return false;
				}
			});
		}
	}
}
