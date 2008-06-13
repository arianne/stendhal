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

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityView;
import games.stendhal.client.entity.User;
import games.stendhal.client.events.PositionChangeMulticaster;
import games.stendhal.client.gui.wt.Buddies;
import games.stendhal.client.gui.wt.BuddyListDialog;
import games.stendhal.client.gui.wt.Character;
import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.gui.wt.InternalManagedDialog;
import games.stendhal.client.gui.wt.KeyRing;
import games.stendhal.client.gui.wt.Minimap;
import games.stendhal.client.gui.wt.SettingsPanel;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** The main class that create the screen and starts the arianne client. */
public class j2DClient extends StendhalUI {


	private static final long serialVersionUID = 3356310866399084117L;

	/** width of the game screen (without the chat line). */
	public static int SCREEN_WIDTH;

	/** height of the game screen (without the chat line). */
	public static int SCREEN_HEIGHT;

	static {
		String[] dim = stendhal.SCREEN_SIZE.split("x");
		SCREEN_WIDTH = Integer.parseInt(dim[0]);
		SCREEN_HEIGHT = Integer.parseInt(dim[1]);
	}

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(j2DClient.class);

	private MainFrame mainFrame;
	private QuitDialog quitDialog;

	private GameScreen screen;

	private Canvas canvas;

	private JLayeredPane pane;

	private KTextEdit gameLog;

	private boolean gameRunning;

	/** NOTE: It sounds bad to see here a GUI component. Try other way. */
	private JTextField playerChatText;

	private boolean ctrlDown;

	private boolean shiftDown;

	private boolean altDown;

	/** settings panel. */
	private SettingsPanel settings;

	/** the Character panel. */
	private Character character;

	/** the Key ring panel. */
	private KeyRing keyring;

	/** the buddy list panel. */
	private BuddyListDialog nbuddies;

	private ManagedWindow buddies;


	/** the minimap panel. */
	private Minimap minimap;

	/** the inventory.*/
	private EntityContainer inventory;

	private User lastuser;


	private PositionChangeMulticaster positionChangeListener;
	private StendhalChatLineListener chatListener;
	/**
	 * Delayed direction release holder.
	 */
	protected DelayedDirectionRelease directionRelease;

	private static final boolean newCode = (System.getProperty("stendhal.newgui") != null);

	/**
	 * A constructor for JUnit tests.
	 */
	public j2DClient() {
		super(null);
		setDefault(this);
	}

	public j2DClient(StendhalClient client) {
		super(client);

		setDefault(this);
		mainFrame = new MainFrame();




		positionChangeListener = new PositionChangeMulticaster();

		Container content = mainFrame.getMainFrame().getContentPane();


		/*
		 * Get hold the content of the frame and set up the resolution of the
		 * game
		 */
		pane = new JLayeredPane();
		pane.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		content.add(pane);

		/*
		 * Wrap canvas in panel that can has setPreferredSize()
		 */
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		pane.add(panel, JLayeredPane.DEFAULT_LAYER);

		/*
		 * Setup our rendering canvas
		 */

		if (System.getProperty("stendhal.refactoringgui") != null) {
			canvas = new Canvas();
			// A bit repetitive... oh well
			canvas.setBounds(200, 0, 600, SCREEN_HEIGHT);
		} else {
			canvas = new Canvas();
			canvas.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		}
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		canvas.setIgnoreRepaint(true);
		panel.add(canvas);

		// register the slash actions in the client side command line parser
		SlashActionRepository.register();

		/*
		 * Chat input field
		 */
		playerChatText = new JTextField("");

		chatListener = new StendhalChatLineListener(
				client, playerChatText);
		playerChatText.addActionListener(chatListener);
		playerChatText.addKeyListener(chatListener);

		content.add(playerChatText);

		/*
		 * Always redirect focus to chat field
		 */
		canvas.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				playerChatText.requestFocus();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		/*
		 * Handle focus assertion and window closing
		 */
		mainFrame.getMainFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent ev) {
				playerChatText.requestFocus();
			}

			@Override
			public void windowActivated(WindowEvent ev) {
				playerChatText.requestFocus();
			}

