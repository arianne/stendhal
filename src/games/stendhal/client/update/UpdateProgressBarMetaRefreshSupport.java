/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

/**
 * adds support &lt;meta http-equiv="refresh" content="30; url=https://arianne-project.org/"&gt;
 *
 * @author hendrik
 */
class UpdateProgressBarMetaRefreshSupport implements PropertyChangeListener, Runnable {
	private JEditorPane browser;
	private String url;
	private int delay;

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		browser = (JEditorPane) event.getSource();
		String html = browser.getText();
		if (parseHtml(html)) {
			Thread thread = new Thread(this, "UpdateProgressBar");
			thread.setDaemon(true);
			thread.start();
		}
	}

	/**
	 * parses the html to look for meta-refresh.
	 *
	 * @param html html to parse
	 * @return true, if a refresh was found
	 */
	private boolean parseHtml(String html) {

		// <meta http-equiv="refresh" content="30; url=https://arianne-project.org/">
		Pattern p = Pattern.compile("<meta http-equiv=\"refresh\" content=\"([^;]*); url=([\"]*)\">");
		Matcher m = p.matcher(html);
		if (!m.find()) {
			p = Pattern.compile("<meta content=\"([^;]*); url=([^\"]*)\" http-equiv=\"refresh\">");
			m = p.matcher(html);
		}
		if (!m.find()) {
			return false;
		}

		delay = Integer.parseInt(m.group(1));
		url = m.group(2);

		return true;
	}

	@Override
	public void run() {
		// wait the delay
		try {
			Thread.sleep(delay * 1000);
		} catch (InterruptedException e1) {
			// ignore
		}

		// load the page
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					browser.setPage(url);
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		});
	}

}
