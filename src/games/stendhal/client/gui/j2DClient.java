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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.PerceptionListenerImpl;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.UserContext;
import games.stendhal.client.World;
import games.stendhal.client.WorldObjects;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.bag.BagPanelControler;
import games.stendhal.client.gui.buddies.BuddyPanelControler;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chattext.ChatCompletionHelper;
import games.stendhal.client.gui.chattext.ChatTextController;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.map.MapPanelController;
import games.stendhal.client.gui.stats.StatsPanelController;
import games.stendhal.client.gui.wt.Character;
import games.stendhal.client.gui.wt.EntityContainer;
import games.stendhal.client.gui.wt.InternalManagedDialog;
import games.stendhal.client.gui.wt.KeyRing;
import games.stendhal.client.gui.wt.SettingsPanel;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.listener.PositionChangeMulticaster;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.manager.SoundFile.Type;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** The main class that create the screen and starts the arianne client. */
public class j2DClient implements UserInterface {
	/**
	 * A shared [singleton] copy.
	 */
	private static j2DClient sharedUI;
	
	/**
	 * Get the default UI.
	 * @return  the instance
	 *
	 *
	 */
	public static j2DClient get() {
		return sharedUI;
	}
	/**
	 * Set the shared [singleton] value.
	 *
	 * @param sharedUI
	 *            The stendhal UI.
	 */
	public static void setDefault(final j2DClient sharedUI) {
		j2DClient.sharedUI = sharedUI;
		ClientSingletonRepository.setUserInterface(sharedUI);
	}


	private static final long serialVersionUID = 3356310866399084117L;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(j2DClient.class);

	private MainFrame mainFrame;
	private QuitDialog quitDialog;

	private GameScreen screen;

	private JLayeredPane pane;

	private KTextEdit gameLog;

	private boolean gameRunning;



	ChatTextController chatText = new ChatTextController();
	
	private boolean ctrlDown;

	private boolean shiftDown;

	private boolean altDown;

	/** settings panel. */
	private SettingsPanel settings;

	/** the Character panel. */
	private Character character;

	/** the Key ring panel. */
	private KeyRing keyring;

	/** the minimap panel. */
	private MapPanelController minimap;

	/** the inventory.*/
	private EntityContainer inventory;

	private User lastuser;


	private final PositionChangeMulticaster positionChangeListener = new PositionChangeMulticaster();
	/**
	 * Delayed direction release holder.
	 */
	protected DelayedDirectionRelease directionRelease;

	private UserContext userContext;

