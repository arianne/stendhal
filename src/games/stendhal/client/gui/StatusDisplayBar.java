/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A component for drawing the various color bars.
 */
public class StatusDisplayBar extends JComponent {
	/** Preferred height of the bar. */
	private final int PREFERRED_HEIGHT = 10;
	/** Value model. */
	private final ScalingModel model;
	/** Default bar color. */
	private Color color = Color.WHITE;
	
	/**
	 * Create a StatusDisplayBar.
	 * 
	 * @param model Scaling model. representation corresponds to the length
	 *	of the color bar. The StatusDisplayBar will take care of the maximum
	 *	representation value
	 */
	public StatusDisplayBar(final ScalingModel model) {
		this.model = model;
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Insets insets = getInsets();
				int barWidth = getWidth() - insets.left - insets.right - 2;
				model.setMaxRepresentation(barWidth);
			}
		});
		this.setPreferredSize(new Dimension(2, PREFERRED_HEIGHT));
	}
	
	/**
	 * Set the color of the bar.
	 * 
	 * @param color new color
	 */
	public void setBarColor(Color color) {
		this.color = color;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Insets insets = getInsets();
		// Paint black what is not covered by the colored bar
		g.setColor(Color.BLACK);
		g.fillRect(insets.left, insets.top, getWidth() - insets.left - insets.right,
				getHeight() - insets.top - insets.bottom);
		
		if (color != null) {
			g.setColor(color);
			g.fillRect(insets.left + 1, insets.top + 1, model.getRepresentation(), getHeight() - insets.top - insets.bottom - 2);
		}
	}
}
