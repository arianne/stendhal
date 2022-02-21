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
//import android.view.View;
import android.widget.ImageView;


abstract class JoyPadButton extends ImageView {

	public JoyPadButton(final Context ctx) {
		super(ctx);
	}

	protected float getCenterX() {
		return getX() + (getWidth() / 2);
	}

	protected float getCenterY() {
		return getY() + (getHeight() / 2);
	}

	protected void centerOn(final JoyPadButton parent) {
		super.setX(parent.getCenterX() - (getWidth() / 2));
		super.setY(parent.getCenterY() - (getHeight() / 2));
	}
}
