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
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.PerceptionListenerImpl;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.UserContext;
import games.stendhal.client.WeatherSoundManager;
import games.stendhal.client.World;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.buddies.BuddyPanelController;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chattext.CharacterMap;
import games.stendhal.client.gui.chattext.ChatCompletionHelper;
import games.stendhal.client.gui.chattext.ChatTextController;
import games.stendhal.client.gui.group.GroupPanelController;
import games.stendhal.client.gui.layout.FreePlacementLayout;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.gui.map.MapPanelController;
import games.stendhal.client.gui.spells.Spells;
import games.stendhal.client.gui.stats.StatsPanelController;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.StyleUtil;
import games.stendhal.client.gui.styled.StyledTabbedPaneUI;
import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.listener.PositionChangeMulticaster;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/** The main class that create the screen and starts the arianne client. */
public class j2DClient implements UserInterface {
	static {
		// This is potentially the first loaded GUI component (happens when
		// using web start)
		Initializer.init();
	}
	
	/** Scrolling speed when using the mouse wheel. */
	private static final int SCROLLING_SPEED = 8;
	/** Background color of the private chat tab. Light blue. */
	private static final String PRIVATE_TAB_COLOR = "0xdcdcff";
	/** Property name used to determine if scaling is wanted. */
	private static final String SCALE_PREFERENCE_PROPERTY = "ui.scale_screen";

	/**
	 * A shared [singleton] copy.
	 */
	private static j2DClient sharedUI;

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(j2DClient.class);

	/** Main window. */
	private JFrame frame;
	private final Dimension frameDefaultSize;
	private QuitDialog quitDialog;

	private GameScreen screen;
	private final ScreenController screenController;

	private JLayeredPane pane;

	/** Chat channels. */
	private NotificationChannelManager channelManager;

	private ContainerPanel containerPanel;

	private boolean gameRunning;

	private final ChatTextController chatText = new ChatTextController();

	/** the Character panel. */
	private Character character;

	/** the Key ring panel. */
	private KeyRing keyring;

	/** the minimap panel. */
	private MapPanelController minimap;

	/** the inventory.*/
	private SlotWindow inventory;

	private Spells spells;

	private User lastuser;

	private boolean offline;

	private OutfitDialog outfitDialog;

	private final PositionChangeMulticaster positionChangeListener = new PositionChangeMulticaster();

	private UserContext userContext;

	/** Key handling. */
	private GameKeyHandler gameKeyHandler;

	/**
	 * Get the default UI.
	 * @return  the instance
	 */
	public static j2DClient get() {
		return sharedUI;
	}

	/**
	 * Set the shared [singleton] value.
	 *
	 * @param sharedUI
	 *            The Stendhal UI.
	 */
	private static void setDefault(final j2DClient sharedUI) {
		j2DClient.sharedUI = sharedUI;
		ClientSingletonRepository.setUserInterface(sharedUI);
	}


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
	private StendhalClient client;

	private SoundSystemFacade soundSystemFacade;


	/**
	 * A constructor for JUnit tests.
	 */
	public j2DClient() {
		setDefault(this);
		screenController = null;
		frameDefaultSize = null;
	}

