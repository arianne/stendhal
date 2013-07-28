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
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Summary description for LoginGUI.
 *
 */
@SuppressWarnings("serial")
public class StendhalFirstScreen extends JFrame {
	private static final long serialVersionUID = -7825572598938892220L;
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 32;

	/** Name of the font used for the html areas. Should match the file name without .ttf */
	private static final String FONT_NAME = "BlackChancery";

	private final StendhalClient client;

	private final Image background;

	private JButton loginButton;
	private JButton createAccountButton;
	private JButton helpButton;
	private JButton creditButton;

	static {
		// This is the initial window, when loaded at all.
		Initializer.init();
	}

	/**
	 * Creates the first screen.
	 *
	 * @param client
	 *            StendhalClient
	 */
	public StendhalFirstScreen(final StendhalClient client) {
		super(detectScreen());
		setLocationByPlatform(true);
		this.client = client;
		client.setSplashScreen(this);

		final URL url = DataLoader.getResource(ClientGameConfiguration.get("GAME_SPLASH_BACKGROUND"));
		final ImageIcon imageIcon = new ImageIcon(url);
		background = imageIcon.getImage();

		initializeComponent();

		setVisible(true);
	}

	/**
	 * Detect the preferred screen by where the mouse is the moment the method
	 * is called. This is for multi-monitor support.
	 *
	 * @return GraphicsEnvironment of the current screen
	 */
	private static GraphicsConfiguration detectScreen() {
		PointerInfo pointer = MouseInfo.getPointerInfo();
		if (pointer != null) {
			return pointer.getDevice().getDefaultConfiguration();
		}
		return null;
	}

	/**
	 * Setup the window contents.
	 */
	private void initializeComponent() {
		setContentPane(new JComponent() {
			{
				setOpaque(true);
				setPreferredSize(new Dimension(background.getWidth(this), background.getHeight(this)));
			}

			@Override
			public void paintComponent(final Graphics g) {
				g.drawImage(background, 0, 0, this);
			}
		});

		Font font = new Font(FONT_NAME, Font.PLAIN, 16);

		//
		// Login
		//
		Action loginAction = new AbstractAction("Login to "
				+ ClientGameConfiguration.get("GAME_NAME")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				new LoginDialog(StendhalFirstScreen.this, client).setVisible(true);
			}
		};
		loginAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		loginAction.putValue(Action.SHORT_DESCRIPTION, "Press this button to login to a "
				+ ClientGameConfiguration.get("GAME_NAME") + " server");
		
		loginButton = new JButton();
		loginButton.setAction(loginAction);
		loginButton.setFont(font);

		//
		// Create account
		//
		Action createAccountAction = new AbstractAction("Create an account") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreateAccountDialog(StendhalFirstScreen.this, client);
			}
		};
		createAccountAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		createAccountAction.putValue(Action.SHORT_DESCRIPTION, "Press this button to create an account on a "
				+ ClientGameConfiguration.get("GAME_NAME") + " server.");
		
		createAccountButton = new JButton();
		createAccountButton.setFont(font);
		createAccountButton.setAction(createAccountAction);

		//
		// Help
		//
		Action helpAction = new AbstractAction("Help") {
			@Override
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.openURL("http://stendhalgame.org/wiki/Stendhal_Manual");
			}
		};
		helpAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
		
		helpButton = new JButton();
		helpButton.setFont(font);
		helpButton.setAction(helpAction);

		//
		// Credits
		//
		Action showCreditsAction = new AbstractAction("Credits") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CreditsDialog(StendhalFirstScreen.this);
			}
		};
		showCreditsAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);

		creditButton = new JButton();
		creditButton.setFont(font);
		creditButton.setAction(showCreditsAction);

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
		setResizable(false);

		final URL url = DataLoader.getResource(ClientGameConfiguration.get("GAME_ICON"));
		this.setIconImage(new ImageIcon(url).getImage());
		pack();
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		loginButton.setEnabled(b);
		createAccountButton.setEnabled(b);
		helpButton.setEnabled(b);
		creditButton.setEnabled(b);
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
