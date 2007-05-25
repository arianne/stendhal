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
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.stendhal;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJButton;
import games.stendhal.client.gui.wt.GroundContainer;
import games.stendhal.client.gui.wt.InternalManagedDialog;
import games.stendhal.client.gui.wt.SettingsPanel;
import games.stendhal.client.gui.wt.core.WtBaseframe;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.common.CollisionDetection;
import games.stendhal.common.Direction;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;

/** The main class that create the screen and starts the arianne client. */
public class j2DClient extends StendhalUI {

	private static final long serialVersionUID = 3356310866399084117L;

	/** width of the game screen (without the chat line) */
	public static int SCREEN_WIDTH;

	/** height of the game screen (without the chat line) */
	public static int SCREEN_HEIGHT;

	static {
		String[] dim = stendhal.SCREEN_SIZE.split("x");
		SCREEN_WIDTH = Integer.parseInt(dim[0]);
		SCREEN_HEIGHT = Integer.parseInt(dim[1]);
	}

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(j2DClient.class);

	/**
	 * The man window frame.
	 */
	private JFrame	frame;

	private GameScreen screen;

	private Canvas canvas;

	private JLayeredPane	pane;

	private KTextEdit gameLog;

	private boolean gameRunning;

	/** NOTE: It sounds bad to see here a GUI component. Try other way. */
	private JTextField playerChatText;

// Not currently used (maybe later?)
//	private FXLayer fx;

	private boolean ctrlDown;

	private boolean shiftDown;

	private boolean altDown;

	/** the main frame */
	private WtBaseframe baseframe;

	/** this is the ground */
	private GroundContainer ground;

	/** settings panel */
	private SettingsPanel settings;

	private Component	quitDialog;

	/**
	 * Not currently used - but will likely be later
	 */
	private static final boolean newCode =
			(System.getProperty("stendhal.newgui") != null);

	public j2DClient(StendhalClient client) {
		super(client);
		
		setDefault(this);

		frame = new JFrame();

		frame.setTitle(ClientGameConfiguration.get("GAME_NAME") + " " + stendhal.VERSION
		        + " - a multiplayer online game using Arianne");


		// create a frame to contain our game

		URL url = SpriteStore.get().getResourceURL(ClientGameConfiguration.get("GAME_ICON"));
		frame.setIconImage(new ImageIcon(url).getImage());


		// When the user tries to close the window, don't close immediately,
		// but show a confirmation dialog. 
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);


