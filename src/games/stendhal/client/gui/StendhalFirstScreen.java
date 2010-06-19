/* $Id$ */
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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.login.CreateAccountDialog;
import games.stendhal.client.gui.login.LoginDialog;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.client.update.HttpClient;
import games.stendhal.client.update.Version;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Summary description for LoginGUI.
 * 
 */
public class StendhalFirstScreen extends JFrame {
	private static final long serialVersionUID = -7825572598938892220L;
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 32;

	private final StendhalClient client;

	private final Image background;

	/**
	 * Creates the first screen.
	 * 
	 * @param client
	 *            StendhalClient
	 */
	public StendhalFirstScreen(final StendhalClient client) {
		super();
		this.client = client;

		final URL url = this.getClass().getClassLoader().getResource(
				ClientGameConfiguration.get("GAME_SPLASH_BACKGROUND"));
		final ImageIcon imageIcon = new ImageIcon(url);
		background = imageIcon.getImage();

		initializeComponent();

		this.setVisible(true);
	}

	/**
	 * Setup the window contents.
	 */
	private void initializeComponent() {
		setContentPane(new JComponent() {
			{
				setOpaque(true);
				setPreferredSize(new Dimension(640, 480));
			}

			@Override
			public void paintComponent(final Graphics g) {
				g.drawImage(background, 0, 0, this);
			}
		});

		//
		// loginButton
		//
		final JButton loginButton = new JButton();
		loginButton.setText("Login to "
				+ ClientGameConfiguration.get("GAME_NAME"));
		loginButton.setMnemonic(KeyEvent.VK_L);
		loginButton.setToolTipText("Press this button to login to a "
				+ ClientGameConfiguration.get("GAME_NAME") + " server");
		loginButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				login();
			}
		});
		//
		// createAccountButton
		//
		final JButton createAccountButton = new JButton();
		createAccountButton.setText("Create an account");
		createAccountButton.setMnemonic(KeyEvent.VK_A);
		createAccountButton.setToolTipText("Press this button to create an account on a "
				+ ClientGameConfiguration.get("GAME_NAME") + " server.");
		createAccountButton.setEnabled(true);
		createAccountButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				createAccount();
			}
		});
		//
		// creaditButton
		//
		final JButton helpButton = new JButton();
		helpButton.setText("Help");
		helpButton.setMnemonic(KeyEvent.VK_H);
		helpButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				showHelp();
			}
		});
		//
		// creaditButton
		//
		final JButton creditButton = new JButton();
		creditButton.setText("Credits");
		creditButton.setMnemonic(KeyEvent.VK_C);
		creditButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				showCredits();
			}
		});

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});

		//
		// contentPane
		//
		final Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		int x = (background.getWidth(null) - BUTTON_WIDTH) / 2; 
		addComponent(contentPane, loginButton, x, 300, BUTTON_WIDTH, BUTTON_HEIGHT);
		addComponent(contentPane, createAccountButton, x, 340, BUTTON_WIDTH, BUTTON_HEIGHT);
		addComponent(contentPane, helpButton, x, 380, BUTTON_WIDTH, BUTTON_HEIGHT);
		addComponent(contentPane, creditButton, x, 420, BUTTON_WIDTH, BUTTON_HEIGHT);

		getRootPane().setDefaultButton(loginButton);

		//
		// LoginGUI
		//
		setTitle(ClientGameConfiguration.get("GAME_NAME") + " "
				+ stendhal.VERSION
				+ " - a multiplayer online game using Arianne");
		this.setLocation(new Point(100, 100));
		this.setResizable(false);

		final URL url = this.getClass().getClassLoader().getResource(
				ClientGameConfiguration.get("GAME_ICON"));
		this.setIconImage(new ImageIcon(url).getImage());
		pack();
	}

	private void login() {
		checkVersion();
		new LoginDialog(StendhalFirstScreen.this, client).setVisible(true);
	}

	private void showCredits() {
		new CreditsDialog(StendhalFirstScreen.this);
	}

	private void showHelp() {
		BareBonesBrowserLaunch.openURL("http://stendhalgame.org/wiki/Stendhal_Manual");
	}

	private void checkVersion() {
		final HttpClient httpClient = new HttpClient(
				ClientGameConfiguration.get("UPDATE_VERSION_CHECK"));
		final String version = httpClient.fetchFirstLine();
		if (version != null) {
			if (Version.compare(version, stendhal.VERSION) > 0) {
				// custom title, warning icon
				JOptionPane.showMessageDialog(
						null,
						"Your client is out of date. Latest version is "
								+ version
								+ ". But you are using "
								+ stendhal.VERSION
								+ ".\nDownload from http://arianne.sourceforge.net",
						"Client out of date", JOptionPane.WARNING_MESSAGE);
			}
		}

	}

	/**
	 * Opens the create account dialog after checking the server version.
	 */
	public void createAccount() {
		checkVersion();
		new CreateAccountDialog(StendhalFirstScreen.this, client);
	}

	/** Adds Component Without a Layout Manager (Absolute Positioning). 
	 * @param container 
	 * @param c 
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height */
	private void addComponent(final Container container, final Component c, final int x, final int y,
			final int width, final int height) {
		c.setBounds(x, y, width, height);
		container.add(c);
	}

}
