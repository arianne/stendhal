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
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.gui.LinearScalingModel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Health bar for RPEntities.
 */
class HealthBar implements ChangeListener {
	private final int width, height;
	private BufferedImage image;
	private final LinearScalingModel model = new LinearScalingModel();
	private volatile boolean needsRedraw = true;
	
	/**
	 * Create a health bar.
	 * 
	 * @param width width of the bar
	 * @param height height of the bar
	 */
	HealthBar(int width, int height) {
		this.width = width;
		this.height = height;
		model.setMaxRepresentation(width - 2);
		model.addChangeListener(this);
	}
	
	/**
	 * Get the bar height.
	 * 
	 * @return height
	 */
	int getHeight() {
		return height;
	}

	/**
	 * Get the bar width.
	 * 
	 * @return width
	 */
	int getWidth() {
		return width;
	}
	
	/**
	 * Set the HP ratio.
	 * 
	 * @param hpRatio new HP ratio
	 */
	void setHPRatio(double hpRatio) {
		model.setValue(hpRatio);
	}
	
	/**
	 * Draw the health bar.
	 * 
	 * @param g graphics
	 * @param x x coordinate of the top left corner
	 * @param y y coordinate of the top left corner
	 */
	void draw(Graphics g, int x, int y) {
		if (image == null) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
		if (needsRedraw) {
			synchronized (this) {
				if (needsRedraw) {
					Graphics gr = image.createGraphics();
					drawImage(gr);
					gr.dispose();
					needsRedraw = false;
				}
			}
		}
		g.drawImage(image, x, y, null);
	}
	
	/**
	 * Draw the health bar image.
	 * 
	 * @param g graphics
	 */
	private void drawImage(Graphics g) {
		g.setColor(Color.GRAY);
		g.fillRect(1, 1, width, height);

		g.setColor(determineColor());
		g.fillRect(1, 1, model.getRepresentation(), height - 2);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
	}
	
	/**
	 * Determine the health bar color.
	 * 
	 * @return bar color
	 */
	private Color determineColor() {
		float hpRatio = (float) model.getValue();

		float r = Math.min((1.0f - hpRatio) * 2.0f, 1.0f);
		float g = Math.min(hpRatio * 2.0f, 1.0f);
		return new Color(r, g, 0.0f);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		needsRedraw = true;
	}
}
