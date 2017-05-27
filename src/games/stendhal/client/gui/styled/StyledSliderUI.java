/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.styled;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * A SliderUI implementation using {@link Style} settings.
 * <br>
 * <b>IMPORTANT:</b> Only drawing horizontal sliders is implemented, and
 * trying to use this for vertical sliders will most likely fail spectacularly.
 */
public class StyledSliderUI extends BasicSliderUI {

	private static final int TRACK_HEIGHT = 6;
	private static final int SLIDER_WIDTH = 8;
	private final Style style;

	// Required by UIManager
	public static ComponentUI createUI(JComponent slider) {
		// SliderUI can not be shared
		if (slider instanceof JSlider) {
			return new StyledSliderUI(StyleUtil.getStyle(), (JSlider) slider);
		} else {
			return null;
		}
	}

	public StyledSliderUI(Style style, JSlider slider) {
		super(slider);
		this.style = style;
	}

	@Override
	public void paintTrack(Graphics g) {
		int x = trackRect.x;
		int y = trackRect.y + (trackRect.height - TRACK_HEIGHT) / 2;

		// Right side of the slider
		int adjust = xPositionForValue(slider.getValue());
		/*
		 * If the user is adjusting the slider, getValue() is not accurate
		 * enough.
		 */
		if (isDragging()) {
			/*
			 * Needs to be done like this, because getMousePosition() does
			 * not necessarily return a non null value in the next call.
			 */
			final Point point = slider.getMousePosition();
			if (point != null) {
				adjust = point.x;
			}
		}
		// Center of the slider
		adjust -= thumbRect.width / 2;
		adjust = Math.max(0, adjust);

		if (slider.isEnabled()) {
			g.setColor(slider.getForeground());
		} else {
			g.setColor(slider.getBackground());
		}
		g.fillRect(x + adjust, y, trackRect.width - adjust, TRACK_HEIGHT);
		// Who knows why painBorder has Component as the first parameter?
		// Anyway, passing it null does not seem to crash it.
		style.getBorderDown().paintBorder(null, g, x, y, trackRect.width, TRACK_HEIGHT);
	}

	@Override
	public void paintThumb(Graphics g) {
		Rectangle r = getThumbShape();
		StyleUtil.fillBackground(style, g, r.x, r.y, r.width, r.height);
		style.getBorder().paintBorder(null, g, r.x, r.y, r.width, r.height);
	}

	@Override
	public void paintFocus(Graphics g) {
		g.setColor(style.getHighLightColor());
		Rectangle r = getThumbShape();
		g.drawRect(r.x - 1, r.y, r.width + 1, r.height - 1);
	}

	private Rectangle getThumbShape() {
		int x = thumbRect.x - (SLIDER_WIDTH - thumbRect.width) / 2;

		return new Rectangle(x, thumbRect.y, SLIDER_WIDTH, thumbRect.height);
	}

	@Override
	public void installUI(JComponent slider) {
		super.installUI(slider);
		// A pixmap style will not tile right if we try to draw the background
		// here
		slider.setOpaque(false);
		slider.setForeground(style.getForeground());
		slider.setBackground(style.getShadowColor());
	}
}