		Container content = frame.getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));


		/*
		 * Get hold the content of the frame and set up the resolution
		 * of the game
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
		    canvas.setBounds(200, 0, 600, SCREEN_HEIGHT);// A bit repetitive... oh well
		} else {
		    canvas = new Canvas();
		    canvas.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		}
		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		canvas.setIgnoreRepaint(true);
		panel.add(canvas);


		/*
		 * Chat input field
		 */
		playerChatText = new JTextField("");

		StendhalChatLineListener chatListener = new StendhalChatLineListener(client, playerChatText);
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
		frame.addWindowListener(new WindowAdapter() {
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
		 */
		quitDialog = buildQuitDialog();
		quitDialog.setVisible(false);

		pane.add(quitDialog, JLayeredPane.MODAL_LAYER);


		/*
		 * Game log
		 */
		gameLog = new KTextEdit();
		gameLog.setPreferredSize(new Dimension(SCREEN_WIDTH, 171));


		if (System.getProperty("stendhal.onewindow") != null) {
			content.add(gameLog);
			frame.pack();
		} else if (System.getProperty("stendhal.onewindowtitle") != null || System.getProperty("stendhal.refactoringguiui") != null) {
		    	JLabel header = new JLabel();	
			header.setText("Game Chat and Events Log");
			header.setFont(new java.awt.Font("Dialog", 3, 14));
			content.add(header);
			content.add(gameLog);
			frame.pack();
		} else {
			/*
			 * In own window
			 */
			final JDialog dialog = new JDialog(frame, "Game chat and events log");

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
			frame.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					Rectangle bounds = frame.getBounds();

					dialog.setLocation(
				        	bounds.x,
						bounds.y + bounds.height);

					dialog.setVisible(true);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					Rectangle bounds = frame.getBounds();

					dialog.setLocation(
				        	bounds.x,
						bounds.y + bounds.height);
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
		// I hope that it discourages its use without the risks of unabdateable
		// clients being distributed
		if (!stendhal.SCREEN_SIZE.equals("640x480")) {
			addEventLine("Using window size cheat: " + stendhal.SCREEN_SIZE, Color.RED);
		}



		frame.setLocation(new Point(20, 20));

		// finally make the window visible
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);


		/*
		 * create the buffering strategy which will allow AWT
		 * to manage our accelerated graphics
		 */
		BufferStrategy strategy;
		canvas.createBufferStrategy(2);
		strategy = canvas.getBufferStrategy();

		screen = new GameScreen(client, strategy, SCREEN_WIDTH, SCREEN_HEIGHT);
		screen.setComponent(canvas);

		GameScreen.setDefaultScreen(screen);
		client.setScreen(screen);

// Not currently used (maybe later?)
//		fx = new FXLayer(SCREEN_WIDTH, SCREEN_HEIGHT);


		// create the baseframe
		baseframe = new WtBaseframe(screen);
		// register native event handler
		canvas.addMouseListener(baseframe);
		canvas.addMouseMotionListener(baseframe);
		// create ground
		ground = new GroundContainer(this);

		baseframe.addChild(ground);
		// the settings panel creates all other
		settings = new SettingsPanel(this, ground);
		ground.addChild(settings);

		// set some default window positions
		WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);


		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		gameLoop();

		chatListener.save();
		logger.debug("Exit");
		System.exit(0);
	} // constructor


	/**
	 * Add a native in-window dialog to the screen.
	 *
	 * @param	comp		The component to add.
	 */
	public void addDialog(Component comp) {
		pane.add(comp, JLayeredPane.PALETTE_LAYER);
	}


	/**
	 * Build the in-window quit dialog [panel].
	 *
	 *
	 */
	protected Component buildQuitDialog() {
		InternalManagedDialog	imd;
		Style	style;
		JPanel	panel;
		JButton	b;


		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(150, 75));

		style = WoodStyle.getInstance();

		b = new StyledJButton(style);
		b.setText("Yes");
		b.setBounds(30, 25, 40, 25);
		b.addActionListener(new QuitConfirmCB());

		panel.add(b);


		b = new StyledJButton(style);
		b.setText("No");
		b.setBounds(80, 25, 40, 25);
		b.addActionListener(new QuitCancelCB());

		panel.add(b);


		imd = new InternalManagedDialog("quit", "Quit");
		imd.setContent(panel);
		imd.setMinimizable(false);
		imd.setMovable(false);

		return imd.getDialog();
	}


	public void gameLoop() {
		final int frameLength = (int) (1000.0 / stendhal.FPS_LIMIT);
		int fps = 0;
		GameObjects gameObjects = client.getGameObjects();
		long oldTime = System.nanoTime();

		// Clear the first screen
		screen.clear();
//		screen.place(-100, -100);
		SoundMaster.play("harp-1.wav");

		// keep looping until the game ends
		long refreshTime = System.currentTimeMillis();
		long lastMessageHandle = refreshTime;

		gameRunning = true;
		boolean canExit = false;

		while (!canExit) {
			fps++;
			// figure out what time it is right after the screen flip then
			// later we can figure out how long we have been doing redrawing
			// / networking, then we know how long we need to sleep to make
			// the next flip happen at the right time

			screen.nextFrame();
			long now = System.currentTimeMillis();
			long delta = now - refreshTime;
			refreshTime = now;

			logger.debug("Move objects");
			gameObjects.move(delta);


			StaticGameLayers gameLayers = client.getStaticGameLayers();

			// create the map if there is none yet
			if (gameLayers.changedArea()) {
				CollisionDetection cd = gameLayers.getCollisionDetection();
				if (cd != null) {
					gameLayers.resetChangedArea();
					settings.updateMinimap(cd, screen.expose().getDeviceConfiguration(), gameLayers.getArea());
				}
			}

			settings.setPlayer(User.get());

			if (frame.getState() != Frame.ICONIFIED) {
				logger.debug("Draw screen");
				screen.draw(baseframe);
			}

			// TODO: only draw it if it is required to save cpu time
			// fx.draw(screen.expose());

			logger.debug("Query network");
			if (client.loop(0)) {
				lastMessageHandle = System.currentTimeMillis();
			}

			if (System.nanoTime() - oldTime > 1000000000) {
				oldTime = System.nanoTime();
				logger.debug("FPS: " + Integer.toString(fps));
				long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
				long totalMemory = Runtime.getRuntime().totalMemory() / 1024;

				logger.debug("Total/Used memory: " + totalMemory + "/" + (totalMemory - freeMemory));

				fps = 0;
			}

			// Shows a offline icon if no messages are recieved in 10 seconds.
			if ((refreshTime - lastMessageHandle > 120000) || !client.getConnectionState()) {
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
				if (wait > 100) {
					logger.info("Waiting " + wait + " ms");
					wait = 100;
				}

				try {
					Thread.sleep(wait);
				} catch (Exception e) {
				}
				;
			}

			logger.debug("End sleeping");
			
			if(!gameRunning) {
				logger.info("Request logout");
				try {
					/*
					 * We request server permision to logout.
					 * Server can deny it. 
					 */
	                if(client.logout()) {
	                	canExit=true;
	                } else {
	                	gameRunning=true;
	                }
                } catch (Exception e) {
	                e.printStackTrace();
                }
			}
		}

		SoundSystem.get().exit();
	}


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
				if(e.isControlDown()) {
					/*
					 * Ctrl+L
					 * Make game log visible
					 */
					SwingUtilities.getRoot(gameLog).setVisible(true);
				}

				break;

			case KeyEvent.VK_R:
				if(e.isControlDown()) {
					/*
					 * Ctrl+R
					 * Remove text bubbles
					 */
					client.clearTextBubbles();
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
//				int dy;
//				if (direction.getdy()==0)
//					dy=1;
//				else if (direction.getdy()==-1)
//					dy=0;
//				else
//					dy=2;

				Entity en =GameObjects.getInstance().at(User.get().getX()+direction.getdx(),User.get().getY()+direction.getdy()+1
				);
				if (en != null&& !en.equals(User.get())){
					if (e.isAltGraphDown()){
						en.onAction(en.defaultAction());	
					}
				}
				client.addDirection(
						direction,
						e.isControlDown());

				break;
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
				client.removeDirection(
					keyCodeToDirection(e.getKeyCode()),
					e.isControlDown());
		}
	}


	protected void quitCancelCB() {
		quitDialog.setVisible(false);
	}


	protected void quitConfirmCB() {
		shutdown();
	}

	/**
	 * Save the current keyboard modifier (i.e. Alt/Ctrl/Shift) state.
	 *
	 * @param	ev		The keyboard event.
	 */
	protected void updateModifiers(KeyEvent ev) {
		altDown = ev.isAltDown();
		ctrlDown = ev.isControlDown();
		shiftDown = ev.isShiftDown();
	}


	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
	protected void shutdown() {
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
	 * @param	mw		A managed window.
	 *
	 * @throws	IllegalArgumentException
	 *				If an unsupported ManagedWindow is given.
	 */
	@Override
	public void addWindow(ManagedWindow mw) {
		if(mw instanceof InternalManagedDialog) {
			addDialog(((InternalManagedDialog) mw).getDialog());
		} else if(mw instanceof WtPanel) {
			ground.addChild((WtPanel) mw);
		} else {
			throw new IllegalArgumentException(
				"Unsupport ManagedWindow type: "
				+ mw.getClass().getName());
		}
	}


	/**
	 * Determine if the Alt key is held down.
	 *
	 * @return	Returns <code>true</code> if down.
	 */
	@Override
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * Determine if the <Ctrl> key is held down.
	 *
	 * @return	Returns <code>true</code> if down.
	 */
	@Override
	public boolean isCtrlDown() {
		return ctrlDown;
	}

	/**
	 * Determine if the <Shift> key is held down.
	 *
	 * @return	Returns <code>true</code> if down.
	 */
	@Override
	public boolean isShiftDown() {
		return shiftDown;
	}

	/**
	 * Sets the context menu. It is closed automatically one the user
	 * clicks outside of it.
	 */
	@Override
	public void setContextMenu(JPopupMenu contextMenu) {
		baseframe.setContextMenu(contextMenu);
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
		gameLog.addLine(text);
	}


	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(String header, String text) {
		gameLog.addLine(header, text);
	}


	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(String text, Color color) {
		gameLog.addLine(text, color);
	}


	/**
	 * Add an event line.
	 *
	 */
	@Override
	public void addEventLine(String header, String text, Color color) {
		gameLog.addLine(header, text, color);
	}


	/**
	 * Initiate outfit selection by the user.
	 */
	@Override
	public void chooseOutfit() {
		int	outfit;


		RPObject player = client.getPlayer();

		if(player.has("outfit_org")) {
			outfit = player.getInt("outfit_org");
		} else {
			outfit = player.getInt("outfit");
		}

		// Should really keep only one instance of this around
		OutfitDialog dialog = new OutfitDialog(frame, "Set outfit", outfit);
		dialog.setVisible(true);
	}
	@Override
	public void ManageGuilds() {
	    GuildManager gm = new GuildManager();
	    gm.setVisible(true);
	}


	/**
	 * Get the current game screen height.
	 *
	 * @return	The height.
	 */
	@Override
	public int getHeight()
	{
		return SCREEN_HEIGHT;
	}


	/**
	 * Get the entity inspector.
	 *
	 * @return	The inspector.
	 */
	@Override
	public Inspector getInspector() {
		return ground;
	}


	/**
	 * Get the game screen.
	 *
	 * @return	The game screen.
	 */
	@Override
	public GameScreen getScreen() {
		return screen;
	}


	/**
	 * Get the current game screen width.
	 *
	 * @return	The width.
	 */
	@Override
	public int getWidth() {
		return SCREEN_WIDTH;
	}


	/**
	 * Request quit confirmation from the user.
	 * This stops all player actions and shows a dialog in which the
	 * player can confirm that they really wants to quit the program.
	 * If so it flags the client for termination.
	 */
	@Override
	public void requestQuit() {
		/*
		 * Stop the player
		 */
		client.stop();

		/*
		 * Center dialog
		 */
		Dimension psize = quitDialog.getPreferredSize();

		quitDialog.setBounds(
			(getWidth() - psize.width) / 2,
			(getHeight() - psize.height) / 2,
			psize.width,
			psize.height);

		quitDialog.validate();
		quitDialog.setVisible(true);
	}


	/**
	 * Set the input chat line text.
	 *
	 * @param	text		The text.
	 */
	@Override
	public void setChatLine(String text) {
		playerChatText.setText(text);
	}


	/**
	 * Set the offline indication state.
	 *
	 * @param	offline		<code>true</code> if offline.
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

	//
	//

	protected class QuitCancelCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			quitCancelCB();
		}
	}


	protected class QuitConfirmCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			quitConfirmCB();
		}
	}


	//
	//

	public static void main(String args[]) {
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

			if ((username != null) && (password != null) && (host != null) && (port != null)) {
				StendhalClient client = StendhalClient.get();
				try {
					client.connect(host, Integer.parseInt(port));
					client.login(username, password);

					new j2DClient(client);
				} catch (Exception ex) {
					ex.printStackTrace();
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
}
