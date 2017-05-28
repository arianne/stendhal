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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A component for drawing the various color bars.
 */
public class StatusDisplayBar extends JComponent {
	/** Preferred height of the bar. */
	private static final int PREFERRED_HEIGHT = 10;
	/** Value model. */
	private final ScalingModel model;
	/** Default bar color. */
	private Color color = Color.WHITE;
	/** Painter, or <code>null</code> if a plain color bar is used. */
	private BarPainter painter;
	/**
	 * Image of a full color bar, or <code>null</code> if a plain color bar
	 * is used.
	 */
	private BufferedImage barImage;

	/**
	 * Create a StatusDisplayBar.
	 *
	 * @param model Scaling model. representation corresponds to the length
	 *	of the color bar. The StatusDisplayBar will take care of the maximum
	 *	representation value
	 */
	public StatusDisplayBar(final ScalingModel model) {
		this.model = model;
		setForeground(Color.BLACK);
		setBackground(Color.BLACK);
		model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				valueChanged();
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Insets insets = getInsets();
				int barWidth = getWidth() - insets.left - insets.right - 2;
				if (painter != null && barWidth > 0) {
					int barHeight = getHeight() - insets.top - insets.bottom;
					if (barHeight > 0) {
						barImage = getGraphicsConfiguration().createCompatibleImage(barWidth, barHeight);
						Graphics2D g = barImage.createGraphics();
						painter.paint(g, barWidth, barHeight);
						g.dispose();
					}
					// Otherwise we can just keep the old image. It does not get
					// painted anyway
				}
				model.setMaxRepresentation(barWidth);
			}
		});
		setPreferredSize(new Dimension(2, PREFERRED_HEIGHT));
		setMinimumSize(getPreferredSize());
	}

	/**
	 * Return the value scaling model in use.
	 *
	 * @return model
	 */
	public ScalingModel getModel() {
		return model;
	}

	/**
	 * Set the color of the bar.
	 *
	 * @param color new color
	 */
	public void setBarColor(Color color) {
		this.color = color;
	}

	/**
	 * Set painter for fancy colored bars.
	 *
	 * @param painter painter for coloring the template image
	 */
	protected void setPainter(BarPainter painter) {
		this.painter = painter;
	}

	/**
	 * Called when the model value changes.
	 */
	protected void valueChanged() {
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Insets insets = getInsets();
		int barHeight = getHeight() - insets.top - insets.bottom - 2;
		// Paint frame
		g.setColor(getForeground());
		g.drawRect(insets.left, insets.top, getWidth() - insets.left - insets.right - 1,
				getHeight() - insets.top - insets.bottom - 1);
		// Paint what is not covered by the colored bar
		g.setColor(getBackground());
		g.fillRect(insets.left + 1, insets.top + 1, getWidth() - insets.left - insets.right - 2,
				barHeight);

		if (barImage != null) {
			Graphics clipped = g.create(insets.left + 1, insets.top + 1, model.getRepresentation(), barHeight);
			clipped.drawImage(barImage, 0, 0, null);
			clipped.dispose();
		} else {
			g.setColor(color);
			g.fillRect(insets.left + 1, insets.top + 1, model.getRepresentation(), barHeight);
		}
	}

	/**
	 * Interface for bars that need more complicated drawing than a simple color
	 * bar.
	 */
	public interface BarPainter {
		/**
		 * Fill an area corresponding to a <em>full</em> bar.
		 *
		 * @param g graphics
		 * @param width width of the area
		 * @param height height of the area
		 */
		void paint(Graphics2D g, int width, int height);
	}
}
