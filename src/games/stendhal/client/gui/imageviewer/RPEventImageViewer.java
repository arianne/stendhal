package games.stendhal.client.gui.imageviewer;

import java.net.URL;

import marauroa.common.game.RPEvent;

import org.apache.log4j.Logger;

/**
 * Opens an image in a styled internal frame with (possibly) some alternate
 * text.
 * 
 * @author timothyb89
 */
public class RPEventImageViewer {

	private String path;
	private String alt;
	private String title;
	private static final Logger logger = Logger.getLogger(RPEventImageViewer.class);

	private RPEventImageViewer(final RPEvent e) {
		if (e.has("path")) {
			path = e.get("path");
		}
		if (e.has("alt")) {
			alt = e.get("alt");
		}
		if (e.has("title")) {
			title = e.get("title");
		}
		view();
	}

	public static void viewImage(final RPEvent e) {
		new RPEventImageViewer(e);
	}

	public URL genURL() {
		try {
			URL url = null;
			if (path.startsWith("http://")) {
				url = new URL(path);
			} else {
				url = getClass().getResource(path);
			}
			return url;
		} catch (final Exception e) {
			logger.error(null, e);
		}
		return null;
	}

	public void view() {
		final ViewPanel vp = new ImageViewPanel(genURL(), alt);
		new ImageViewWindow(title, vp);
	}
}
