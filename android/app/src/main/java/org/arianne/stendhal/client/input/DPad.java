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

import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;

import marauroa.common.Pair;


public abstract class DPad {

	public static enum Dir {
		NONE,
		LEFT,
		RIGHT,
		UP,
		DOWN;
	}

	private static DPad currentPad = null;


	public abstract ConstraintLayout getLayout();

	public abstract void setVisibility(final int vis);

	public abstract boolean isVisible();

	public void show() {
		setVisibility(View.VISIBLE);
	}

	public void hide() {
		setVisibility(View.GONE);
	}

	public abstract void setPosition(final int x, final int y);

	public abstract Pair<Integer, Integer> getPosition();

	public abstract Pair<Integer, Integer> getSize();

	public abstract void onRefreshView();

	public static void setCurrentPad(final DPad pad) {
		currentPad = pad;
	}

	public static DPad getCurrentPad() {
		return currentPad;
	}
}