	private final IPerceptionListener perceptionListener = new PerceptionListenerImpl() {
		int times;
		@Override
		public void onSynced() {
			setOffline(false);
			times = 0;
			logger.debug("Synced with server state.");
			addEventLine(new HeaderLessEventLine("Synchronized",
					NotificationType.CLIENT));
			
		}
		
		@Override
		public void onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				addEventLine(new HeaderLessEventLine("Unsynced: Resynchronizing...",
						NotificationType.CLIENT));
			}
			
		}
	};

	/**
	 * The stendhal client.
	 */
	protected StendhalClient client;

	private SoundSystemFacade soundSystemFacade;


	/**
	 * A constructor for JUnit tests.
	 */
	public j2DClient() {
		
		setDefault(this);
	}

	public j2DClient(final StendhalClient client, final GameScreen gameScreen, final UserContext userContext) {
		this.client = client;
		this.userContext = userContext;
		setDefault(this);
		/*
		 * Stop swing from using unsafe popup menus
		 */
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		minimap = new MapPanelController(client);
		final StatsPanelController stats = StatsPanelController.get();
		final BuddyPanelControler buddies = new BuddyPanelControler();
		final JScrollPane buddyPane = new JScrollPane();
		/*
		 * A border looks inconsistent with the stats panel when the scroll bar
		 * is not visible.
		 */
		buddyPane.setBorder(null);
		buddyPane.setViewportView(buddies.getComponent());
				
		/*
		 * Add a layered pane for the game area, so that we can have
		 * windows on top of it
		 */
		pane = new JLayeredPane();
		pane.setPreferredSize(stendhal.screenSize);
		/*
		 *  Set the sizes strictly so that the layout manager
		 *  won't try to resize it
		 */
		pane.setMaximumSize(stendhal.screenSize);
	
		/*
		 * Create the main game screen
		 */
		screen = new GameScreen(client);
		GameScreen.setDefaultScreen(screen);
		screen.setMinimumSize(new Dimension(stendhal.screenSize.width, 0));
		
		// ... and put it on the ground layer of the pane
		pane.add(screen, Component.LEFT_ALIGNMENT, JLayeredPane.DEFAULT_LAYER);

		client.setScreen(screen);
		positionChangeListener.add(screen);

				
		final KeyAdapter tabcompletion = new ChatCompletionHelper(chatText, World.get().getPlayerList().getNamesList());
		chatText.addKeyListener(tabcompletion);
		
		/*
		 * Always redirect focus to chat field
		 */
		screen.addFocusListener(new FocusListener() {
			public void focusGained(final FocusEvent e) {
				chatText.getPlayerChatText().requestFocus();
			}

			public void focusLost(final FocusEvent e) {
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
		gameLog.setPreferredSize(new Dimension(getWidth(), 171));

		final KeyListener keyListener = new GameKeyHandler();

		// add a key input system (defined below) to our canvas so we can
		// respond to key pressed
		chatText.addKeyListener(keyListener);
		screen.addKeyListener(keyListener);

		// Display a warning message in case the screen size was adjusted
		// This is a temporary solution until this issue is fixed server side.
		// I hope that it discourages its use without the risks of unupdateable
		// clients being distributed
		if (!stendhal.screenSize.equals(new Dimension(640, 480))) {
			addEventLine(new HeaderLessEventLine(("Using window size cheat: " + getWidth() + "x" + getHeight()), NotificationType.NEGATIVE));
		}	

		/*
		 * In-screen dialogs
		 */
		settings = new SettingsPanel(gameScreen);
		screen.addDialog(settings);

		character = new Character(this, gameScreen);
		addWindow(character);
		settings.add(character, "buddy", gameScreen);
		
		createAndAddOldBag(gameScreen);
		//createAndAddNewBag(mainFrameContentPane);
		
		keyring = new KeyRing(gameScreen);
		client.addFeatureChangeListener(keyring);
		addWindow(keyring);
		settings.add(keyring, "keyring", gameScreen);
		
		settings.addSeparator();
		
		settings.add(null, "help", gameScreen);
		settings.add(null, "accountcontrol", gameScreen);
		settings.add(null, "settings", gameScreen);
		settings.add(null, "rp", gameScreen);
		
		// set some default window positions
		final WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);
		
		/*
		 * Finally create the window, and place all the components in it
		 */
		// Create the main window
		mainFrame = new MainFrame();
		mainFrame.getMainFrame().getContentPane().setBackground(Color.black);
		JComponent glassPane = DragLayer.get();
		mainFrame.getMainFrame().setGlassPane(glassPane);
		glassPane.setVisible(true);
		
		// *** Create the layout ***
		final JComponent leftColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		leftColumn.add(minimap.getComponent(), SBoxLayout.constraint(SLayout.EXPAND_X));
		leftColumn.add(stats.getComponent(), SBoxLayout.constraint(SLayout.EXPAND_X));
		leftColumn.add(buddyPane, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));
		
		// Chat entry and chat log
		final JComponent chatBox = new JComponent() {};
		chatBox.setLayout(new BorderLayout());
		chatBox.add(chatText.getPlayerChatText(), BorderLayout.NORTH);
		chatBox.add(gameLog, BorderLayout.CENTER);
		chatBox.setMinimumSize(chatText.getPlayerChatText().getMinimumSize());
		chatBox.setMaximumSize(new Dimension(stendhal.screenSize.width, Integer.MAX_VALUE));
		
		// Give the user the ability to make the the game area less tall
		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, chatBox);
		splitPane.setBorder(null);
		// Works for showing the resize, but is extremely flickery
		//splitPane.setContinuousLayout(true);
		pane.addComponentListener(new SplitPaneResizeListener(screen, splitPane));

		// Avoid panel drawing overhead
		final Container windowContent = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		mainFrame.getMainFrame().setContentPane(windowContent);
		
		// Finally add the left pane, and the games creen + chat combo
		// Make the panel take any horizontal resize
		windowContent.add(leftColumn, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));
		leftColumn.setMinimumSize(new Dimension());
		
		windowContent.add(splitPane, SBoxLayout.constraint(SLayout.EXPAND_Y));
		splitPane.setMinimumSize(stendhal.screenSize);
		splitPane.setMaximumSize(new Dimension(stendhal.screenSize.width, Integer.MAX_VALUE));
				
		/*
		 * Handle focus assertion and window closing
		 */
		mainFrame.getMainFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowActivated(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowGainedFocus(final WindowEvent ev) {
				chatText.getPlayerChatText().requestFocus();
			}

			@Override
			public void windowClosing(final WindowEvent e) {
				requestQuit();
			}
		});
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame.getMainFrame().pack();
				/*
				 *  A bit roundabout way to calculate the desired minsize, but
				 *  different java versions seem to take the window decorations
				 *  in account in rather random ways.
				 */
				final int width = mainFrame.getMainFrame().getWidth() - minimap.getComponent().getWidth();
				final int height = mainFrame.getMainFrame().getHeight() - gameLog.getHeight();
				
				mainFrame.getMainFrame().setMinimumSize(new Dimension(width, height));
				mainFrame.getMainFrame().setVisible(true);
				/*
				 * Needed for small screens; Sometimes the divider is placed
				 * incorrectly unless we explicitly set it.
				 */
				splitPane.setDividerLocation(stendhal.screenSize.height + 1);
			}
		});
		directionRelease = null;
	
		// register the slash actions in the client side command line parser
		SlashActionRepository.register();

		checkAndComplainAboutJavaImplementation();
		WorldObjects.addWorldListener(getSoundSystemFacade());
	} // constructor

	private void checkAndComplainAboutJavaImplementation() {
		final String vmName = System.getProperty("java.vm.name", "unknown").toLowerCase();
		if ((vmName.indexOf("hotspot") < 0) && (vmName.indexOf("openjdk") < 0)) {
			final String text = "Stendhal is developed and tested on Sun Java and OpenJDK. You are using " 
				+ System.getProperty("java.vm.vendor", "unknown") + " " 
				+ System.getProperty("java.vm.name", "unknown") 
				+ " so there may be some problems like a black or grey screen.\n"
				+ " If you have coding experience with your JDK, we are looking for help.";
			addEventLine(new HeaderLessEventLine(text, NotificationType.ERROR));
		}
	}

	private void createAndAddOldBag(final GameScreen gameScreen) {
		inventory = new EntityContainer("bag", 3, 4, gameScreen);
		addWindow(inventory);
		settings.add(inventory, "bag", gameScreen);
	}
	
	private void createAndAddNewBag(final Container content) {
		final BagPanelControler bag = new BagPanelControler();
		bag.getComponent().setPreferredSize(new Dimension(200, getHeight()));
		content.add(bag.getComponent(), BorderLayout.EAST);
		
	}

	public void cleanup() {
		chatText.saveCache();
		logger.debug("Exit");
		System.exit(0);
	}

	/**
	 * Add a native in-window dialog to the screen.
	 *
	 * @param comp
	 *            The component to add.
	 */
	public void addDialog(final Component comp) {
		pane.add(comp, JLayeredPane.PALETTE_LAYER);
	}


	public void gameLoop(final GameScreen gameScreen) {
		final int frameLength = (int) (1000.0 / stendhal.FPS_LIMIT);
		int fps = 0;
		final GameObjects gameObjects = client.getGameObjects();
		final StaticGameLayers gameLayers = client.getStaticGameLayers();

		try {
			SoundGroup group = initSoundSystem();
			group.play("harp-1", 0, null, null, false, true);
		} catch (RuntimeException e) {
			logger.error(e, e);
		}
		
		// keep looping until the game ends
		long refreshTime = System.currentTimeMillis();
		long lastFpsTime = refreshTime;
		long lastMessageHandle = refreshTime;

		gameRunning = true;

		boolean canExit = false;
		while (!canExit) {
			try {
				fps++;
				// figure out what time it is right after the screen flip then
				// later we can figure out how long we have been doing redrawing
				// / networking, then we know how long we need to sleep to make
				// the next flip happen at the right time
	
				screen.nextFrame();
				final long now = System.currentTimeMillis();
				final int delta = (int) (now - refreshTime);
				refreshTime = now;
	
				logger.debug("Move objects");
				gameObjects.update(delta);
	
				if (!client.isInBatchUpdate() && gameLayers.isAreaChanged()) {
					/*
					 * Update the screen
					 */
					screen.setMaxWorldSize(gameLayers.getWidth(), gameLayers.getHeight());
					screen.center();
	
					// [Re]create the map
		
					final CollisionDetection cd = gameLayers.getCollisionDetection();
					final CollisionDetection pd = gameLayers.getProtectionDetection();
					
					if (cd != null) {
						minimap.update(cd, pd,
								screen.getGraphicsConfiguration(),
								gameLayers.getArea());
					} 
					gameLayers.resetChangedArea();
				}
	
				final User user = User.get();
	
				if (user != null) {
					// check if the player object has changed.
					// Note: this is an exact object reference check
					if (user != lastuser) {
						character.setPlayer(user);
						keyring.setSlot(user, "keyring", gameScreen);
						inventory.setSlot(user, "bag", gameScreen);
	
						lastuser = user;
					}
				}
	
				if (!client.isInBatchUpdate()) {
					if (mainFrame.getMainFrame().getState() != Frame.ICONIFIED) {
						logger.debug("Draw screen");
						screen.draw();
						minimap.refresh();
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
						final long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
						final long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
	
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
					} catch (final InterruptedException e) {
						logger.error(e, e);
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
					} catch (final Exception e) {
						/*
						 * If we get a timeout exception we accept exit request.
						 */
						canExit = true;
						logger.error(e, e);
					}
				}
			} catch (RuntimeException e) {
				logger.error(e, e);
			}
		}
	
		getSoundSystemFacade().exit();
	}
	private SoundGroup initSoundSystem() {
		SoundGroup group = getSoundSystemFacade().getGroup(SoundLayer.USER_INTERFACE.groupName);
		group.loadSound("harp-1", "audio:/harp-1.ogg", Type.OGG, false);
		group.loadSound("click-4", "audio:/click-4.ogg", Type.OGG, false);
		group.loadSound("click-5", "audio:/click-5.ogg", Type.OGG, false);
		group.loadSound("click-6", "audio:/click-6.ogg", Type.OGG, false);
		group.loadSound("click-8", "audio:/click-8.ogg", Type.OGG, false);
		group.loadSound("click-10", "audio:/click-10.ogg", Type.OGG, false);
		return group;
	}

	/**
	 * Convert a keycode to the corresponding direction.
	 *
	 * @param keyCode
	 *            The keycode.
	 *
	 * @return The direction, or <code>null</code>.
	 */
	protected Direction keyCodeToDirection(final int keyCode) {
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

	protected void onKeyPressed(final KeyEvent e) {
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
			final Direction direction = keyCodeToDirection(e.getKeyCode());

			if (e.isAltGraphDown()) {
				final User user = User.get();

				final EntityView view = screen.getEntityViewAt(user.getX()
						+ direction.getdx(), user.getY() + direction.getdy());

				if (view != null) {
					final IEntity entity = view.getEntity();
					if (!entity.equals(user)) {
						view.onAction();
					}
				}
			}

			processDirectionPress(direction, e.isControlDown());
		}
	}

	protected void onKeyReleased(final KeyEvent e) {
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
	protected void processDirectionPress(final Direction direction, final boolean facing) {
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
	protected void processDirectionRelease(final Direction direction, final boolean facing) {
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
	protected void updateModifiers(final KeyEvent ev) {
		altDown = ev.isAltDown();
		ctrlDown = ev.isControlDown();
		shiftDown = ev.isShiftDown();
	}

	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
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
	public void addWindow(final ManagedWindow mw) {
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
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * Determine if the [Ctrl] key is held down.
	 *
	 * @return Returns <code>true</code> if down.
	 */
	public boolean isCtrlDown() {
		return ctrlDown;
	}

	/**
	 * Determine if the [Shift] key is held down.
	 *
	 * @return Returns <code>true</code> if down.
	 */
	public boolean isShiftDown() {
		return shiftDown;
	}

	//
	// j2DClient
	//

	/**
	 * Add an event line.
	 *
	 */
	public void addEventLine(final EventLine line) {
		gameLog.addLine(line);
	}

	/**
	 * adds a text box on the screen
	 *
	 * @param x  x
	 * @param y  y
	 * @param text text to display
	 * @param type type of text
	 * @param isTalking chat?
	 */
	public void addGameScreenText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		screen.addText(x, y, text, type, isTalking);
	}

	/**
	 * Initiate outfit selection by the user.
	 */
	public void chooseOutfit() {
		int outfit;

		final RPObject player = userContext.getPlayer();

		if (player.has("outfit_org")) {
			outfit = player.getInt("outfit_org");
		} else {
			outfit = player.getInt("outfit");
		}

		// Should really keep only one instance of this around
		final OutfitDialog dialog = new OutfitDialog(mainFrame.getMainFrame(), "Set outfit", outfit);
		dialog.setVisible(true);
	}

	/**
	 * Get the current game screen height.
	 *
	 * @return The height.
	 */
	public int getHeight() {
		return screen.getHeight();
	}

	/**
	 * Get the current game screen width.
	 *
	 * @return The width.
	 */
	public int getWidth() {
		return screen.getWidth();
	}



	/**
	 * Set the input chat line text.
	 *
	 * @param text
	 *            The text.
	 */
	public void setChatLine(final String text) {
		chatText.setChatLine(text);
		
	}
	
	public void clearGameLog() {
		gameLog.clear();
	}

	/**
	 * Set the user's position.
	 *
	 * @param x
	 *            The user's X coordinate.
	 * @param y
	 *            The user's Y coordinate.
	 */
	public void setPosition(final double x, final double y) {
		positionChangeListener.positionChanged(x, y);
	}

	/**
	 * Sets the offline indication state.
	 *
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	public void setOffline(final boolean offline) {
		screen.setOffline(offline);
	}

	//
	//

	protected class GameKeyHandler implements KeyListener {
		public void keyPressed(final KeyEvent e) {
			updateModifiers(e);

			onKeyPressed(e);
		}

		public void keyReleased(final KeyEvent e) {
			updateModifiers(e);
			onKeyReleased(e);
		}

		public void keyTyped(final KeyEvent e) {
			if (e.getKeyChar() == 27) {
				// Escape
				requestQuit();
			}
		}
	}


	protected static class DelayedDirectionRelease {
		/**
		 * The maximum delay between auto-repeat release-press.
		 */
		protected static final long DELAY = 50L;

		protected long expiration;

		protected Direction dir;

		protected boolean facing;

		public DelayedDirectionRelease(final Direction dir, final boolean facing) {
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
		public boolean check(final Direction dir, final boolean facing) {
			if (!this.dir.equals(dir)) {
				return false;
			}

			if (this.facing != facing) {
				return false;
			}

			final long now = System.currentTimeMillis();

			if (now >= expiration) {
				return false;
			}

			expiration = now + DELAY;

			return true;
		}
	}


	public void requestQuit() {
		quitDialog.requestQuit();

	}

	public IPerceptionListener getPerceptionListener() {
		
		return perceptionListener;
	}

	/**
	 * Get the client.
	 *
	 * @return The client.
	 */
	public StendhalClient getClient() {
		return client;
	}
	
	/**
	 * The layered pane where the game screen is does not automatically resize 
	 * the game screen. This handler is needed to do that work.
	 */
	private static class SplitPaneResizeListener implements ComponentListener {
		private Component child;
		private JSplitPane splitPane;
		
		public SplitPaneResizeListener(Component child, JSplitPane splitPane) {
			this.child = child;
			this.splitPane = splitPane;
		}
		
		public void componentHidden(ComponentEvent e) {	}

		public void componentMoved(ComponentEvent e) { 	}

		public void componentResized(ComponentEvent e) {
			Dimension newSize = e.getComponent().getSize();
			if (newSize.height > stendhal.screenSize.height) {
				/*
				 *  There is no proper limit setting for JSplitPane,
				 *  so return the divider to the maximum allowed height
				 *  by force.
				 */
				splitPane.setDividerLocation(stendhal.screenSize.height
						+ splitPane.getInsets().top);
			} else {
				child.setSize(newSize);
			}
		}

		public void componentShown(ComponentEvent e) { 	}
	}


	/**
	 * sets the cursor
	 *
	 * @param cursor Cursor
	 */
	public void setCursor(Cursor cursor) {
		pane.setCursor(cursor);
	}

	/**
	 * gets the sound system
	 *
	 * @return SoundSystemFacade
	 */
	public SoundSystemFacade getSoundSystemFacade() {
		if (soundSystemFacade == null) {
			try {
				soundSystemFacade = new games.stendhal.client.sound.sound.SoundSystemFacadeImpl();
			} catch (RuntimeException e) {
				soundSystemFacade = new NoSoundFacade();
				logger.error(e, e);
				soundSystemFacade = new NoSoundFacade();
				logger.error(e, e);
			}
		}
		return soundSystemFacade;
	}
}
