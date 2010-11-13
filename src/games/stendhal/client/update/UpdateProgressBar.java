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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

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
		contentPane.add(Box.createVerticalStrut(5));

		if (toVersion != null) {
			// Set up page display.
			browser = new JEditorPane();
			browser.setContentType("text/html");
			browser.setEditable(false);
			Dimension dim = new Dimension(600, 440);
			browser.setPreferredSize(dim);
			browser.addHyperlinkListener(new UpdateProgressBarHyperLinkListener());
			
			Dimension windowSize = new Dimension(640, 480);
			setPreferredSize(windowSize);
			// TODO: load page async?
			try {
				browser.setPage("http://arianne.sourceforge.net/stendhal/greeting/" + fromVersion + "/" + toVersion + ".html");
			} catch (IOException e) {
				System.out.println(e);
			}
			// Gige the page scroll bars if it needs them
			final JScrollPane scrollPane = new JScrollPane(browser);
			contentPane.add(scrollPane);
		}
	}

	public void onDownloading(final int downloadedBytes) {
		// The updater will not be running in EDT
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(sizeOfLastFiles + downloadedBytes);
			}
		});
	}

	public void onDownloadCompleted(final int byteCounter) {
		sizeOfLastFiles = sizeOfLastFiles + byteCounter;
		// The updater will not be running in EDT
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setValue(sizeOfLastFiles);
			}
		});
	}

	/**
	 * main entrance point for testing
	 *
	 * @param args ignored
	 */
	public static void main(String[] args) {
		UpdateProgressBar updateProgressBar = new UpdateProgressBar(100, null, "0.88");
		updateProgressBar.onDownloading(50);
		updateProgressBar.setVisible(true);
	}
}
