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

import java.util.Arrays;

import javax.swing.JOptionPane;

/**
 * <b>Bare Bones Browser Launch for Java</b><br>
 * Utility class to open a web page from a Swing application in the user's
 * default browser.<br>
 * Supports: Mac OS X, GNU/Linux, Unix, Windows XP/Vista/7<br>
 * Example Usage:<code><br> &nbsp; &nbsp;
 *    String url = "https://www.google.com/";<br> &nbsp; &nbsp;
 *    BareBonesBrowserLaunch.openURL(url);<br></code> Latest Version: <a
 * href="http://www.centerkey.com/java/browser/"
 * >www.centerkey.com/java/browser</a><br>
 * Author: Dem Pilafian<br>
 * Public Domain Software -- Free to Use as You Like
 *
 * @version 3.0, February 7, 2010
 */
public class BareBonesBrowserLaunch {
	private static final String[] browsers = { "google-chrome", "chromium-browser", "firefox", "opera",
			"konqueror", "epiphany", "seamonkey", "galeon", "kazehakase",
			"mozilla" };
	private static final String errMsg = "Error attempting to launch web browser:\n ";

	/**
	 * Opens the specified web page in the user's default browser
	 *
	 * @param url
	 *            A web address (URL) of a web page (ex:
	 *            "https://www.google.com/")
	 */
	public static void openURL(String url) {
		try { // attempt to use Desktop library from JDK 1.6+ (even if on 1.5)
			Class<?> d = Class.forName("java.awt.Desktop");
			d.getDeclaredMethod("browse", new Class[] { java.net.URI.class })
					.invoke(d.getDeclaredMethod("getDesktop").invoke(null),
							new Object[] { java.net.URI.create(url) });
			// above code mimics:
			// java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		} catch (Exception ignore) { // library not available or failed
			String osName = System.getProperty("os.name");
			try {
				if (osName.startsWith("Mac OS")) {
					Class.forName("com.apple.eio.FileManager")
							.getDeclaredMethod("openURL",
									new Class[] { String.class }).invoke(null,
									new Object[] { url });
				} else if (osName.startsWith("Windows")) {
					Runtime.getRuntime().exec(
							new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
				} else { // assume Unix or Linux
					boolean found = false;
					for (String browser : browsers) {
						if (!found) {
							found = Runtime.getRuntime().exec(
									new String[] { "which", browser })
									.waitFor() == 0;
							if (found) {
								Runtime.getRuntime().exec(
										new String[] { browser, url });
							}
						}
					}
					if (!found) {
						throw new Exception(Arrays.toString(browsers));
					}
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(j2DClient.get().getMainFrame(), errMsg + url);
			}
		}
	}
}
