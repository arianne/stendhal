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

import java.net.URL;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.DataLoader;
import marauroa.common.game.RPEvent;

/**
 * Opens an image in a styled internal frame with (possibly) some alternate
 * text.
 *
 * @author timothyb89
 */
public final class RPEventImageViewer {

	private String path;
	private String caption;
	private String title;
	private static final Logger logger = Logger.getLogger(RPEventImageViewer.class);

	private RPEventImageViewer(final RPEvent e) {
		if (e.has("path")) {
			path = e.get("path");
		}
		if (e.has("caption")) {
			caption = e.get("caption");
		}
		if (e.has("title")) {
			title = e.get("title");
		}
		view();
	}

	public static void viewImage(final RPEvent e) {
		new RPEventImageViewer(e);
	}

	private URL genURL() {
		try {
			URL url = null;
			if (path.startsWith("http://") || path.startsWith("https://")) {
				url = new URL(path);
			} else {
				url = DataLoader.getResource(path);
			}
			return url;
		} catch (final Exception e) {
			logger.error(null, e);
		}
		return null;
	}

	private void view() {
		URL url = genURL();
		if (url != null) {
			final ViewPanel vp = new ImageViewPanel(url, caption);
			new ImageViewWindow(title, vp);
		} else {
			logger.error("No such image: " + path);
		}
	}
}
