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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import marauroa.common.Pair;
import org.arianne.stendhal.client.DebugLog;
import org.arianne.stendhal.client.MainActivity;
import org.arianne.stendhal.client.PreferencesActivity;
import org.arianne.stendhal.client.R;


public class DPadJoy extends DPad {

	private static DPadJoy instance;

	private static Context ctx;

	private static ConstraintLayout layout;

	private static OuterButton outerButton;
	private static int sensitivity;


	public static DPadJoy get() {
		if (instance == null) {
			instance = new DPadJoy();
		}

		return instance;
	}

	private DPadJoy() {
		ctx = (Context) MainActivity.get();
		layout = new ConstraintLayout(ctx);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		outerButton = new OuterButton(ctx);
		outerButton.addToLayout();

		setSensitivity(PreferencesActivity.getInt("joypad_sensitivity", 25));

		// hidden by default
		hide();
	}

	public ConstraintLayout getLayout() {
		return layout;
	}

	public void setVisibility(final int vis) {
		outerButton.setVisibility(vis);
	}

	public boolean isVisible() {
		return outerButton.isVisible();
	}

	public void setPosition(final int x, final int y) {
		outerButton.setPosition(x, y);
	}

	private int getX() {
		return (int) outerButton.getX();
	}

	private int getY() {
		return (int) outerButton.getY();
	}

	public Pair<Integer, Integer> getPosition() {
		return new Pair<Integer, Integer>(getX(), getY());
	}

	private int getWidth() {
		return outerButton.getWidth();
	}

	private int getHeight() {
		return outerButton.getHeight();
	}

	public Pair<Integer, Integer> getSize() {
		return new Pair<Integer, Integer>(getWidth(), getHeight());
	}

	public void setSensitivity(final int value) {
		sensitivity = value;
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
			DebugLog.debug("DPadJoy: pos (" + getX() + "," + getY() + "), size ("
				+ getWidth() + "," + getHeight() + ")");
		}
	}


	private static class OuterButton extends JoyPadButton {

		private final InnerButton innerButton;

		private DPad.Dir currentDir = DPad.Dir.NONE;
		private boolean keyDown = false;
		private final List<Integer> pressedKeys;


		public OuterButton(final Context ctx) {
			super(ctx);

			pressedKeys = new ArrayList<>();

			setBackgroundColor(android.graphics.Color.TRANSPARENT);
			setImageResource(R.drawable.dpad_circle_outer);

			innerButton = new InnerButton(ctx);
			innerButton.setBackgroundColor(android.graphics.Color.TRANSPARENT);
			innerButton.setImageResource(R.drawable.dpad_circle_inner);

			setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(final View view, final MotionEvent event) {
					final int action = event.getAction();

					DebugLog.debug("joypad action: " + action);

					if (action == MotionEvent.ACTION_DOWN && !keyDown) {
						keyDown = true;
						innerButton.setImageResource(R.drawable.dpad_circle_inner_active);
						innerButton.setRelativeX(event.getX());
						innerButton.setRelativeY(event.getY());

						final DPad.Dir newDir = getTouchDirection();
						if (newDir != currentDir) {
							currentDir = newDir;
							onDirectionChange(KeyEvent.ACTION_DOWN);
						}
					} else if (action == MotionEvent.ACTION_UP && keyDown) {
						keyDown = false;
						innerButton.setImageResource(R.drawable.dpad_circle_inner);
						innerButton.centerOn((JoyPadButton) view);

						clearPresses();
						currentDir = DPad.Dir.NONE;
					} else if (action == MotionEvent.ACTION_MOVE && keyDown) {
						innerButton.setRelativeX(event.getX());
						innerButton.setRelativeY(event.getY());

						final DPad.Dir newDir = getTouchDirection();
						if (newDir != currentDir) {
							currentDir = newDir;
							onDirectionChange(KeyEvent.ACTION_DOWN);
						}
					}

					return true;
				}
			});
		}

		public void addToLayout() {
			layout.addView(this);
			layout.addView(innerButton);

			// FIXME: inner button not centering at startup
			innerButton.centerOn(this);
		}

		@Override
		public void setVisibility(final int vis) {
			innerButton.setVisibility(vis);
			super.setVisibility(vis);
		}

		public boolean isVisible() {
			return getVisibility() == View.VISIBLE
				&& innerButton.getVisibility() == View.VISIBLE;
		}

		public void setPosition(final int x, final int y) {
			setX(x);
			setY(y);

			innerButton.centerOn(this);
		}

		/**
		 * Determines pressed direction based on inner button position
		 * relative to outer button position.
		 */
		private DPad.Dir getTouchDirection() {
			final float outerX = getCenterX();
			final float outerY = getCenterY();
			final float innerX = innerButton.getCenterX();
			final float innerY = innerButton.getCenterY();
			final float offsetX = outerX - innerX;
			final float offsetY = outerY - innerY;

			final int absX = (int) Math.abs(offsetX);
			final int absY = (int) Math.abs(offsetY);

			if (absX < sensitivity && absY < sensitivity) {
				return DPad.Dir.NONE;
			}

			// horizontal directions take precedence if they are the same as vertical
			if (absX >= absY) {
				if (innerX < outerX) {
					return DPad.Dir.LEFT;
				} else if (innerX > outerX) {
					return DPad.Dir.RIGHT;
				}
			} else {
				if (innerY < outerY) {
					return DPad.Dir.UP;
				} else if (innerY > outerY) {
					return DPad.Dir.DOWN;
				}
			}

			return DPad.Dir.NONE;
		}

		private void clearPresses() {
			for (final int key: pressedKeys) {
				MainActivity.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, key));
			}
		}

		/**
		 * Sends a direction key press event to client.
		 */
		private void onDirectionChange(final int action) {
			Integer keyCode = null;

			switch (currentDir) {
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

			if (keyCode != null) {
				// clear pressed keys before dispatching new key press
				clearPresses();

				if (action == KeyEvent.ACTION_DOWN) {
					MainActivity.get().dispatchKeyEvent(new KeyEvent(action, keyCode));
					pressedKeys.add(keyCode);
				}
			}
		}
	}


	private static class InnerButton extends JoyPadButton {

		public InnerButton(final Context ctx) {
			super(ctx);
		}

		/**
		 * Sets X coordinate relative to OuterButton.
		 */
		public void setRelativeX(float x) {
			x = x + outerButton.getX();

			final float maxL = outerButton.getX();
			final float maxR = maxL + outerButton.getWidth();

			// don't move outside bounds of outer button
			if (x >= maxL && x <= maxR) {
				// use circle center
				setX(x - (getWidth() / 2));
			}
		}

		/**
		 * Sets Y coordinate relative to OuterButton.
		 */
		public void setRelativeY(float y) {
			y = y + outerButton.getY();

			final float maxT = outerButton.getY();
			final float maxB = maxT + outerButton.getHeight();

			// don't move outside bounds of outer button
			if (y >= maxT && y <= maxB) {
				// use circle center
				setY(y - (getHeight() / 2));
			}
		}
	}
}
