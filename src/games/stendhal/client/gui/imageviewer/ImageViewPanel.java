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
package games.stendhal.client.gui.imageviewer;

import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

/**
 * A JPanel to be viewed from an ImageViewWindow.
 *
 * @author timothyb89
 */
class ImageViewPanel extends ViewPanel {
	private static final Logger logger = Logger.getLogger(ImageViewPanel.class);

	private static final String FONT_COLOR = "#FFFFFF";
	private static final String FONT_SIZE = "5";
	/**
	 * The image to be displayed.
	 */
	private Image image;

	private final URL url;
	private final String caption;

	ImageViewPanel(final URL url, final String caption) {
		this.url = url;
		this.caption = caption;
	}

	@Override
	public void prepareView(final Dimension maxSize) {
		initImage();
		initComponents(maxSize);
	}

	/**
	 * Loads the image. Will cause problems if the image does not exist.
	 */
	private void initImage() {
		try {
			// we load the image twice for scaling purposes (height and width).
			// maybe there's a better way?
			image = ImageIO.read(url);
		} catch (final Exception e) {
			logger.error("Failed to read image from '" + url + "'", e);
		}
	}

	/**
	 * Creates and adds components to draw the image.
	 * @param maxSize TODO
	 */
	private void initComponents(final Dimension maxSize) {
		final Dimension max = maxSize;
		int width = image.getWidth(null);
		int height = image.getHeight(null);

		if (image.getWidth(null) > max.width) {
			width = max.width - 2;
		}
		if (image.getHeight(null) > max.height) {
			height = max.height - 2;
		}

		// only display when not null
		String htmlCaption = "";
		if (caption != null) {
			htmlCaption = "<b><i><font color=\"" + FONT_COLOR + "\" size=\""
					+ FONT_SIZE + "\">" + caption + "</big></i></b><br>";
		}


		final String img = "<img width=" + width + " height=" + height + " src="
					+ url.toString() + ">";

		final String text = "<html>" + htmlCaption + img;
		final JLabel imageLabel = new JLabel(text);

		add(imageLabel);

		setVisible(true);
	}

}
