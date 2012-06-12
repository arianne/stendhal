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
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

/**
 * Summary description for LoginGUI.
 *
 */
@SuppressWarnings("serial")
public class StendhalFirstScreen extends JFrame {
	private static final long serialVersionUID = -7825572598938892220L;
	private static final Logger logger = Logger.getLogger(StendhalFirstScreen.class);
	private static final int BUTTON_WIDTH = 160;
	private static final int BUTTON_HEIGHT = 32;

	/** Name of the font used for the html areas. Should match the file name without .ttf */
	private static final String FONT_NAME = "BlackChancery";
	/** Font used for the html areas */
	private static final String FONT = "data/gui/" + FONT_NAME + ".ttf";

	private final StendhalClient client;

	private final Image background;

	private JButton loginButton;
	private JButton createAccountButton;
	private JButton helpButton;
	private JButton creditButton;

	// load an atmospheric font for the text
	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		// Don't needlessly load the font if user already has it installed
		boolean needsLoading = true;
		for (String font : ge.getAvailableFontFamilyNames()) {
			if (FONT_NAME.equals(font)) {
				needsLoading = false;
				break;
			}
		}
		if (needsLoading) {
			try {
				// Call via reflection to keep supporting java 1.5
				Method m = ge.getClass().getMethod("registerFont", Font.class);
				m.invoke(ge, Font.createFont(Font.TRUETYPE_FONT, DataLoader.getResourceAsStream(FONT)));
			} catch (IOException e) {
				logger.error("Error loading custom font", e);
			} catch (FontFormatException e) {
				logger.error("Error loading custom font", e);
			} catch (SecurityException e) {
				logger.error("Error loading custom font", e);
			} catch (NoSuchMethodException e) {
				logger.error("Error loading custom font. Java version 6 or later is required for that to work.");
			} catch (IllegalArgumentException e) {
				logger.error("Error loading custom font", e);
			} catch (IllegalAccessException e) {
				logger.error("Error loading custom font", e);
			} catch (InvocationTargetException e) {
				logger.error("Error loading custom font", e);
			}
		}
		initApplicationName();
	}

	/**
	 * Creates the first screen.
	 *
	 * @param client
	 *            StendhalClient
	 */
	public StendhalFirstScreen(final StendhalClient client) {
		super(detectScreen());
		this.client = client;
		client.setSplashScreen(this);

		final URL url = DataLoader.getResource(ClientGameConfiguration.get("GAME_SPLASH_BACKGROUND"));
		final ImageIcon imageIcon = new ImageIcon(url);
		background = imageIcon.getImage();

		initializeComponent();

		this.setVisible(true);
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
	 * Set the application name for the windowing system.
	 */
	private static void initApplicationName() {
		/*
		 * WM_CLASS for X window managers that use it
		 * (A workaround, see java RFE 6528430)
		 *
		 * Used for example in collapsing the window list in gnome 2, and
		 * for the application menu in gnome 3.
		 */
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			java.lang.reflect.Field awtAppClassNameField =
				toolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(toolkit, stendhal.GAME_NAME);
		} catch (NoSuchFieldException e) {
			logger.debug("Not setting X application name " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("Not setting X application name " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug("Not setting X application name: " + e.getMessage());
		}
		// Setting the name for Mac probably requires using the native LAF, and
		// we do not use it
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

		Font font = new Font(FONT_NAME, Font.PLAIN, 16);

		//
		// loginButton
		//
		loginButton = new JButton();
		loginButton.setFont(font);
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
		createAccountButton = new JButton();
		createAccountButton.setFont(font);
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
		helpButton = new JButton();
		helpButton.setFont(font);
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
		creditButton = new JButton();
		creditButton.setFont(font);
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
		this.setResizable(false);

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

	private void login() {
		new LoginDialog(StendhalFirstScreen.this, client).setVisible(true);
	}

	private void showCredits() {
		new CreditsDialog(StendhalFirstScreen.this);
	}

	private void showHelp() {
		BareBonesBrowserLaunch.openURL("http://stendhalgame.org/wiki/Stendhal_Manual");
	}

	/**
	 * Opens the create account dialog after checking the server version.
	 */
	public void createAccount() {
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
