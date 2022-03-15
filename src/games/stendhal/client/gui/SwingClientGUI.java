/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e.V.                  *
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

import static games.stendhal.common.constants.Actions.COND_STOP;
import static games.stendhal.common.constants.Actions.TYPE;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;

import org.apache.log4j.Logger;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalClient.ZoneChangeListener;
import games.stendhal.client.UserContext;
import games.stendhal.client.WeatherSoundManager;
import games.stendhal.client.World;
import games.stendhal.client.Zone;
import games.stendhal.client.stendhal;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.buddies.BuddyPanelController;
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
import games.stendhal.client.gui.styled.StyledTabbedPaneUI;
import games.stendhal.client.gui.wt.core.SettingChangeListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Testing;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

class SwingClientGUI implements J2DClientGUI {
	/** Scrolling speed when using the mouse wheel. */
	private static final int SCROLLING_SPEED = 8;
	/** Property name used to determine if scaling is wanted. */
	private static final String SCALE_PREFERENCE_PROPERTY = "ui.scale_screen";
	private static final Logger logger = Logger.getLogger(SwingClientGUI.class);

	private final JLayeredPane pane;
	private final GameScreen screen;
	private final ScreenController screenController;
	private final ContainerPanel containerPanel;
	private final QuitDialog quitDialog;
	private final UserContext userContext;
	private final ChatTextController chatText = new ChatTextController();
	private MapPanelController minimap;
	private JSplitPane verticalSplit;
	private final JFrame frame;
	private final JComponent chatLogArea;
	private final JComponent leftColumn;
	private JSplitPane horizontalSplit;
	private final Dimension frameDefaultSize;

	/** the Character panel. */
	private Character character;
	/** the inventory. */
	private Bag inventory;
	/** the Key ring panel. */
	private KeyRing keyring;
	//private Portfolio portfolio;
	private Spells spells;
	private boolean offline;
	private int paintCounter;
	private User user;
	private GameKeyHandler gameKeyHandler;
	private OutfitDialog outfitDialog;