			@Override
			public void windowGainedFocus(WindowEvent ev) {
				playerChatText.requestFocus();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				requestQuit();
			}
		});

		/*
		 * Quit dialog
		 *
		 *
		 */
		quitDialog = new QuitDialog();

		pane.add(quitDialog.getQuitDialog(), JLayeredPane.MODAL_LAYER);

		/*
		 * Game log
		 */
		gameLog = new KTextEdit();
		gameLog.setPreferredSize(new Dimension(SCREEN_WIDTH, 171));

		if (System.getProperty("stendhal.onewindow") != null) {
			content.add(gameLog);
			mainFrame.getMainFrame().pack();
		} else if (System.getProperty("stendhal.onewindowtitle") != null
				|| System.getProperty("stendhal.refactoringguiui") != null) {
			JLabel header = new JLabel();
			header.setText("Game Chat and Events Log");
			header.setFont(new java.awt.Font("Dialog", 3, 14));
			content.add(header);
			content.add(gameLog);
			mainFrame.getMainFrame().pack();
		} else {
			/*
			 * In own window
			 */
			final JDialog dialog = new JDialog(mainFrame.getMainFrame(),
					"Game chat and events log");

			content = dialog.getContentPane();
			content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
			content.add(gameLog);

			dialog.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					playerChatText.requestFocus();
				}

				public void focusLost(FocusEvent e) {
				}
			});

			dialog.pack();

			/*
			 * Move tracker
			 */
			mainFrame.getMainFrame().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					Rectangle bounds = mainFrame.getMainFrame().getBounds();

					dialog.setLocation(bounds.x, bounds.y + bounds.height);

					dialog.setVisible(true);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					Rectangle bounds = mainFrame.getMainFrame().getBounds();

					dialog.setLocation(bounds.x, bounds.y + bounds.height);
				}
			});
		}

		KeyListener keyListener = new GameKeyHandler();

		// add a key input system (defined below) to our canvas so we can
		// respond to key pressed
		playerChatText.addKeyListener(keyListener);
		canvas.addKeyListener(keyListener);

		// Display a warning message in case the screen size was adjusted
		// This is a temporary solution until this issue is fixed server side.
		// I hope that it discourages its use without the risks of unupdateable
		// clients being distributed
		if (!stendhal.SCREEN_SIZE.equals("640x480")) {
			addEventLine("Using window size cheat: " + stendhal.SCREEN_SIZE,
					NotificationType.NEGATIVE);
		}

		mainFrame.getMainFrame().setLocation(new Point(20, 20));

		// finally make the window visible
		mainFrame.getMainFrame().pack();
		mainFrame.getMainFrame().setResizable(false);
		mainFrame.getMainFrame().setVisible(true);

		screen = new GameScreen(client, canvas);

		GameScreen.setDefaultScreen(screen);
		client.setScreen(screen);

		positionChangeListener.add(screen);

		mainFrame.getMainFrame().toFront();

		/*
		 * In-screen dialogs
		 */
		settings = new SettingsPanel(SCREEN_WIDTH);
		screen.addDialog(settings);

		minimap = new Minimap(client);
		addWindow(minimap);
		settings.add(minimap, "Enable Minimap");

		positionChangeListener.add(minimap);

		character = new Character(this);
		addWindow(character);
		settings.add(character, "Enable Character");

		inventory = new EntityContainer("bag", 3, 4);
		addWindow(inventory);
		settings.add(inventory, "Enable Bag");

		keyring = new KeyRing();
		client.addFeatureChangeListener(keyring);
		addWindow(keyring);
		settings.add(keyring, "Enable Key Ring");

		if (newCode) {
			nbuddies = new BuddyListDialog(this);
			buddies = nbuddies;
		} else {
			Buddies obuddies = new Buddies(this);
			buddies = obuddies;
		}

		addWindow(buddies);
		settings.add(buddies, "Enable Buddies");

		// set some default window positions
		WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);

		directionRelease = null;

	} // constructor

	public void initialize() {

	}

	public void cleanup() {
		chatListener.save();
		logger.debug("Exit");
		System.exit(0);
	}

	/**
	 * Add a native in-window dialog to the screen.
	 *
	 * @param comp
	 *            The component to add.
	 */
	public void addDialog(Component comp) {
		pane.add(comp, JLayeredPane.PALETTE_LAYER);
	}


	// MEMORY DEBUGGING:
	// private long avgmemt = 0L;
	// private long avgmemc = 0L;

	public void gameLoop() {
		final int frameLength = (int) (1000.0 / stendhal.FPS_LIMIT);
		int fps = 0;
		GameObjects gameObjects = client.getGameObjects();
		StaticGameLayers gameLayers = client.getStaticGameLayers();

		// Clear the first screen
		screen.clear();
		// screen.place(-100, -100);
		SoundMaster.play("harp-1.wav");

		// keep looping until the game ends
		long refreshTime = System.currentTimeMillis();
		long lastFpsTime = refreshTime;
		long lastMessageHandle = refreshTime;

		gameRunning = true;

		// MEMORY DEBUGGING:
		// {
		// Runtime rt = Runtime.getRuntime();
		// rt.gc();
		// long mem = (rt.totalMemory() - rt.freeMemory()) / 1024L;
		// System.err.println("init mem = " + mem + "k");
		// }

		boolean canExit = false;
		while (!canExit) {
			fps++;
			// figure out what time it is right after the screen flip then
			// later we can figure out how long we have been doing redrawing
			// / networking, then we know how long we need to sleep to make
			// the next flip happen at the right time

			screen.nextFrame();
			long now = System.currentTimeMillis();
			int delta = (int) (now - refreshTime);
			refreshTime = now;

			// MEMORY DEBUGGING:
			// Runtime rt = Runtime.getRuntime();
			// long mem = (rt.totalMemory() - rt.freeMemory()) / 1024L;
			// avgmemt += mem;
			// avgmemc++;
			//
			// System.err.println("mem = " + (avgmemt / avgmemc) + "k");
			// //rt.gc();
			logger.debug("Move objects");
			gameObjects.update(delta);

			/*
			 * TODO: Consolidate the next 3 parts into one isInBatchUpdate()
			 * check, if User update code can be skipped [without side effects]
			 * while in it.
			 */
			if (!client.isInBatchUpdate() && gameLayers.changedArea()) {
				/*
				 * Update the screen
				 */
				screen.setMaxWorldSize(gameLayers.getWidth(),
						gameLayers.getHeight());
				screen.clear();
				screen.center();

				// [Re]create the map
				//
				// TODO: Replace with listener notification
				CollisionDetection cd = gameLayers.getCollisionDetection();
				if (cd != null) {
					minimap.update(cd,
							screen.expose().getDeviceConfiguration(),
							gameLayers.getArea());
				}

				gameLayers.resetChangedArea();
			}

			User user = User.get();

			if (user != null) {
				if (newCode) {
					/*
					 * Hack! Need to update list when changes arrive
					 */
					if (nbuddies.isVisible()) {
						nbuddies.update();
					}
				}

				// check if the player object has changed.
				// Note: this is an exact object reference check
				if (user != lastuser) {
					character.setPlayer(user);
					keyring.setSlot(user, "keyring");
					inventory.setSlot(user, "bag");

					lastuser = user;
				}
			}

			if (!client.isInBatchUpdate()) {
				minimap.update_pathfind();
				if (mainFrame.getMainFrame().getState() != Frame.ICONIFIED) {
					logger.debug("Draw screen");
					screen.draw();
				}
			}

			logger.debug("Query network");

			if (client.loop(0)) {
				lastMessageHandle = refreshTime;
			}

			/*
			 * Process delayed direction release
			 */
			if ((directionRelease != null) && directionRelease.hasExpired()) {
				client.removeDirection(directionRelease.getDirection(),
						directionRelease.isFacing());

				directionRelease = null;
			}

			if (logger.isDebugEnabled()) {
				if ((refreshTime - lastFpsTime) >= 1000L) {
					logger.debug("FPS: " + fps);
					long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
					long totalMemory = Runtime.getRuntime().totalMemory() / 1024;

					logger.debug("Total/Used memory: " + totalMemory + "/"
							+ (totalMemory - freeMemory));

					fps = 0;
					lastFpsTime = refreshTime;
				}
			}

			// Shows a offline icon if no messages are received in 120 seconds.
			if ((refreshTime - lastMessageHandle > 120000L)
					|| !client.getConnectionState()) {
				setOffline(true);
			} else {
				setOffline(false);
			}

			logger.debug("Start sleeping");
			// we know how long we want per screen refresh (40ms) then
			// we add the refresh time and subtract the current time
			// leaving us with the amount we still need to sleep.
			long wait = frameLength + refreshTime - System.currentTimeMillis();

			if (wait > 0) {
				if (wait > 100L) {
					logger.info("Waiting " + wait + " ms");
					wait = 100L;
				}

				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
				}
			}

			logger.debug("End sleeping");

			if (!gameRunning) {
				logger.info("Request logout");
				try {
					/*
					 * We request server permision to logout. Server can deny
					 * it.
					 */
					if (client.logout()) {
						canExit = true;
					} else {
						logger.warn("You can't logout now.");
						gameRunning = true;
					}
				} catch (Exception e) {
					/*
					 * If we get a timeout exception we accept exit request.
					 */
					canExit = true;
					logger.error(e, e);
				}
			}
		}
		// MEMORY DEBUGGING:
		// {
		// Runtime rt = Runtime.getRuntime();
		// //rt.gc();
		// long mem = (rt.totalMemory() - rt.freeMemory()) / 1024L;
		// System.err.println("end mem = " + mem + "k");
		// }

		SoundSystem.get().exit();
	}

	/**
	 * Convert a keycode to the corresponding direction.
	 *
	 * @param keyCode
	 *            The keycode.
	 *
	 * @return The direction, or <code>null</code>.
	 */
	protected Direction keyCodeToDirection(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			return Direction.LEFT;

		case KeyEvent.VK_RIGHT:
			return Direction.RIGHT;

		case KeyEvent.VK_UP:
			return Direction.UP;

		case KeyEvent.VK_DOWN:
			return Direction.DOWN;

		default:
			return null;
		}
	}

	protected void onKeyPressed(KeyEvent e) {
		if (e.isShiftDown()) {
			/*
			 * We are going to use shift to move to previous/next line of text
			 * with arrows so we just ignore the keys if shift is pressed.
			 */
			return;
		}

		switch (e.getKeyCode()) {
		case KeyEvent.VK_L:
			if (e.isControlDown()) {
				/*
				 * Ctrl+L Make game log visible
				 */
				SwingUtilities.getRoot(gameLog).setVisible(true);
			}

			break;

		case KeyEvent.VK_R:
			if (e.isControlDown()) {
				/*
				 * Ctrl+R Remove text bubbles
				 */
				screen.clearTexts();
			}

			break;

		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			/*
			 * Ctrl means face, otherwise move
			 */
			Direction direction = keyCodeToDirection(e.getKeyCode());

			if (e.isAltGraphDown()) {
				User user = User.get();

				EntityView view = screen.getEntityViewAt(user.getX()
						+ direction.getdx(), user.getY() + direction.getdy());

				if (view != null) {
					Entity entity = view.getEntity();
					if (!entity.equals(user)) {
						view.onAction();
						// TODO: Do we want to move also? Or just 'return' here?
					}
				}
			}

			processDirectionPress(direction, e.isControlDown());
		}
	}

	protected void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_DOWN:
			/*
			 * Ctrl means face, otherwise move
			 */
			processDirectionRelease(keyCodeToDirection(e.getKeyCode()),
					e.isControlDown());
		}
	}

	/**
	 * Handle direction press actions.
	 *
	 * @param direction
	 *            The direction.
	 * @param facing
	 *            If facing only.
	 */
	protected void processDirectionPress(Direction direction, boolean facing) {
		if (directionRelease != null) {
			if (directionRelease.check(direction, facing)) {
				/*
				 * Cancel pending release
				 */
				logger.debug("Repeat suppressed");
				directionRelease = null;
				return;
			} else {
				/*
				 * Flush pending release
				 */
				client.removeDirection(directionRelease.getDirection(),
						directionRelease.isFacing());

				directionRelease = null;
			}
		}

		client.addDirection(direction, facing);
	}

	/**
	 * Handle direction release actions.
	 *
	 * @param direction
	 *            The direction.
	 * @param facing
	 *            If facing only.
	 */
	protected void processDirectionRelease(Direction direction, boolean facing) {
		if (directionRelease != null) {
			if (directionRelease.check(direction, facing)) {
				/*
				 * Ignore repeats
				 */
				return;
			} else {
				/*
				 * Flush previous release
				 */
				client.removeDirection(directionRelease.getDirection(),
						directionRelease.isFacing());
			}
		}

		directionRelease = new DelayedDirectionRelease(direction, facing);
	}





	/**
	 * Save the current keyboard modifier (i.e. Alt/Ctrl/Shift) state.
	 *
	 * @param ev
	 *            The keyboard event.
	 */
	protected void updateModifiers(KeyEvent ev) {
		altDown = ev.isAltDown();
		ctrlDown = ev.isControlDown();
		shiftDown = ev.isShiftDown();
	}

	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
	@Override
	public void shutdown() {
		gameRunning = false;

		// try to save the window configuration
		WtWindowManager.getInstance().save();
	}

	//
	// <StendhalGUI>
	//

	/**
	 * Add a new window.
	 *
	 * @param mw
	 *            A managed window.
	 *
	 * @throws IllegalArgumentException
	 *             If an unsupported ManagedWindow is given.
	 */
	@Override
	public void addWindow(ManagedWindow mw) {
		if (mw instanceof InternalManagedDialog) {
			addDialog(((InternalManagedDialog) mw).getDialog());
		} else if (mw instanceof WtPanel) {
			screen.addDialog((WtPanel) mw);
		} else {
			throw new IllegalArgumentException("Unsupport ManagedWindow type: "
					+ mw.getClass().getName());
		}
	}

	/**
	 * Determine if the Alt key is held down.
	 *
	 * @return Returns <code>true</code> if down.
	 */
	@Override
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * Determine if the [Ctrl] key is held down.
	 *
	 * @return Returns <code>true</code> if down.
	 */
	@Override
	public boolean isCtrlDown() {
		return ctrlDown;
	}

	/**
	 * Determine if the [Shift] key is held down.
	 *
	 * @return Returns <code>true</code> if down.
	 */
	@Override
	public boolean isShiftDown() {
		return shiftDown;
	}

	//
	// StendhalUI
	//

	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(String text) {
		addEventLine("", text, NotificationType.NORMAL);
	}

	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(String header, String text) {
		addEventLine(header, text, NotificationType.NORMAL);
	}

	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(final String text, final NotificationType type) {
		addEventLine("", text, type);
	}

	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(final String header, final String text,
			final NotificationType type) {
		gameLog.addLine(header, text, type);
	}

	/**
	 * Initiate outfit selection by the user.
	 */
	@Override
	public void chooseOutfit() {
		int outfit;

		RPObject player = client.getPlayer();

		if (player.has("outfit_org")) {
			outfit = player.getInt("outfit_org");
		} else {
			outfit = player.getInt("outfit");
		}

		// Should really keep only one instance of this around
		OutfitDialog dialog = new OutfitDialog(mainFrame.getMainFrame(), "Set outfit", outfit);
		dialog.setVisible(true);
	}

	@Override
	public void manageGuilds() {
		GuildManager gm = new GuildManager();
		gm.setVisible(true);
	}

	/**
	 * Get the current game screen height.
	 *
	 * @return The height.
	 */
	@Override
	public int getHeight() {
		return SCREEN_HEIGHT;
	}

	/**
	 * Get the game screen.
	 *
	 * @return The game screen.
	 */
	@Override
	public IGameScreen getScreen() {
		return screen;
	}

	/**
	 * Get the current game screen width.
	 *
	 * @return The width.
	 */
	@Override
	public int getWidth() {
		return SCREEN_WIDTH;
	}



	/**
	 * Set the input chat line text.
	 *
	 * @param text
	 *            The text.
	 */
	@Override
	public void setChatLine(String text) {
		playerChatText.setText(text);
	}

	/**
	 * Set the user's positiion.
	 *
	 * @param x
	 *            The user's X coordinate.
	 * @param y
	 *            The user's Y coordinate.
	 */
	@Override
	public void setPosition(double x, double y) {
		positionChangeListener.positionChanged(x, y);
	}

	/**
	 * Sets the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	@Override
	public void setOffline(boolean offline) {
		screen.setOffline(offline);
	}

	//
	//

	protected class GameKeyHandler implements KeyListener {
		public void keyPressed(KeyEvent e) {
			updateModifiers(e);

			onKeyPressed(e);
		}

		public void keyReleased(KeyEvent e) {
			updateModifiers(e);
			onKeyReleased(e);
		}

		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == 27) {
				// Escape
				requestQuit();
			}
		}
	}






	public static void main(String[] args) {
		if (args.length > 0) {
			int i = 0;
			String port = null;
			String username = null;
			String password = null;
			String host = null;

			while (i != args.length) {
				if (args[i].equals("-u")) {
					username = args[i + 1];
				} else if (args[i].equals("-p")) {
					password = args[i + 1];
				} else if (args[i].equals("-h")) {
					host = args[i + 1];
				} else if (args[i].equals("-port")) {
					port = args[i + 1];
				}
				i++;
			}

			if ((username != null) && (password != null) && (host != null)
					&& (port != null)) {
				StendhalClient client = StendhalClient.get();

				try {
					client.connect(host, Integer.parseInt(port));
					client.login(username, password);

					j2DClient locclient = new j2DClient(client);
					locclient.initialize();
					locclient.gameLoop();
					locclient.cleanup();
				} catch (Exception ex) {
					logger.error(ex, ex);
				}

				return;
			}
		}

		System.out.println("Stendhal j2DClient\n");
		System.out.println("  games.stendhal.j2DClient -u username -p pass -h host -c character\n");
		System.out.println("Required parameters");
		System.out.println("* -h\tHost that is running Marauroa server");
		System.out.println("* -port\tport of the Marauroa server (try 32160)");
		System.out.println("* -u\tUsername to log into Marauroa server");
		System.out.println("* -p\tPassword to log into Marauroa server");
	}


	protected static class DelayedDirectionRelease {
		/**
		 * The maximum delay between auto-repeat release-press.
		 */
		protected static final long DELAY = 50L;

		protected long expiration;

		protected Direction dir;

		protected boolean facing;

		public DelayedDirectionRelease(Direction dir, boolean facing) {
			this.dir = dir;
			this.facing = facing;

			expiration = System.currentTimeMillis() + DELAY;
		}

		//
		// DelayedDirectionRelease
		//

		/**
		 * Get the direction.
		 *
		 * @return The direction.
		 */
		public Direction getDirection() {
			return dir;
		}

		/**
		 * Determine if the delay point has been reached.
		 *
		 * @return <code>true</code> if the delay time has been reached.
		 */
		public boolean hasExpired() {
			return System.currentTimeMillis() >= expiration;
		}

		/**
		 * Determine if the facing only option was used.
		 *
		 * @return <code>true</code> if facing only.
		 */
		public boolean isFacing() {
			return facing;
		}

		/**
		 * Check if a new direction matches the existing one, and if so, reset
		 * the expiration point.
		 *
		 * @param dir
		 *            The direction.
		 * @param facing
		 *            The facing flag.
		 *
		 * @return <code>true</code> if this is a repeat.
		 */
		public boolean check(Direction dir, boolean facing) {
			if (!this.dir.equals(dir)) {
				return false;
			}

			if (this.facing != facing) {
				return false;
			}

			long now = System.currentTimeMillis();

			if (now >= expiration) {
				return false;
			}

			expiration = now + DELAY;

			return true;
		}
	}






	@Override
	public void requestQuit() {
		quitDialog.requestQuit();

	}
}
