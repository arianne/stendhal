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

import static games.stendhal.client.gui.layout.SBoxLayout.COMMON_PADDING;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.login.CreateAccountDialog;
import games.stendhal.client.gui.login.LoginDialog;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;

/**
 * Summary description for LoginGUI.
 *
 */
@SuppressWarnings("serial")
public class StendhalFirstScreen extends JFrame {
	private static final long serialVersionUID = -7825572598938892220L;

	/** Name of the font used for the html areas. Should match the file name without .ttf */
	private static final String FONT_NAME = "BlackChancery";
	private static final int FONT_SIZE = 16;

	private final StendhalClient client;

	private BackgroundComponent backgroundComponent;
	private JButton loginButton;
	private JButton createAccountButton;
	private JButton helpButton;
	private JButton creditButton;
	private final Point zeroPoint = new Point();

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
		WindowUtils.trackLocation(this, "main", true);
		this.client = client;
		client.setSplashScreen(this);

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
		backgroundComponent = new BackgroundComponent();
		setContentPane(backgroundComponent);

		Font font = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);

		//
		// Login
		//
		String gameName = ClientGameConfiguration.get("GAME_NAME");
		Action loginAction = new AbstractAction("Login to " + gameName) {
			@Override
			public void actionPerformed(ActionEvent e) {
				new LoginDialog(StendhalFirstScreen.this, client).setVisible(true);
			}
		};
		loginAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
		loginAction.putValue(Action.SHORT_DESCRIPTION, "Press this button to login to a "
				+ gameName + " server");

		loginButton = createTransparentButton();
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
				+ gameName + " server.");

		createAccountButton = createTransparentButton();
		createAccountButton.setFont(font);
		createAccountButton.setAction(createAccountAction);

		//
		// Help
		//
		Action helpAction = new AbstractAction("Help") {
			@Override
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.openURL("https://stendhalgame.org/wiki/Stendhal_Manual");
			}
		};
		helpAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);

		helpButton = createTransparentButton();
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

		creditButton = createTransparentButton();
		creditButton.setFont(font);
		creditButton.setAction(showCreditsAction);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Add the buttons
		backgroundComponent.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		// The buttons should be a bit larger than by default. We have enough
		// space.
		gbc.ipadx = 2 * COMMON_PADDING;
		gbc.ipady = 2;

		// All extra space should be above
		gbc.weighty = 1.0;
		backgroundComponent.add(Box.createVerticalGlue(), gbc);
		gbc.weighty = 0.0;

		gbc.gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(COMMON_PADDING, COMMON_PADDING, COMMON_PADDING, COMMON_PADDING);

		backgroundComponent.add(loginButton, gbc);
		gbc.gridy++;
		backgroundComponent.add(createAccountButton, gbc);
		gbc.gridy++;
		backgroundComponent.add(helpButton, gbc);
		gbc.gridy++;
		backgroundComponent.add(creditButton, gbc);
		gbc.gridy++;
		backgroundComponent.add(Box.createVerticalStrut(2 * COMMON_PADDING), gbc);

		getRootPane().setDefaultButton(loginButton);

		//
		// LoginGUI
		//
		setTitle(gameName + " " + stendhal.VERSION
				+ " - a multiplayer online game using Arianne");

		URL url = DataLoader.getResource(ClientGameConfiguration.get("GAME_ICON"));
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
	
	private JButton createTransparentButton() {
		return new JButton() {
			@Override
			public void paint(Graphics g) {
				super.paint(getWrapperGraphics(g, this));
			}
		};
	}

	private Graphics getWrapperGraphics(Graphics g, JComponent component) {
		if (g instanceof TransparencyGraphicsWrapper) {
			return g;
		}

		Dimension size = getSize();
		Point relativePos = SwingUtilities.convertPoint(component, zeroPoint, this);
		backgroundComponent.paintBgImage(g, size, -relativePos.x, -relativePos.y);
		return BackgroundComponent.createGraphicsWrapper(g);
	}

	/**
	 * Background component with automatically scaled background image.
	 */
	private static class BackgroundComponent extends JComponent {

		private final ImageObserver emptyObserver = (Image img, int infoflags, int x, int y, int width,
				int height) -> false;

		private final Image bgImage;
		private final Dimension bgImageSize;

		public BackgroundComponent() {
			URL url = DataLoader.getResource(ClientGameConfiguration.get("GAME_SPLASH_BACKGROUND"));
			bgImage = new ImageIcon(url).getImage();
			bgImageSize = new Dimension(bgImage.getWidth(emptyObserver), bgImage.getHeight(emptyObserver));
			setPreferredSize(bgImageSize);
		}

		@Override
		public void paint(Graphics g) {
			Dimension size = getSize();
			paintBgImage(g, size, 0, 0);

			Graphics gWrapper = getComponentGraphics(g);
			super.paint(gWrapper);
		}

		public void paintBgImage(Graphics g, Dimension size, int bgX, int bgY) {
			int bgWidth;
			int bgHeight;
			if (bgImageSize.width * size.height >= size.width * bgImageSize.height) {
				bgWidth = size.height * bgImageSize.width / bgImageSize.height;
				bgHeight = size.height;
			} else {
				bgWidth = size.width;
				bgHeight = size.width * bgImageSize.height / bgImageSize.width;
			}

			g.drawImage(bgImage, bgX, bgY, bgWidth, bgHeight, emptyObserver);
		}

		@Override
		protected Graphics getComponentGraphics(Graphics g) {
			return BackgroundComponent.createGraphicsWrapper(g);
		}

		private static TransparencyGraphicsWrapper createGraphicsWrapper(Graphics g) {
			if (g instanceof TransparencyGraphicsWrapper) {
				return (TransparencyGraphicsWrapper) g;
			}
			return new TransparencyGraphicsWrapper((Graphics2D) g, 127);
		}
	}
}
