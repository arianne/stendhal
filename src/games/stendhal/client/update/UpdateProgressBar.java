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
package games.stendhal.client.update;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.security.AccessControlException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * A progress bar for the download progress.
 */
public class UpdateProgressBar extends JFrame implements
		HttpClient.ProgressListener {
	private static final long serialVersionUID = -1607102841664745919L;

	private int max = 100;

	/** the version the update is based on, <code>null</code> for a initial download */
	private String fromVersion = null;

	/** the version the download is leading to */
	private String toVersion = null;

	private int sizeOfLastFiles;

	private JPanel contentPane;

	private JProgressBar progressBar;

	private JEditorPane browser;


	/**
	 * Creates update progress bar.
	 * 
	 * @param max max file size
	 * @param fromVersion the version the update is based on, may be <code>null</code>
	 * @param toVersion the version the download leads to
	 */
	public UpdateProgressBar(final int max, final String fromVersion, final String toVersion) {
		super("Downloading...");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new UpdateProgressBarWindowListener());

		this.max = max;
		this.fromVersion = fromVersion;
		this.toVersion = toVersion;

		try {
			final URL url = this.getClass().getClassLoader().getResource(
					ClientGameConfiguration.get("GAME_ICON"));
			setIconImage(new ImageIcon(url).getImage());
		} catch (final Exception e) {
			// in case that resource is not available
		}

		initializeComponents();

		this.pack();
		try {
			this.setAlwaysOnTop(true);
		} catch (final AccessControlException e) {
			// ignore it
		}
	}

	private void initializeComponents() {
		contentPane = (JPanel) this.getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		contentPane.add(new JLabel("Downloading..."));
		contentPane.add(Box.createVerticalStrut(5));

		progressBar = new JProgressBar(0, max);
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		contentPane.add(progressBar);

		if (toVersion != null) {
			// Set up page display.
			browser = new JEditorPane();
			browser.setContentType("text/html");
			browser.setEditable(false);
			Dimension dim = new Dimension(600, 440);
			setSize(dim);
			browser.addHyperlinkListener(new UpdateProgressBarHyperLinkListener());
			// TODO: scrollbars if necessary
			// TODO: 640x440 window size?
			// TODO: load page async?
			try {
				browser.setPage("http://arianne.sourceforge.net/stendhal/greeting/" + fromVersion + "/" + toVersion + ".html");
				contentPane.add(browser);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

	public void onDownloading(final int downloadedBytes) {
		progressBar.setValue(sizeOfLastFiles + downloadedBytes);
	}

	public void onDownloadCompleted(final int byteCounter) {
		sizeOfLastFiles = sizeOfLastFiles + byteCounter;
		progressBar.setValue(sizeOfLastFiles);
	}

	public static void main(String[] args) {
		UpdateProgressBar updateProgressBar = new UpdateProgressBar(100, null, "0.88");
		updateProgressBar.onDownloading(50);
		updateProgressBar.setVisible(true);
	}
}
