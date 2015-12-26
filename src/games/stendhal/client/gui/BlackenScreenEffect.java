/***************************************************************************
 *                 (C) Copyright 2003-2014 - Faiumoni e.V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * An effect that turns the screen black, fading in specified duration.
 */
public class BlackenScreenEffect extends EffectLayer {
	public BlackenScreenEffect(int duration) {
		super(duration);
	}

	@Override
	public boolean isExpired() {
		// Never expire. Lasts until the next zone change.
		return false;
	}

	@Override
	public void drawScreen(Graphics g, int x, int y, int w, int h) {
		Color c;
		long time = System.currentTimeMillis();
		if (time > timestamp + duration) {
			c = Color.BLACK;
		} else {
			int traslucency = (int) ((timestamp  + duration - time) * 255 / duration);
			c = new Color(0, 0, 0, alpha(255 - traslucency));
		}
		g.setColor(c);
		g.fillRect(x, y, w, h);
	}
}
