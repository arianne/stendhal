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

import java.awt.Graphics;
import java.awt.Transparency;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.LayerRenderer;
import games.stendhal.client.sprite.Tileset;

/**
 * A renderer for map wide effects. These effects are normally temporary and
 * they are automatically removed as soon as {@link #isExpired()} returns
 * <code>true</code>.
 *
 */
public abstract class EffectLayer extends LayerRenderer {
	/** Duration of the effect. The actual meaning is effect dependent. */
	final long duration;
	/** Time stamp of the creation of the effect. */
	final long timestamp;

	/**
	 * Create an EffectLayer for specified effect duration.
	 *
	 * @param duration effect duration in milliseconds
	 */
	public EffectLayer(int duration) {
		this.duration = duration;
		timestamp = System.currentTimeMillis();
	}

	@Override
	public void setTileset(Tileset tileset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void draw(Graphics g, int x, int y, int w, int h) {
		int s = IGameScreen.SIZE_UNIT_PIXELS;
		drawScreen(g, x * s, y * s, w * s, h* s);
	}

	/**
	 * A convenience method for drawing in screen coordinates. The parameters
	 * correspond to drawing the whole game screen. If world units are wanted,
	 * override {@link #draw(Graphics, int, int, int, int)} instead.
	 * @param g graphics
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param w screen width
	 * @param h screen height
	 */
	abstract void drawScreen(Graphics g, int x, int y, int w, int h);

	/**
	 * Check if the effect is old enough to have expired so that it should be removed.
	 *
	 * @return <code>true</code> if the effect has expired, otherwise <code>false</code>
	 */
	public boolean isExpired() {
		// Default implementation: remove after the time of duration has passed
		return System.currentTimeMillis() > timestamp + duration;
	}

	/**
	 * A convenience method for getting the opaque or fully transparent alpha
	 * values on systems that need it.
	 *
	 * @param originalAlpha original alpha value
	 * @return original alpha, or its value rounded to fully opaque or
	 * 	transparent on systems that have the translucency turned off
	 */
	int alpha(int originalAlpha) {
		if (TransparencyMode.TRANSPARENCY == Transparency.BITMASK) {
			return originalAlpha > 127 ? 255 : 0;
		}
		return originalAlpha;
	}
}
