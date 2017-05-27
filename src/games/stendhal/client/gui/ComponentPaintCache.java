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
package games.stendhal.client.gui;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;

/**
 * Cached painting for components that need it. Components using this need
 * to implement the <code>Cacheable</code> interface, and override the paint
 * method to something like:
 * <pre>
 * public void paint(Graphics g) {
 *	cache.paintComponent(g);
 *	paintChildren(g);
 * } </pre>
 * Also, the components need to call <code>cache.invalidate()</code> should
 * the component need a proper redraw. Size changes are automatically handled.
 */
public class ComponentPaintCache {
	/** The cached component */
	private Cacheable component;
	/** Cached width of the component */
	private int oldWidth;
	/** Cached height of the component */
	private int oldHeight;
	/** Cached image of the component and its borders */
	private Image cachedImage;
	/** Should the child components be cached as well */
	private boolean paintChildren;

	/**
	 * Create a new paint cache.
	 *
	 * @param component the cached component
	 */
	public ComponentPaintCache(Cacheable component) {
		this.component = component;
	}

	/**
	 * Paint the cached component.
	 *
	 * @param g graphics
	 */
	public void paintComponent(Graphics g) {
		/*
		 * A Component listener would be cleaner way to detect resizes.
		 * Unfortunately the component gets drawn before the listener gets
		 * notified.
		 */
		int width = component.getWidth();
		int height = component.getHeight();
		if (cachedImage == null || (oldWidth != width) || (oldHeight != height)) {
			oldWidth = width;
			oldHeight = height;

			// Create a new image, and draw the components onto it
			cachedImage = component.getGraphicsConfiguration().createCompatibleImage(width, height, Transparency.OPAQUE);
			Graphics imageGraphics = cachedImage.getGraphics();
			imageGraphics.setClip(0, 0, width, height);
			component.paintComponent(imageGraphics);
			component.paintBorder(imageGraphics);
			if (paintChildren) {
				/*
				 * JComponent.paint produces garbage, and paintComponent &
				 * paintBorder need to be exposed for those that can't include
				 * the children anyway. Otherwise it would be easiest to use
				 * SwingUtilities.paintComponent and accept JComponent in the
				 * constructor, but now we need the interface anyway.
				 */
				component.paintChildren(imageGraphics);
			}
			imageGraphics.dispose();
		}
		g.drawImage(cachedImage, 0, 0, null);
	}

	/**
	 * Set if the child components should be cached as images as well. The
	 * default is not caching. Do change it to true unless the children are
	 * static images or you take care of calling invalidate() when they change.
	 *
	 * @param paint <code>true</code> if the child components should be included
	 * 	in the cached image, <code>false</code> otherwise
	 */
	public void setPaintChildren(boolean paint) {
		paintChildren = paint;
	}

	/**
	 * Interface for components that use <code>ComponentPaintCache</code>.
	 * <code>JComponents</code> support by default all but
	 * {@link #paintComponent(Graphics)} and {@link #paintBorder(Graphics)},
	 * which are by default <code>protected</code>
	 */
	public interface Cacheable {
		/**
		 * Get the total width of the component, including borders.
		 *
		 * @return component width.
		 */
		int getWidth();
		/**
		 * Get the total height of the component, including borders.
		 * @return component height.
		 */
		int getHeight();
		/**
		 * Paint the component.
		 *
		 * @param g graphics
		 */
		void paintComponent(Graphics g);
		/**
		 * Paint the component border.
		 *
		 * @param g graphics
		 */
		void paintBorder(Graphics g);
		/**
		 * Paint everything, including the child components.
		 *
		 * @param g graphics
		 */
		void paintChildren(Graphics g);
		/**
		 * Get the component graphics configuration.
		 *
		 * @return graphics configuration
		 */
		GraphicsConfiguration getGraphicsConfiguration();
	}
}
