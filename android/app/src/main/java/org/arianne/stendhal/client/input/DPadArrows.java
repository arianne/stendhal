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
package org.arianne.stendhal.client.input;

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

import marauroa.common.Pair;
import org.arianne.stendhal.client.DebugLog;
import org.arianne.stendhal.client.MainActivity;
import org.arianne.stendhal.client.PreferencesActivity;
import org.arianne.stendhal.client.R;


/**
 * A directional pad on the screen.
 *
 * TODO: Option to use joystick style d-pad. Currently setting "dpad_joy"
 *   is disabled in preferences manager.
 */
public class DPadArrows extends DPad {

	private static DPadArrows instance;

	private static Context ctx;

	private final List<ArrowView> arrows;
	private final ConstraintLayout layout;


	public static DPadArrows get() {
		if (instance == null) {
			instance = new DPadArrows();
		}

		return instance;
	}

	private DPadArrows() {
		ctx = (Context) MainActivity.get();

		arrows = new LinkedList<>();
		layout = new ConstraintLayout(ctx);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		final ArrowView arrow_l = new ArrowView(ctx, Dir.LEFT);
		final ArrowView arrow_r = new ArrowView(ctx, Dir.RIGHT);
		final ArrowView arrow_u = new ArrowView(ctx, Dir.UP);
		final ArrowView arrow_d = new ArrowView(ctx, Dir.DOWN);

		arrows.add(arrow_l);
		arrows.add(arrow_r);
		arrows.add(arrow_u);
		arrows.add(arrow_d);

		for (final ArrowView av: arrows) {
			layout.addView(av);
		}

		// hidden by default
		hide();
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	public void setVisibility(final int vis) {
		for (final ArrowView av: arrows) {
			av.setVisibility(vis);
		}
	}

	public boolean isVisible() {
		for (final ArrowView av: arrows) {
			if (av.getVisibility() != View.VISIBLE) {
				return false;
			}
		}

		return true;
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

	private int getX() {
		return (int) arrows.get(0).getX();
	}

	private int getY() {
		return (int) arrows.get(2).getY();
	}

	public Pair<Integer, Integer> getPosition() {
		return new Pair<Integer, Integer>(getX(), getY());
	}

	private int getWidth() {
		final ArrowView aRight = arrows.get(1);
		final int absX = ((int) aRight.getX()) + aRight.getWidth();

		return absX - getX();
	}

	private int getHeight() {
		final ArrowView aDown = arrows.get(3);
		final int absY = ((int) aDown.getY()) + aDown.getHeight();

		return absY - getY();
	}

	public Pair<Integer, Integer> getSize() {
		return new Pair<Integer, Integer>(getWidth(), getHeight());
	}

	public void onRefreshView() {
		final Point size = new Point();
		final Display disp = MainActivity.get().getWindowManager().getDefaultDisplay();
		disp.getSize(size);

		final PreferencesActivity prefs = PreferencesActivity.get();
		final int x = prefs.getInt("dpad_offset_x", 100);
		final int y = size.y - prefs.getInt("dpad_offset_y", 300);

		setPosition(x, y);

		if (isVisible()) {
			DebugLog.debug("DPadArrows: pos (" + getX() + "," + getY() + "), size ("
				+ getWidth() + "," + getHeight() + ")");
			DebugLog.debug("DPadArrows: left arrow: x (" + arrows.get(0).getX() + "), width ("
				+ arrows.get(0).getWidth() + ")");
			DebugLog.debug("DPadArrows: down arrow: y (" + arrows.get(3).getY() + "), height ("
				+ arrows.get(3).getHeight() + ")");
		}
	}


	private static class ArrowView extends ImageView {

		private final Dir dir;
		private final int id;
		private final int id_active;

		private boolean keyDown = false;


		public ArrowView(final Context ctx, final Dir dir) {
			super(ctx);

			this.dir = dir;

			switch (dir) {
				case LEFT:
					id = R.drawable.dpad_arrow_left;
					id_active = R.drawable.dpad_arrow_left_active;
					break;
				case RIGHT:
					id = R.drawable.dpad_arrow_right;
					id_active = R.drawable.dpad_arrow_right_active;
					break;
				case UP:
					id = R.drawable.dpad_arrow_up;
					id_active = R.drawable.dpad_arrow_up_active;
					break;
				default:
					id = R.drawable.dpad_arrow_down;
					id_active = R.drawable.dpad_arrow_down_active;
					break;
			}

			setBackgroundColor(android.graphics.Color.TRANSPARENT);
			setImageResource(id);

			setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(final View view, final MotionEvent event) {
					final int action = event.getAction();

					Integer newAction = null;
					Integer keyCode = null;

					if (action == MotionEvent.ACTION_DOWN && !keyDown) {
						setImageResource(id_active);
						newAction = KeyEvent.ACTION_DOWN;
						keyDown = true;
					} else if (action == MotionEvent.ACTION_UP && keyDown) {
						setImageResource(id);
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