	/**
	 * Create new j2DClient.
	 *
	 * @param client
	 * @param userContext
	 * @param splash splash screen or <code>null</code>. If not
	 *	<code>null</code>, it will be used as the main window
	 */
	public j2DClient(final StendhalClient client, final UserContext userContext,
			JFrame splash) {
		this.client = client;
		this.userContext = userContext;
		setDefault(this);

		final Dimension displaySize = stendhal.getDisplaySize();

		/*
		 * Add a layered pane for the game area, so that we can have
		 * windows on top of it
		 */
		pane = new JLayeredPane();
		pane.setLayout(new FreePlacementLayout());

		/*
		 * Create the main game screen
		 */
		screen = new GameScreen(client);
		screenController = new ScreenController(screen);
		GameScreen.setDefaultScreen(screen);

		// ... and put it on the ground layer of the pane
		pane.add(screen, Component.LEFT_ALIGNMENT, JLayeredPane.DEFAULT_LAYER);

		client.addZoneChangeListener(screen);
		client.addZoneChangeListener(new WeatherSoundManager());
		positionChangeListener.add(screenController);

		/*
		 * Register the slash actions in the client side command line parser.
		 * This needs to be at least before getting the actions to
		 * ChatCompletionHelper.
		 */
		SlashActionRepository.register();
		
		final KeyListener tabcompletion = new ChatCompletionHelper(chatText, World.get().getPlayerList().getNamesList(),
				SlashActionRepository.getCommandNames());
		chatText.addKeyListener(tabcompletion);

		/*
		 * Always redirect focus to chat field
		 */
		screen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				chatText.getPlayerChatText().requestFocus();
			}
		});

		// On Screen windows
		/*
		 * Quit dialog
		 */
		quitDialog = new QuitDialog();
		pane.add(quitDialog.getQuitDialog(), JLayeredPane.MODAL_LAYER);

		/*
		 * Game log
		 */
		final JComponent chatLogArea = createLogArea();
		chatLogArea.setPreferredSize(new Dimension(screen.getWidth(), 171));

		// *** Key handling ***
		gameKeyHandler = new GameKeyHandler(client, screen);
		// add a key input system (defined below) to our canvas so we can
		// respond to key pressed
		chatText.addKeyListener(gameKeyHandler);
		screen.addKeyListener(gameKeyHandler);
		// Also redirect key presses to the chatlog tabs, so that the tabs
		// can be switched with ctrl-PgUp/Down
		chatText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				/*
				 * Redispatch only if CTRL is pressed. Otherwise any arrow key
				 * press will be interpreted as switching log tabs.
				 *
				 * What should be used for Macs?
				 */
				if (e.isControlDown()) {
					chatLogArea.dispatchEvent(e);
					// The log tab contents like stealing the focus if they get
					// key events.
					chatText.getPlayerChatText().requestFocus();
				}
			}
		});

		// Display a hint if this is a debug client
		if (Debug.PRE_RELEASE_VERSION != null) {
			addEventLine(new HeaderLessEventLine("This is a pre release test client: " + Debug.VERSION + " - " + Debug.PRE_RELEASE_VERSION, NotificationType.CLIENT));
		}

		// set some default window positions
		final WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);

		/*
		 * Finally create the window, and place all the components in it
		 */
		frame = MainFrame.prepare(splash);
		JComponent glassPane = DragLayer.get();
		frame.setGlassPane(glassPane);
		glassPane.setVisible(true);

		// *** Create the layout ***
		// left side panel
		final JComponent leftColumn = createLeftPanel();

		// Set maximum size to prevent the entry requesting massive widths, but
		// force expand if there's extra space anyway
		chatText.getPlayerChatText().setMaximumSize(new Dimension(displaySize.width, Integer.MAX_VALUE));
		// Container for chat entry and character map
		JComponent chatEntryBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		chatEntryBox.add(chatText.getPlayerChatText(), SLayout.EXPAND_X);
		
		if (System.getProperty("charmap.emotes") != null) {
			chatEntryBox.add(new CharacterMap(chatText.getPlayerChatText()));
		}
		
		// Chat entry and chat log. The chatlogs are in tabs so they need a
		// patterned background.
		final JComponent chatBox = new JPanel();
		chatBox.setBorder(null);
		chatBox.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		chatBox.add(chatEntryBox, SLayout.EXPAND_X);
		chatBox.add(chatLogArea, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		// Give the user the ability to make the the game area less tall
		final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, chatBox);
		splitPane.setBorder(null);
		// Works for showing the resize, but is extremely flickery
		//splitPane.setContinuousLayout(true);
		// Moving either divider will result in the screen resized. Pass it to
		// the game screen so that it can recenter the player.
		pane.addComponentListener(new SplitPaneResizeListener(screen));

		containerPanel = createContainerPanel();

		// Avoid panel drawing overhead
		final Container windowContent = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		frame.setContentPane(windowContent);

		// Finally add the left pane, and the games screen + chat combo
		// Make the panel take any horizontal resize
		leftColumn.setMinimumSize(new Dimension());
		/*
		 * Fix the container panel size, so that it is always visible
		 */
		containerPanel.setMinimumSize(containerPanel.getPreferredSize());
		
		// Splitter between the left column and game screen
		final JSplitPane horizSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftColumn, splitPane);

		// Ensure that the limits are obeyed even when the component is resized
		horizSplit.addComponentListener(new ComponentAdapter() {
			// Start with a large value, so that the divider is placed as left
			// as possible
			private int oldWidth = Integer.MAX_VALUE;
			
			@Override
			public void componentResized(ComponentEvent e) {
				if (screen.isScaled()) {
					/*
					 * Default behavior is otherwise reasonable, except the
					 * user will likely want to use the vertical space for the
					 * game screen.
					 * 
					 * Try to keep the aspect ratio near the optimum; the sizes
					 * have not changed when this gets called, so push it to the
					 * EDT.
					 */
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							double hScale = screen.getWidth() / (double) displaySize.width;
							int preferredLocation = (int) (hScale * displaySize.height);
							splitPane.setDividerLocation(preferredLocation);
						}
					});
				} else {
					int position = horizSplit.getDividerLocation();
					/*
					 * The trouble: the size of the game screen is likely the one
					 * that the player wants to preserve when making the window
					 * smaller. Swing provides no default way to the old component
					 * size, so we stash the interesting dimension in oldWidth. 
					 */
					int width = horizSplit.getWidth();
					int oldRightDiff = oldWidth - position;
					int widthChange = width - oldWidth;
					int underflow = widthChange + position;
					if (underflow < 0) {
						/*
						 * Extreme size reduction. The divider location would have
						 * changed as the result. Use the previous location instead
						 * of the current.
						 */
						oldRightDiff = oldWidth - horizSplit.getLastDividerLocation();
					}
					position = width - oldRightDiff;

					position = Math.min(position, horizSplit.getMaximumDividerLocation());
					position = Math.max(position, horizSplit.getMinimumDividerLocation());

					horizSplit.setDividerLocation(position);
					oldWidth = horizSplit.getWidth();
				}
			}
		});
		/** Used as a workaround for BasicSplitPaneUI bugs */
		final int divWidth = splitPane.getDividerSize();
		
		pane.setPreferredSize(new Dimension(displaySize.width + divWidth, displaySize.height));
		horizSplit.setBorder(null);
		
		windowContent.add(horizSplit, SBoxLayout.constraint(SLayout.EXPAND_Y, SLayout.EXPAND_X));
		
		// The contents of the right side
		JComponent rightSidePanel = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		JComponent settings = new SettingsPanel();
		rightSidePanel.add(settings, SLayout.EXPAND_X);
		rightSidePanel.add(containerPanel, SBoxLayout.constraint(SLayout.EXPAND_Y, SLayout.EXPAND_X));
		windowContent.add(rightSidePanel, SLayout.EXPAND_Y);
		
		windowManager.registerSettingChangeListener(SCALE_PREFERENCE_PROPERTY,
				new SettingChangeAdapter(SCALE_PREFERENCE_PROPERTY, "true") {
			@Override
			public void changed(String newValue) {
				boolean scale = Boolean.parseBoolean(newValue);
				screen.setUseScaling(scale);
				if (scale) {
					// Clear the resize limits
					splitPane.setMaximumSize(null);
					pane.setMaximumSize(null);
				} else {
					// Set the limits
					splitPane.setMaximumSize(new Dimension(displaySize.width + divWidth, Integer.MAX_VALUE));
					pane.setMaximumSize(displaySize);
					// The user may have resized the screen outside allowed
					// parameters
					int overflow = horizSplit.getWidth() - horizSplit.getDividerLocation() - displaySize.width - divWidth;
					if (overflow > 0) {
						horizSplit.setDividerLocation(horizSplit.getDividerLocation() + overflow);
					}
					if (splitPane.getDividerLocation() > displaySize.height) {
						splitPane.setDividerLocation(displaySize.height);
					}
				}
			}
		});
		
		/*
		 * Handle focus assertion and window closing
		 */
		frame.addWindowListener(new WindowAdapter() {
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

		frame.pack();
		horizSplit.setDividerLocation(leftColumn.getPreferredSize().width);
		setInitialWindowStates();

		/*
		 *  A bit roundabout way to calculate the desired minsize, but
		 *  different java versions seem to take the window decorations
		 *  in account in rather random ways.
		 */
		final int width = frame.getWidth()
				- minimap.getComponent().getWidth() - containerPanel.getWidth();
		final int height = frame.getHeight() - chatLogArea.getHeight();

		frame.setMinimumSize(new Dimension(width, height));
		frame.setVisible(true);

		/*
		 * For small screens. Setting the maximum window size does
		 * not help - pack() happily ignores it.
		 */
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension current = frame.getSize();
		frame.setSize(Math.min(current.width, maxBounds.width),
				Math.min(current.height, maxBounds.height));
		
		/*
		 * Used by settings dialog to restore the client's dimensions back to
		 * the original width and height. Needs to be called after
		 * frame.setSize().
		 */
		frameDefaultSize = frame.getSize();
		
		/*
		 * Needed for small screens; Sometimes the divider is placed
		 * incorrectly unless we explicitly set it. Try to fit it on the
		 * screen and show a bit of the chat.
		 */
		splitPane.setDividerLocation(Math.min(displaySize.height,
				maxBounds.height  - 80));

		checkAndComplainAboutJavaImplementation();
		positionChangeListener.add(getSoundSystemFacade());
		WindowUtils.watchFontSize(frame);
		
		/*
		 * On some systems the window may end up occasionally unresponsive
		 * to keyboard use unless these are delayed.
		 */
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				/*
				 * A massive kludge to ensure that the window position is
				 * treated properly. Without this popup menus can be misplaced
				 * and unusable until the user moves the game window. This
				 * can happen with certain window managers if the window manager
				 * moves the window as a result of resizing the window.
				 * 
				 * Description of the bug:
				 * 	https://bugzilla.redhat.com/show_bug.cgi?id=698295
				 * 
				 * As of 2013-09-07 it is reproducible at least when using
				 * Mate desktop's marco window manager. Metacity and mutter
				 * have a workaround for the same issue in AWT.
				 */
				Point location = frame.getLocation();
				frame.setLocation(location.x + 1, location.y);
				frame.setLocation(location.x, location.y);
				
				// The keyboard fix mentioned above
				frame.setEnabled(true);
				chatText.getPlayerChatText().requestFocus();
			}
		});
		
		/* Restore client's window dimensions from config (set by previous
		 * session) if available. Call after ???.
		 */
		restorePrevSessionSize();
	} // constructor

	/**
	 * Create the left side panel of the client.
	 *
	 * @return A component containing the components left of the game screen
	 */
	private JComponent createLeftPanel() {
		minimap = new MapPanelController(client);
		positionChangeListener.add(minimap);
		final StatsPanelController stats = StatsPanelController.get();
		final BuddyPanelController buddies = BuddyPanelController.get();
		ScrolledViewport buddyScroll = new ScrolledViewport((JComponent) buddies.getComponent());
		buddyScroll.setScrollingSpeed(SCROLLING_SPEED);
		final JComponent buddyPane = buddyScroll.getComponent();
		buddyPane.setBorder(null);

		final JComponent leftColumn = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		leftColumn.add(minimap.getComponent(), SLayout.EXPAND_X);
		leftColumn.add(stats.getComponent(), SLayout.EXPAND_X);

		// Add a background for the tabs. The column itself has none.
		JPanel tabBackground = new JPanel();
		tabBackground.setBorder(null);
		tabBackground.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		// Adjust the Tab Width, if we can. The default is pretty if there's
		// space, but in the column there are no pixels to waste.
		TabbedPaneUI ui = tabs.getUI();
		if (ui instanceof StyledTabbedPaneUI) {
			((StyledTabbedPaneUI) ui).setTabLabelMargins(1);
		}
		tabs.setFocusable(false);
		tabs.add("Friends", buddyPane);

		tabs.add("Group", GroupPanelController.get().getComponent());

		tabBackground.add(tabs, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));
		leftColumn.add(tabBackground, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		return leftColumn;
	}

	/**
	 * Create the container panel (right side panel), and its child components.
	 *
	 * @return container panel
	 */
	private ContainerPanel createContainerPanel() {
		ContainerPanel containerPanel = new ContainerPanel();
		containerPanel.setMinimumSize(new Dimension(0, 0));

		/*
		 * Contents of the containerPanel
		 */
		// Character window
		character = new Character();
		containerPanel.addRepaintable(character);

		// Create the bag window
		inventory = new SlotWindow("bag", 3, 4);
		inventory.setAcceptedTypes(EntityMap.getClass("item", null, null));
		inventory.setCloseable(false);
		containerPanel.addRepaintable(inventory);

		keyring = new KeyRing();
		// keyring's types are more limited, but it's simpler to let the server
		// handle those
		keyring.setAcceptedTypes(EntityMap.getClass("item", null, null));
		containerPanel.addRepaintable(keyring);
		userContext.addFeatureChangeListener(keyring);

		spells = new Spells();
		spells.setAcceptedTypes(EntityMap.getClass("spell", null, null));
		containerPanel.addRepaintable(spells);
		userContext.addFeatureChangeListener(spells);

		return containerPanel;
	}

	/**
	 * Modify the states of the on screen windows. The window manager normally
	 * restores the state of the window as it was on the previous session. For
	 * some windows this is not desirable.
	 * <p>
	 * <em>Note:</em> This need to be called from the event dispatch thread.
	 */
	private void setInitialWindowStates() {
		/*
		 * Window manager may try to restore the visibility of the dialog when
		 * it's added to the pane.
		 */
		quitDialog.getQuitDialog().setVisible(false);
		// Windows may have been closed in old clients
		character.setVisible(true);
		inventory.setVisible(true);
		/*
		 * Keyring, on the other hand, *should* be hidden until revealed
		 * by feature change
		 */
		keyring.setVisible(false);

		// spells should also be invisible until revealed by a feature change
		spells.setVisible(false);
	}

	/**
	 * Check the used java version, and show a warning if it's not known to be
	 * a compatible one.
	 */
	private void checkAndComplainAboutJavaImplementation() {
		final String vmName = System.getProperty("java.vm.name", "unknown").toLowerCase(Locale.ENGLISH);
		if ((vmName.indexOf("hotspot") < 0) && (vmName.indexOf("openjdk") < 0)) {
			final String text = "Stendhal is developed and tested on Sun Java and OpenJDK. You are using "
				+ System.getProperty("java.vm.vendor", "unknown") + " "
				+ System.getProperty("java.vm.name", "unknown")
				+ " so there may be some problems like a black or grey screen.\n"
				+ " If you have coding experience with your JDK, we are looking for help.";
			addEventLine(new HeaderLessEventLine(text, NotificationType.ERROR));
		}
	}

	/**
	 * Called at quit.
	 */
	private void cleanup() {
		chatText.saveCache();
		
		// Fall back in case sound system hangs. Can happen at least when using
		// the pulseaudio driver and the sound daemon is shut down while the
		// client has the line open.
		Runnable quit = new Runnable() {
			@Override
			public void run() {
				logger.warn("Forced exit, sound system likely locked up");
				System.exit(1);
			}
		};
		Executors.newSingleThreadScheduledExecutor().schedule(quit, 3, TimeUnit.SECONDS);
		getSoundSystemFacade().exit();
		
		// Normal shutdown
		logger.debug("Exit");
		System.exit(0);
	}

	/**
	 * Add a native in-window dialog to the screen.
	 *
	 * @param comp
	 *            The component to add.
	 */
	private void addDialog(final Component comp) {
		pane.add(comp, JLayeredPane.PALETTE_LAYER);
	}

	/**
	 * Start the game loop thread.
	 */
	public void startGameLoop() {
		try {
			SoundGroup group = initSoundSystem();
			group.play("harp-1", 0, null, null, false, true);
		} catch (RuntimeException e) {
			logger.error(e, e);
		}

		GameLoop loop = GameLoop.get();
		final GameObjects gameObjects = client.getGameObjects();
		final StaticGameLayers gameLayers = client.getStaticGameLayers();

		loop.runAllways(new GameLoop.PersistentTask() {
			@Override
			public void run(int delta) {
				gameLoop(delta, gameLayers, gameObjects);
			}
		});

		loop.runAtQuit(new Runnable() {
			@Override
			public void run() {
				cleanup();
			}
		});

		gameRunning = true;

		loop.start();
	}

	/**
	 * Main game loop contents. Updates objects, and requests redraws.
	 *
	 * @param delta difference to previous calling time
	 * @param gameLayers
	 * @param gameObjects
	 */
	private void gameLoop(final int delta, final StaticGameLayers gameLayers,
			final GameObjects gameObjects) {
		// Check logouts first, in case something goes wrong with the drawing
		// code

		if (!gameRunning) {
			logger.info("Request logout");
			try {
				/*
				 * We request server permision to logout. Server can deny
				 * it, unless we are already offline.
				 */
				if (offline || client.logout()) {
					GameLoop.get().stop();
				} else {
					logger.warn("You can't logout now.");
					gameRunning = true;
				}
			} catch (final Exception e) { // catch InvalidVersionException, TimeoutException and BannedAddressException
				/*
				 * If we get a timeout exception we accept exit request.
				 */
				logger.error(e, e);
				GameLoop.get().stop();
			}
		}

		// Shows a offline icon if the connection is broken
		setOffline(!client.getConnectionState());

		// figure out what time it is right after the screen flip then
		// later we can figure out how long we have been doing redrawing
		// / networking, then we know how long we need to sleep to make
		// the next flip happen at the right time
		screenController.nextFrame();

		logger.debug("Move objects");
		gameObjects.update(delta);

		if (gameLayers.isAreaChanged()) {
			// Same thread as the ClientFramework loop, so these should
			// be save
			/*
			 * Update the screen
			 */
			screenController.setWorldSize(gameLayers.getWidth(), gameLayers.getHeight());

			// [Re]create the map.

			final CollisionDetection cd = gameLayers.getCollisionDetection();
			final CollisionDetection pd = gameLayers.getProtectionDetection();

			if (cd != null) {
				minimap.update(cd, pd, gameLayers.getReadableName(), gameLayers.getDangerLevel());
			}
			gameLayers.resetChangedArea();
		}

		final User user = User.get();

		// check if the player object has changed.
		// Note: this is an exact object reference check
		if ((user != null) && (user != lastuser)) {
			character.setPlayer(user);
			keyring.setSlot(user, "keyring");
			spells.setSlot(user, "spells");
			inventory.setSlot(user, "bag");
			lastuser = user;
		}

		triggerPainting();

		logger.debug("Query network");

		client.loop(0);

		gameKeyHandler.processDelayedDirectionRelease();
	}

	private int paintCounter;
	
	/**
	 * Requests repaint at the window areas that are painted according to the
	 * game loop frame rate.
	 */
	private void triggerPainting() {
		if (frame.getState() != Frame.ICONIFIED) {
			paintCounter++;
			if (frame.isActive() || System.getProperty("stendhal.skip.inactive", "false").equals("false") || paintCounter >= 20) {
				paintCounter = 0;
				logger.debug("Draw screen");
				minimap.refresh();
				containerPanel.repaintChildren();
				screen.repaint();
			}
		}
    }

	/**
	 * Initialize the sounds used by the user interfase.
	 * 
	 * @return user interface sound group
	 */
	private SoundGroup initSoundSystem() {
		SoundGroup group = getSoundSystemFacade().getGroup(SoundLayer.USER_INTERFACE.groupName);
		group.loadSound("harp-1", "harp-1.ogg", SoundFileType.OGG, false);
		group.loadSound("click-4", "click-4.ogg", SoundFileType.OGG, false);
		group.loadSound("click-5", "click-5.ogg", SoundFileType.OGG, false);
		group.loadSound("click-6", "click-6.ogg", SoundFileType.OGG, false);
		group.loadSound("click-8", "click-8.ogg", SoundFileType.OGG, false);
		group.loadSound("click-10", "click-10.ogg", SoundFileType.OGG, false);
		return group;
	}

	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
	void shutdown() {
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
	 * @param mw A managed window.
	 */
	public void addWindow(final ManagedWindow mw) {
		if (mw instanceof InternalManagedWindow) {
			addDialog((InternalManagedWindow) mw);
		} else {
			throw new IllegalArgumentException("Unsupport ManagedWindow type: "
					+ mw.getClass().getName());
		}
	}

	//
	// j2DClient
	//

	@Override
	public void addEventLine(final EventLine line) {
		channelManager.addEventLine(line);
	}

	@Override
	public void addGameScreenText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		screenController.addText(x, y, text, type, isTalking);
	}

	@Override
	public void addAchievementBox(String title, String description, String category) {
		screen.addAchievementBox(title, description, category);
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

		if (outfitDialog == null) {
			// Here we actually want to call new OutfitColor(). Modifying
			// OutfitColor.PLAIN would be a bad thing.
			outfitDialog = new OutfitDialog(frame, "Set outfit", outfit,
					new OutfitColor(player));
			outfitDialog.setVisible(true);
		} else {
			outfitDialog.setState(outfit, OutfitColor.get(player));
			outfitDialog.setVisible(true);
			outfitDialog.toFront();
		}
	}

	/**
	 * Create the chat log tabs.
	 *
	 * @return chat log area
	 */
	private JComponent createLogArea() {
		final JTabbedPane tabs = new JTabbedPane(JTabbedPane.BOTTOM);
		final Timer animator = new Timer(100, null);
		List<JComponent> logs = createNotificationChannels();
		final BitSet changedChannels = new BitSet(logs.size());
		
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = tabs.getSelectedIndex();
				NotificationChannel channel = channelManager.getChannels().get(i);
				channelManager.setVisibleChannel(channel);
				if (changedChannels.get(i)) {
					changedChannels.set(i, false);
					// Remove modified marker
					tabs.setBackgroundAt(i, null);
					if (changedChannels.isEmpty()) {
						animator.stop();
					}
				}
			}
		});
		
		Iterator<NotificationChannel> it = channelManager.getChannels().iterator();
		for (JComponent tab : logs) {
			tabs.add(it.next().getName(), tab);
		}
		channelManager.addHiddenChannelListener(new NotificationChannelManager.HiddenChannelListener() {
			@Override
			public void channelModified(int index) {
				// Mark the tab as modified so that the user can see there's
				// new text
				if (!changedChannels.get(index)) {
					changedChannels.set(index);
					if (!animator.isRunning()) {
						animator.start();
					}
				}
			}
		});
		
		animator.addActionListener(new ActionListener() {
			private static final int STEPS = 10;
			private final Color[] colors;
			private int colorIndex;
			private int change = 1;
			{
				colors = new Color[STEPS];
				Color endColor;
				
				Style style = StyleUtil.getStyle();
				if (style != null) {
					colors[0] = style.getHighLightColor();
					endColor = style.getPlainColor();
				} else {
					colors[0] = Color.BLUE;
					endColor = Color.DARK_GRAY;
				}
				
				int r = colors[0].getRed();
				int g = colors[0].getGreen();
				int b = colors[0].getBlue();
				int alpha = 0xff;
				int dR = r - endColor.getRed();
				int dG = g - endColor.getGreen();
				int dB = b - endColor.getBlue();
				int dA;
				if (TransparencyMode.TRANSPARENCY == Transparency.TRANSLUCENT) {
					dA = 0xff / STEPS;
				} else {
					dA = 0;
				}
				for (int i = 1; i < STEPS; i++) {
					alpha -= dA;
					colors[i] = new Color(r - i * dR / STEPS, g - i * dG / STEPS, b - i * dB / STEPS, alpha);
				}
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorIndex += change;
				if (colorIndex >= colors.length || colorIndex < 0) {
					change = -change;
					colorIndex += change;
				}

				for (int i = changedChannels.nextSetBit(0); i >= 0; i = changedChannels.nextSetBit(i + 1)) {
					tabs.setBackgroundAt(i, colors[colorIndex]);
				}
			}
		});

		return tabs;
	}

	/**
	 * Create chat channels.
	 *
	 * @return Chat log components of the notification channels
	 */
	private List<JComponent> createNotificationChannels() {
		List<JComponent> list = new ArrayList<JComponent>();
		channelManager = new NotificationChannelManager();
		KTextEdit edit = new KTextEdit();
		list.add(edit);

		// ** Main channel **
		// Follow settings changes for the main channel
		WtWindowManager wm = WtWindowManager.getInstance();
		final NotificationChannel mainChannel = new NotificationChannel("Main", edit, true, "");
		wm.registerSettingChangeListener("ui.healingmessage", new SettingChangeAdapter("ui.healingmessage", "false") {
			@Override
			public void changed(String newValue) {
				mainChannel.setTypeFiltering(NotificationType.HEAL, Boolean.parseBoolean(newValue));
			}
		});
		wm.registerSettingChangeListener("ui.poisonmessage", new SettingChangeAdapter("ui.poisonmessage", "false") {
			@Override
			public void changed(String newValue) {
				mainChannel.setTypeFiltering(NotificationType.POISON, Boolean.parseBoolean(newValue));
			}
		});

		channelManager.addChannel(mainChannel);

		// ** Private channel **
		edit = new KTextEdit();
		edit.setChannelName("Personal");
		/*
		 * Give it a different background color to make it different from the
		 * main chat log.
		 */
		edit.setDefaultBackground(Color.decode(PRIVATE_TAB_COLOR));
		list.add(edit);
		/*
		 * Types shown by default in the private/group tab. Admin messages
		 * should occur everywhere, of course, and not be possible to be
		 * disabled in preferences.
		 */
		String personalDefault = NotificationType.PRIVMSG.toString() + ","
				+ NotificationType.CLIENT + "," + NotificationType.GROUP + ","
				+ NotificationType.TUTORIAL + "," + NotificationType.SUPPORT;
		channelManager.addChannel(new NotificationChannel("Personal", edit, false, personalDefault));

		return list;
	}

	/**
	 * Get the main window component.
	 *
	 * @return main window
	 */
	public Frame getMainFrame() {
		return frame;
	}
	
	/**
	 * Gets the default width and height of the client defined upon
	 * frame (JFrame) construction.
	 * 
	 * @return
	 *         Default dimension of client
	 */
	public Dimension getFrameDefaultSize() {
		return frameDefaultSize;
	}
	
	/**
	 * Sets the window's width and height from config.
	 * 
	 * @return
	 *       Values for width and height are available from previous session.
	 */
	public void restorePrevSessionSize() {
		// Get instance of WtWindowManager
		WtWindowManager windowManager = WtWindowManager.getInstance();
		
		/*
		 * Now restore dimensions from last session.
		 */
		Integer uiWidth = windowManager.getPropertyInt("ui.dimensions.width", frameDefaultSize.width);
		Integer uiHeight = windowManager.getPropertyInt("ui.dimensions.height", frameDefaultSize.height);
		if ((uiWidth != null) && uiHeight != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Setting window size from config.");
			}
			
			frame.setSize(uiWidth, uiHeight);
		}
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

	/**
	 * Clear the visible channel log.
	 */
	public void clearGameLog() {
		channelManager.getVisibleChannel().clear();
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
		screenController.setOffline(offline);
		this.offline = offline;
	}

	/**
	 * Called when the user presses ESC, or tries to close the main game window.
	 */
	public void requestQuit() {
		if (client.getConnectionState() || !offline) {
			quitDialog.requestQuit();
		} else {
			System.exit(0);
		}
	}

	/**
	 * PerceptionListener for the game window.
	 * 
	 * @return listener
	 */
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
	private static class SplitPaneResizeListener extends ComponentAdapter {
		private final Component child;

		/**
		 * Create a SplitPaneResizeListener.
		 * 
		 * @param child the component that needs to be resized
		 */
		public SplitPaneResizeListener(Component child) {
			this.child = child;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// Pass on resize event
			child.setSize(e.getComponent().getSize());
		}
	}

	@Override
	public final SoundSystemFacade getSoundSystemFacade() {
		if (soundSystemFacade == null) {
			try {
				if ((DataLoader.getResource("data/sound/harp-1.ogg") != null)
						|| (DataLoader.getResource("data/music/the_old_tavern.ogg") != null)) {
					soundSystemFacade = new games.stendhal.client.sound.sound.SoundSystemFacadeImpl();
				} else {
					soundSystemFacade = new NoSoundFacade();
				}
			} catch (RuntimeException e) {
				soundSystemFacade = new NoSoundFacade();
				logger.error(e, e);
			}
		}
		return soundSystemFacade;
	}

	public void switchToSpellState(RPObject spell) {
		this.screen.switchToSpellCastingState(spell);
	}
}