	public SwingClientGUI(StendhalClient client, UserContext context,
			NotificationChannelManager channelManager, JFrame splash) {
		this.userContext = context;
		setupInternalWindowProperties();
		/*
		 * Add a layered pane for the game area, so that we can have
		 * windows on top of it
		 */
		pane = new JLayeredPane();
		pane.setLayout(new FreePlacementLayout());

		// Create the main game screen
		screen = GameScreen.get(client);
		GameScreen.setDefaultScreen(screen);
		// initialize the screen controller
		screenController = ScreenController.get(screen);
		pane.addComponentListener(new GameScreenResizer(screen));

		// ... and put it on the ground layer of the pane
		pane.add(screen, Component.LEFT_ALIGNMENT, JLayeredPane.DEFAULT_LAYER);

		quitDialog = new QuitDialog();
		pane.add(quitDialog.getQuitDialog(), JLayeredPane.MODAL_LAYER);

		setupChatEntry();
		chatLogArea = createChatLog(channelManager);
		containerPanel = createContainerPanel();
		leftColumn = createLeftPanel(client);
		frame = prepareMainWindow(splash);

		setupChatText();

		setupZoneChangeListeners(client);
		setupOverallLayout();

		int divWidth = verticalSplit.getDividerSize();
		WtWindowManager.getInstance().registerSettingChangeListener(SCALE_PREFERENCE_PROPERTY,
				new ScalingSettingChangeListener(divWidth));

		setInitialWindowStates();
		frame.setVisible(true);

		/*
		 * Used by settings dialog to restore the client's dimensions back to
		 * the original width and height. Needs to be called after
		 * frame.setSize().
		 */
		frameDefaultSize = frame.getSize();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				requestQuit(client);
			}
		});

		setupKeyHandling(client);

		locationHacksAndBugWorkaround();
		WindowUtils.restoreSize(frame);
	}

	private void setupInternalWindowProperties() {
		WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);
	}

	private void setupChatEntry() {
		final KeyListener tabcompletion = new ChatCompletionHelper(chatText,
				World.get().getPlayerList().getNamesList(),
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
	}

	private void setupKeyHandling(StendhalClient client) {
		gameKeyHandler = new GameKeyHandler(client, screen);
		chatText.addKeyListener(gameKeyHandler);
		screen.addKeyListener(gameKeyHandler);
	}

	private JComponent createChatLog(NotificationChannelManager channelManager) {
		JComponent chatLogArea = new ChatLogArea(channelManager).getComponent();
		chatLogArea.setPreferredSize(new Dimension(screen.getWidth(), 171));
		// Bind the tab changing keys of the chat log to global key map
		InputMap input = chatLogArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		input.put(KeyStroke.getKeyStroke("control PAGE_UP"), "navigatePrevious");
		input.put(KeyStroke.getKeyStroke("control PAGE_DOWN"), "navigateNext");

		return chatLogArea;
	}

	/**
	 * Create the container panel (right side panel), and its child components.
	 *
	 * @return container panel
	 */
	private ContainerPanel createContainerPanel() {
		ContainerPanel containerPanel = new ContainerPanel();
		containerPanel.setAnimated(false);
		containerPanel.setMinimumSize(new Dimension(0, 0));

		/*
		 * Contents of the containerPanel
		 */
		// Character window
		character = new Character();
		containerPanel.addRepaintable(character);

		// Create the bag window
		inventory = new Bag();
		inventory.setAcceptedTypes(EntityMap.getClass("item", null, null));
		containerPanel.addRepaintable(inventory);
		userContext.addFeatureChangeListener(inventory);

		keyring = new KeyRing();
		// keyring's types are more limited, but it's simpler to let the server
		// handle those
		keyring.setAcceptedTypes(EntityMap.getClass("item", null, null));
		containerPanel.addRepaintable(keyring);
		userContext.addFeatureChangeListener(keyring);

		/*
		portfolio = new Portfolio();
		portfolio.setAcceptedTypes(EntityMap.getClass("item", null, null));
		containerPanel.addRepaintable(portfolio);
		userContext.addFeatureChangeListener(portfolio);
		*/

		spells = new Spells();
		spells.setAcceptedTypes(EntityMap.getClass("spell", null, null));
		containerPanel.addRepaintable(spells);
		userContext.addFeatureChangeListener(spells);

		for (final FeatureChangeListener listener: character.getFeatureChangeListeners()) {
			userContext.addFeatureChangeListener(listener);
		}
		for (final ComponentListener listener: character.getComponentListeners()) {
			containerPanel.addComponentListener(listener);
		}

		return containerPanel;
	}

	/**
	 * Create the left side panel of the client.
	 *
	 * @return A component containing the components left of the game screen
	 */
	private JComponent createLeftPanel(StendhalClient client) {
		minimap = new MapPanelController(client);
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
		JTabbedPane tabs = new JTabbedPane(SwingConstants.BOTTOM);
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

	private JFrame prepareMainWindow(JFrame splash) {
		JFrame frame = MainFrame.prepare(splash);
		JComponent glassPane = DragLayer.get();
		frame.setGlassPane(glassPane);
		glassPane.setVisible(true);
		setupWindowWideListeners(frame);
		WindowUtils.watchFontSize(frame);

		return frame;
	}

	private void setupChatText() {
		Dimension displaySize = stendhal.getDisplaySize();
		chatText.getPlayerChatText().setMaximumSize(new Dimension(displaySize.width, Integer.MAX_VALUE));
		GameLoop.get().runAtQuit(chatText::saveCache);
	}

	@Override
	public void requestQuit(StendhalClient client) {
		if (client.getConnectionState() || !offline) {
			quitDialog.requestQuit(user);
		} else {
			System.exit(0);
		}
	}

	@Override
	public void setOffline(final boolean offline) {
		screenController.setOffline(offline);
		this.offline = offline;
	}

	/**
	 * Requests repaint at the window areas that are painted according to the
	 * game loop frame rate.
	 */
	@Override
	public void triggerPainting() {
		if (frame.getState() != Frame.ICONIFIED) {
			paintCounter++;
			if (frame.isActive() || "false".equals(System.getProperty("stendhal.skip.inactive", "false")) || paintCounter >= 20) {
				paintCounter = 0;
				logger.debug("Draw screen");
				minimap.refresh();
				containerPanel.repaintChildren();
				screen.repaint();
			}
		}
    }

	private void locationHacksAndBugWorkaround() {
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
				 * "ui.dimensions"
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
	}

	/**
	 * For small screens. Setting the maximum window size does
	 * not help - pack() happily ignores it.
	 */
	private void smallScreenHacks() {
		Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Dimension current = frame.getSize();
		frame.setSize(Math.min(current.width, maxBounds.width),
				Math.min(current.height, maxBounds.height));
		/*
		 * Needed for small screens; Sometimes the divider is placed
		 * incorrectly unless we explicitly set it. Try to fit it on the
		 * screen and show a bit of the chat.
		 */
		verticalSplit.setDividerLocation(Math.min(stendhal.getDisplaySize().height,
				maxBounds.height  - 80));
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
		 * Keyring and spells, on the other hand, *should* be hidden until
		 * revealed by feature change
		 */
		keyring.setVisible(false);
		//portfolio.setVisible(false);
		spells.setVisible(false);
	}

	private void setupWindowWideListeners(JFrame frame) {
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
		});
		frame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowLostFocus(WindowEvent e) {
				/* Stops player movement via keypress when focus is lost.
				 *
				 * FIXME: When focus is regained, direction key must be
				 *        pressed twice to resume walking. Key states
				 *        not flushed correctly?
				 */
				if (StendhalClient.serverVersionAtLeast("1.27.5")) {
					final RPAction stop = new RPAction();
					stop.put(TYPE, COND_STOP);
					ClientSingletonRepository.getClientFramework().send(stop);
					// Clear any direction keypresses
					gameKeyHandler.flushDirectionKeys();
				}
			}
		});
	}

	private void setupOverallLayout() {
		Dimension displaySize = stendhal.getDisplaySize();
		Container windowContent = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		frame.setContentPane(windowContent);

		// Set maximum size to prevent the entry requesting massive widths, but
		// force expand if there's extra space anyway
		chatText.getPlayerChatText().setMaximumSize(new Dimension(displaySize.width, Integer.MAX_VALUE));
		JComponent chatEntryBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		chatEntryBox.add(chatText.getPlayerChatText(), SLayout.EXPAND_X);

		if (Testing.CHAT) {
			chatEntryBox.add(new CharacterMap(chatText.getPlayerChatText()));
		}
		final JComponent chatBox = new JPanel();
		chatBox.setBorder(null);
		chatBox.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		chatBox.add(chatEntryBox, SLayout.EXPAND_X);
		chatBox.add(chatLogArea, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pane, chatBox);
		verticalSplit.setBorder(null);

		/*
		 * Fix the container panel size, so that it is always visible
		 */
		containerPanel.setMinimumSize(containerPanel.getPreferredSize());

		leftColumn.setMinimumSize(new Dimension());
		// Splitter between the left column and game screen
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftColumn, verticalSplit);
		// Ensure that the limits are obeyed even when the component is resized
		split.addComponentListener(new HorizontalSplitListener(displaySize, split));

		horizontalSplit = split;
		int divWidth = verticalSplit.getDividerSize();
		pane.setPreferredSize(new Dimension(displaySize.width + divWidth, displaySize.height));
		horizontalSplit.setBorder(null);
		windowContent.add(horizontalSplit, SBoxLayout.constraint(SLayout.EXPAND_Y, SLayout.EXPAND_X));

		JComponent rightSidePanel = SBoxLayout.createContainer(SBoxLayout.VERTICAL);
		JComponent settings = new SettingsPanel();
		rightSidePanel.add(settings, SLayout.EXPAND_X);
		rightSidePanel.add(containerPanel, SBoxLayout.constraint(SLayout.EXPAND_Y, SLayout.EXPAND_X));
		windowContent.add(rightSidePanel, SLayout.EXPAND_Y);

		frame.pack();
		horizontalSplit.setDividerLocation(leftColumn.getPreferredSize().width);

		smallScreenHacks();
	}

	private void setupZoneChangeListeners(StendhalClient client) {
		client.addZoneChangeListener(screen);
		client.addZoneChangeListener(minimap);
		client.addZoneChangeListener(new WeatherSoundManager());
		// Disable side panel animation while changing zone
		client.addZoneChangeListener(new ZoneChangeListener() {
			@Override
			public void onZoneUpdate(Zone zone) {
			}

			@Override
			public void onZoneChangeCompleted(Zone zone) {
				containerPanel.setAnimated(true);
			}

			@Override
			public void onZoneChange(Zone zone) {
				containerPanel.setAnimated(false);
			}
		});
	}

	@Override
	public void updateUser(User user) {
		this.user = user;
		character.setPlayer(user);
		keyring.setSlot(user, "keyring");
		//portfolio.setSlot(user, "portfolio");
		spells.setSlot(user, "spells");
		inventory.setSlot(user, "bag");
	}

	@Override
	public JFrame getFrame() {
		return frame;
	}

	@Override
	public void resetClientDimensions() {
		int frameState = frame.getExtendedState();

		/*
		 *  Do not attempt to reset client dimensions if window is maximized.
		 *  Prevents resizing errors for child components.
		 */
		if (frameState != Frame.MAXIMIZED_BOTH) {
			frame.setSize(frameDefaultSize);
		}
	}

	@Override
	public Collection<PositionChangeListener> getPositionChangeListeners() {
		return Arrays.asList(screenController, minimap);
	}

	@Override
	public void setChatLine(String text) {
		chatText.setChatLine(text);
	}

	@Override
	public void afterPainting() {
		gameKeyHandler.processDelayedDirectionRelease();
	}

	@Override
	public void beforePainting() {
		screen.nextFrame();
	}

	@Override
	public void addDialog(Component dialog) {
		pane.add(dialog, JLayeredPane.PALETTE_LAYER);
	}

	@Override
	public boolean isOffline() {
		return offline;
	}

	@Override
	public void addAchievementBox(String title, String description,
			String category) {
		screen.addAchievementBox(title, description, category);
	}

	@Deprecated
	@Override
	public void addGameScreenText(double x, double y, String text,
			NotificationType type, boolean isTalking) {
		screenController.addText(x, y, text, type, isTalking);
	}

	@Deprecated
	@Override
	public void addGameScreenText(final Entity entity, final String text,
			final NotificationType type, final boolean isTalking) {
		screenController.addText(entity, text, type, isTalking);
	}

	@Override
	public void switchToSpellState(RPObject spell) {
		screen.switchToSpellCastingState(spell);
	}

	@Override
	public void chooseOutfit() {
		final RPObject player = userContext.getPlayer();

		if (!player.has("outfit_ext")) {
			final int code;

			if (player.has("outfit_org")) {
				code = player.getInt("outfit_org");
			} else {
				code = player.getInt("outfit");
			}

			final int body = code % 100;
			final int dress = code / 100 % 100;
			final int head = (int) (code / Math.pow(100, 2) % 100);
			final int hair = (int) (code / Math.pow(100, 3) % 100);
			final int detail = (int) (code / Math.pow(100, 4) % 100);

			final StringBuilder sb = new StringBuilder();
			sb.append("body=" + body);
			sb.append(",dress=" + dress);
			sb.append(",head=" + head);
			sb.append(",hair=" + hair);
			sb.append(",detail=" + detail);

			if (outfitDialog == null) {
				// Here we actually want to call new OutfitColor(). Modifying
				// OutfitColor.PLAIN would be a bad thing.
				outfitDialog = new OutfitDialog(frame, "Set outfit", sb.toString(), new OutfitColor(player));

				outfitDialog.setVisible(true);
			} else {
				// XXX: (AntumDeluge) why does this use "OutfitColor.get" but above uses "new OutfitColor"???
				outfitDialog.setState(sb.toString(), OutfitColor.get(player));

				outfitDialog.setVisible(true);
				outfitDialog.toFront();
			}
		} else {
			final String stroutfit;

			if (player.has("outfit_ext_orig")) {
				stroutfit = player.get("outfit_ext_orig");
			} else {
				stroutfit = player.get("outfit_ext");
			}

			if (outfitDialog == null) {
				// Here we actually want to call new OutfitColor(). Modifying
				// OutfitColor.PLAIN would be a bad thing.
				outfitDialog = new OutfitDialog(frame, "Set outfit", stroutfit, new OutfitColor(player));
				outfitDialog.setVisible(true);
			} else {
				outfitDialog.setState(stroutfit, OutfitColor.get(player));

				outfitDialog.setVisible(true);
				outfitDialog.toFront();
			}
		}
	}

	private final class HorizontalSplitListener extends ComponentAdapter {
		private final Dimension displaySize;
		private final JSplitPane split;
		// Start with a large value, so that the divider is placed as left
		// as possible
		private int oldWidth = Integer.MAX_VALUE;

		HorizontalSplitListener(Dimension displaySize, JSplitPane split) {
			this.displaySize = displaySize;
			this.split = split;
		}

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
						verticalSplit.setDividerLocation(preferredLocation);
					}
				});
			} else {
				int position = split.getDividerLocation();
				/*
				 * The trouble: the size of the game screen is likely the one
				 * that the player wants to preserve when making the window
				 * smaller. Swing provides no default way to the old component
				 * size, so we stash the interesting dimension in oldWidth.
				 */
				int width = split.getWidth();
				int oldRightDiff = oldWidth - position;
				int widthChange = width - oldWidth;
				int underflow = widthChange + position;
				if (underflow < 0) {
					/*
					 * Extreme size reduction. The divider location would have
					 * changed as the result. Use the previous location instead
					 * of the current.
					 */
					oldRightDiff = oldWidth - split.getLastDividerLocation();
				}
				position = MathHelper.clamp(width - oldRightDiff,
						split.getMinimumDividerLocation(),
						split.getMaximumDividerLocation());

				split.setDividerLocation(position);
				oldWidth = split.getWidth();
			}
		}
	}

	private class ScalingSettingChangeListener implements SettingChangeListener {
		private final int divWidth;

		ScalingSettingChangeListener(int divWidth) {
			this.divWidth = divWidth;
			changed(WtWindowManager.getInstance().getProperty(SCALE_PREFERENCE_PROPERTY, "true"));
		}

		@Override
		public final void changed(String newValue) {
			boolean scale = Boolean.parseBoolean(newValue);
			screen.setUseScaling(scale);
			if (scale) {
				// Clear the resize limits
				verticalSplit.setMaximumSize(null);
				pane.setMaximumSize(null);
			} else {
				Dimension displaySize = stendhal.getDisplaySize();

				// Set the limits
				verticalSplit.setMaximumSize(new Dimension(displaySize.width + divWidth, Integer.MAX_VALUE));
				pane.setMaximumSize(displaySize);
				// The user may have resized the screen outside allowed
				// parameters
				int overflow = horizontalSplit.getWidth() - horizontalSplit.getDividerLocation() - displaySize.width - divWidth;
				if (overflow > 0) {
					horizontalSplit.setDividerLocation(horizontalSplit.getDividerLocation() + overflow);
				}
				if (verticalSplit.getDividerLocation() > displaySize.height) {
					verticalSplit.setDividerLocation(displaySize.height);
				}
			}
		}
	}

	/**
	 * The layered pane where the game screen is does not automatically resize
	 * the game screen. This handler is needed to do that work.
	 */
	private static class GameScreenResizer extends ComponentAdapter {
		private final Component child;

		/**
		 * Create a SplitPaneResizeListener.
		 *
		 * @param child the component that needs to be resized
		 */
		GameScreenResizer(Component child) {
			this.child = child;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			// Pass on resize event
			child.setSize(e.getComponent().getSize());
		}
	}
}
