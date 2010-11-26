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
package data.sprites.monsters;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JComponent;

/**
 * Component for viewing an Image. Simplified version of Symantec ImageViewer
 * displays an Image. Does not resize the image, though may crop it. Thus always
 * maintains original magnification and aspect ratio. Similar to IconImage.
 * 
 * @author Roedy Green
 */
public class ImageViewerSwing extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8190301649166149694L;

	/**
	 * default Constructor.
	 */
	public ImageViewerSwing() {
		setImage(Toolkit.getDefaultToolkit().getImage("data/sprites/ideas/admin.png"));
	}

	/**
	 * Constructor with Image.
	 * 
	 * @param image
	 *            the Image to be displayed . See the Java glossary under Image
	 *            for ways to create an Image from a file.
	 */
	public ImageViewerSwing(final Image image) {
		this();
		setImage(image);
	}

	/**
	 * Set or change the current Image to display. setImage does a MediaTracker
	 * to ensure the Image is loaded. You don't have to. If you don't plan to
	 * use the old image again you should do a getImage().flush();
	 * 
	 * @param image
	 *            the new Image to be displayed. If the image jpg may have
	 *            recently changed, don't use getImage to create it, use
	 *            URL.openConnection() URLConnection.setUseCaches( false )
	 *            Connection.getContent Component.createImage
	 * 
	 */
	public void setImage(final Image image) {
		// even if Image object is same, we use it since it may have changed
		// state.

		this.image = image;

		if (image != null) {
			MediaTracker tracker;
			try {
				// wait until image is fully loaded.
				// and so that paint will be instantaneous, rather than gradual
				// as
				// the image arrives.
				// MediaTracker notifies of progress via our
				// Component.ImageObsever interface
				tracker = new MediaTracker(this);
				tracker.addImage(image, 0);
				tracker.waitForID(0);
			} catch (final InterruptedException e) {
				//load completed
			}
			setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		}

		revalidate();
		// image is now ready, let's paint it
		repaint();
	}

	/**
	 * Get the Image currently being displayed.
	 * 
	 * @return the Image currently displayed or null if no Image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * Paints this component using the given graphics context.
	 * 
	 * @param g
	 *            Graphics context where to paint, e.g. to screen, printer, RAM.
	 */
	@Override
	public void paintComponent(final Graphics g) {
		// get size of box we have to draw in
		final Dimension dim = getSize();
		if (image != null) {
			/*
			 * center Image in box, normally should exactly fill the box. If we
			 * overflow, no problem, drawImage will clip.
			 */
			final int imageWidth = image.getWidth(this);
			final int imageHeight = image.getHeight(this);

			// this does not complete the job, just starts it.
			// We are notified of progress through our Component ImageObserver
			// interface.
			g.drawImage(image, (dim.width - imageWidth) / 2, (dim.height - imageHeight) / 2, imageWidth, imageHeight,
					this);

		} else {
			/* we have no Image, clear the box */
			g.setColor(getBackground());
			g.clearRect(0, 0, dim.width, dim.height);
		}
	}

	/**
	 * Preferred Layout size.
	 * 
	 * @return the recommended dimensions to display the Image.
	 */
	@Override
	public Dimension getPreferredSize() {
		if (image != null) {
			// should just fit the Image
			return (new Dimension(image.getWidth(this), image.getHeight(this)));
		} else {
			// empty square as a place holder
			return new Dimension(100, 100);
		}
	}

	/**
	 * Minimum layout size.
	 * 
	 * @return he minimum dimensions to properly display the Image
	 */
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * Image that this viewer is currently displaying.
	 */
	private Image image;

}
