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

import androidx.constraintlayout.widget.ConstraintLayout;

import marauroa.common.Pair;


public interface DPad {

	public static enum Dir {
		LEFT,
		RIGHT,
		UP,
		DOWN;
	}

	public abstract ConstraintLayout getLayout();

	public abstract void setVisibility(final int vis);

	public abstract void setPosition(final int x, final int y);

	public abstract Pair<Integer, Integer> getPosition();

	public abstract Pair<Integer, Integer> getSize();

	public abstract void onRefreshView();
}
