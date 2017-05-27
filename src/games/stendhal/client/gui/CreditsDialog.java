/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.DataLoader;

/**
 * Displays a credits dialog box.
 */
class CreditsDialog extends JDialog {
	/** Logger instance. */
	private static final Logger LOGGER = Logger.getLogger(CreditsDialog.class);
	/** Scrolling panel. */
	private ScrollerPanel sp;
	/** Close button. */
	private final JButton closeButton = new JButton("Close");

	/**
	 * Creates a new credits dialog.
	 *
	 * @param owner
	 *            owner window
	 */
	CreditsDialog(final Frame owner) {
		super(owner, true);
		initGUI(owner);
		LOGGER.debug("about dialog initialized");
		eventHandling();
		LOGGER.debug("about dialog event handling ready");

		this.setTitle("Stendhal Credits");
		// required on Compiz
		this.pack();

		Dimension size = owner.getSize();
		size.width -= 50;
		size.height -= 50;
		setSize(size);
		setLocationRelativeTo(owner);
		WindowUtils.closeOnEscape(this);
		this.setVisible(true);
	}

	/**
	 * Create the GUI.
	 *
	 * @param owner parent window
	 */
	private void initGUI(final Frame owner) {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				owner.setEnabled(true);
				dispose();
			}
		});
		owner.setEnabled(false);
		Container content = this.getContentPane();
		if (content instanceof JComponent) {
			((JComponent) content).setBorder(null);
		}
		// Colors
		Color backgroundColor = Color.white;
		Color textColor = new Color(85, 85, 85);

		content.setLayout(new BorderLayout());
		content.setBackground(backgroundColor);

		// read the credits from an external file because code format gets it
		// unreadable if inlined
		final List<String> creditsList = readCredits();
		Font textFont = new Font("SansSerif", Font.BOLD, 12);
		sp = new ScrollerPanel(creditsList, textFont, 0, textColor,
				backgroundColor, 20);

		JPanel buttonPane = new JPanel();
		buttonPane.add(closeButton);

		content.add(sp, BorderLayout.CENTER);
		content.add(buttonPane, BorderLayout.SOUTH);
	}

	/**
	 * Reads the credits from credits.text.
	 *
	 * @return list of lines
	 */
	private List<String> readCredits() {
		final URL url = DataLoader.getResource("games/stendhal/client/gui/credits.txt");
		final List<String> res = new LinkedList<String>();
		try {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));
			try {
				String line = br.readLine();
				while (line != null) {
					res.add(line);
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		} catch (final IOException e) {
			res.add(0, "credits.txt not found");
		}
		return res;
	}

	/**
	 * Sets up the listeners an event handling.
	 */
	private void eventHandling() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				exit();
			}
		});
	}

	/**
	 * Exits Credits Dialog.
	 */
	private void exit() {
		sp.stop();
		this.setVisible(false);
		if (getOwner() != null) {
			getOwner().setEnabled(true);
		}
		this.dispose();
		LOGGER.debug("about dialog closed");
	}
}
