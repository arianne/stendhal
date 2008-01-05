/*
 * @(#) src/games/stendhal/client/gui/j2d/Stendhal2D.java
 *
 * $Id$
 *
 */

package games.stendhal.client.gui.j2d;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.KTextEdit;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.StendhalGUI;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJPanel;
import games.stendhal.common.NotificationType;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * A Stendhal user interface using 2D graphics.
 * 
 * This is a place that developers can do GUI refactoring (hopefully) without
 * breaking the existing client until it is complete enough to replace the old
 * one.
 * 
 * Currently some things are for demonstration and may be removed/changed.
 */
public class Stendhal2D extends StendhalGUI {
	/**
	 * The default game screen width.
	 */
	protected static final int DEFAULT_WIDTH = 640;

	/**
	 * The default game screen height.
	 */
	protected static final int DEFAULT_HEIGHT = 480;

	public static final int GAMELOG_HEIGHT = 120;
	public static final int SIDEBAR_WIDTH = 160;

	public static final int SIDEBAR_NONE = 0;
	public static final int SIDEBAR_LEFT = 1;
	public static final int SIDEBAR_RIGHT = 2;
	public static final int SIDEBAR_BOTH = 3;

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(StendhalGUI.class);

	/**
	 * The game screen height.
	 */
	protected int height;

	/**
	 * The game screen width.
	 */
	protected int width;

	/**
	 * The window frame.
	 */
	protected JFrame frame;

	/**
	 * The left sidebar.
	 */
	protected JPanel leftSB;

	/**
	 * The right sidebar.
	 */
	protected JPanel rightSB;

	protected JTextField chatText;

	protected KTextEdit gameLog;

	protected JLayeredPane pane;

	public Stendhal2D(final StendhalClient client) {
		this(client, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 */
	public Stendhal2D(StendhalClient client, final int width, final int height) {
		super(client);

		this.width = width;
		this.height = height;

		frame = new JFrame();

		JMenuBar mb = new JMenuBar();
		frame.setJMenuBar(mb);

		JMenu m;
		JMenuItem mi;
		JCheckBoxMenuItem cmi;
		JMenu smi;

		/*
		 * Game menu
		 */
		m = new JMenu("Game");
		mb.add(m);

		mi = new JMenuItem("Connect...", KeyEvent.VK_C);
		m.add(mi);

		mi = new JMenuItem("Create Account...", KeyEvent.VK_A);
		m.add(mi);

		mi = new JMenuItem("Reconnect", KeyEvent.VK_R);
		mi.setEnabled(false);
		m.add(mi);

		mi = new JMenuItem("Exit", KeyEvent.VK_X);
		mi.addActionListener(new ExitCB());
		m.add(mi);

		/*
		 * Edit menu
		 */
		m = new JMenu("Edit");
		mb.add(m);

		mi = new JMenuItem("Set Outfit...", KeyEvent.VK_O);
		mi.addActionListener(new ChooseOutfitCB());
		m.add(mi);

		mi = new JMenuItem("Profile Manager...", KeyEvent.VK_P);
		m.add(mi);

		/*
		 * Layout (eventually use icon for choices)
		 */
		smi = new JMenu("Layout");
		m.add(smi);

		ButtonGroup bgroup = new ButtonGroup();

		mi = new JRadioButtonMenuItem("SB - None");
		mi.addActionListener(new SideBarLayoutCB(SIDEBAR_NONE));
		smi.add(mi);
		bgroup.add(mi);

		mi = new JRadioButtonMenuItem("SB - Left");
		mi.addActionListener(new SideBarLayoutCB(SIDEBAR_LEFT));
		smi.add(mi);
		bgroup.add(mi);

		mi = new JRadioButtonMenuItem("SB - Right");
		mi.addActionListener(new SideBarLayoutCB(SIDEBAR_RIGHT));
		smi.add(mi);
		bgroup.add(mi);

		mi = new JRadioButtonMenuItem("SB - Both");
		mi.addActionListener(new SideBarLayoutCB(SIDEBAR_BOTH));
		smi.add(mi);
		bgroup.add(mi);

		cmi = new JCheckBoxMenuItem("Sound Enabled");
		cmi.setState(true);
		m.add(cmi);

		/*
		 * View menu
		 */
		m = new JMenu("View");
		mb.add(m);

		// These are hard-coded to illustration, but should be
		// dynamically added like settings panel did
		mi = new JCheckBoxMenuItem("Minimap");
		m.add(mi);

		mi = new JCheckBoxMenuItem("Character");
		m.add(mi);

		mi = new JCheckBoxMenuItem("Bag");
		m.add(mi);

		mi = new JCheckBoxMenuItem("Key Ring");
		m.add(mi);

		mi = new JCheckBoxMenuItem("Buddies");
		m.add(mi);

		/*
		 * Player menu
		 */
		m = new JMenu("Player");
		mb.add(m);

		mi = new JCheckBoxMenuItem("Ghost Mode");
		m.add(mi);

		/*
		 * Help menu
		 */
		mb.add(Box.createVerticalStrut(1));

		m = new JMenu("Help");
		mb.add(m);

		mi = new JMenuItem("About Stendhal...");
		m.add(mi);

		mi = new JMenuItem("Credits...");
		m.add(mi);

		Container root = frame.getContentPane();
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

		// pane = new JLayeredPane();
		// root.add(pane);

		JPanel gameArea = new JPanel(new BorderLayout());
		root.add(gameArea);

		WoodStyle style = new WoodStyle();

		/*
		 * Left side area
		 */
		leftSB = new StyledJPanel(style);
		leftSB.setPreferredSize(new Dimension(SIDEBAR_WIDTH, height));
		gameArea.add(leftSB, BorderLayout.WEST);

		/*
		 * Right side area
		 */
		rightSB = new StyledJPanel(style);
		rightSB.setPreferredSize(new Dimension(SIDEBAR_WIDTH, height));
		gameArea.add(rightSB, BorderLayout.EAST);

		/*
		 * Wrap canvas in panel that can has setPreferredSize()
		 */
		JPanel panel = new JPanel(null);
		Dimension size = new Dimension(width, height);
		panel.setMinimumSize(size);
		panel.setMaximumSize(size);
		panel.setPreferredSize(size);
		gameArea.add(panel, BorderLayout.CENTER);

		/*
		 * Chat input field
		 */
		chatText = new JTextField("");
		root.add(chatText);

		/*
		 * Chat/game log
		 */
		gameLog = new KTextEdit();
		gameLog.setPreferredSize(new Dimension(width, GAMELOG_HEIGHT));
		root.add(gameLog);

		// StendhalChatLineListener chatListener = new
		// StendhalChatLineListener(client, chatText);

		// chatText.addActionListener(chatListener);
		// chatText.addKeyListener(chatListener);

		frame.pack();

	}

	//
	// Stendhal2D
	//

	public void run() {
		frame.setVisible(true);
	}

	/**
	 * Set the sidebar layout.
	 * 
	 * 
	 */
	public void setSideBar(int mode) {
		switch (mode) {
		case SIDEBAR_NONE:
			leftSB.setVisible(false);
			rightSB.setVisible(false);
			break;

		case SIDEBAR_LEFT:
			leftSB.setVisible(true);
			rightSB.setVisible(false);
			break;

		case SIDEBAR_RIGHT:
			leftSB.setVisible(false);
			rightSB.setVisible(true);
			break;

		case SIDEBAR_BOTH:
			leftSB.setVisible(true);
			rightSB.setVisible(true);
			break;
		}

		frame.pack();
	}

	//
	// StendhalUI
	//

	/**
	 * Add an event line.
	 * 
	 */
	@Override
	public void addEventLine(final String text) {
		addEventLine("", text, NotificationType.NORMAL);
	}

	/**
	 * Add an event line.
	 * 
	 */
	@Override
	public void addEventLine(final String header, final String text) {
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
	 * Adds a ManagedWindow.
	 * 
	 * @param c
	 *            The component to add as an internal window.
	 */
	@Override
	public void addWindow(ManagedWindow c) {
		// do nothing in this implementation.
	}

	/**
	 * Initiate outfit selection by the user.
	 */
	@Override
	public void chooseOutfit() {

	}

	/**
	 * Initiate guild management by the user.
	 */
	@Override
	public void manageGuilds() {

	}

	/**
	 * Get the current game screen height.
	 * 
	 * @return The height.
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the game screen.
	 * 
	 * @return The game screen.
	 */
	@Override
	public IGameScreen getScreen() {
		return null;
	}

	/**
	 * Get the current game screen width.
	 * 
	 * @return The width.
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * Request quit confirmation from the user.
	 */
	@Override
	public void requestQuit() {

	}

	/**
	 * Set the input chat line text.
	 * 
	 * @param text
	 *            The text.
	 */
	@Override
	public void setChatLine(String text) {

	}

	/**
	 * Set the offline indication state.
	 * 
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	@Override
	public void setOffline(boolean offline) {

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

	}

	/**
	 * Display command line usage.
	 */
	protected static void usage() {
		System.err.println("Stendhal 2D\n");
		System.err.println("java "
				+ Stendhal2D.class.getName()
				+ " [-u <username> -p <password> -h <hostname> -port <port>] [-s WxH]");
		System.err.println("  -h <hostname>       Host that is running Stendhal server");
		System.err.println("  -port <port>        Port of the Stendhal server (try 32160)");
		System.err.println("  -u <username>       Username to log into Stendhal server");
		System.err.println("  -p <password>       Password to log into Stendhal server");
		System.err.println("  -s <width>x<height> Screen size.");
	}

	//
	//

	public static void main(final String[] args) {
		String username = null;
		String password = null;
		String host = null;
		int port = 0;
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equals("-u")) {
				username = args[++i];
			} else if (arg.equals("-p")) {
				password = args[++i];
			} else if (arg.equals("-h")) {
				host = args[++i];
			} else if (arg.equals("-port")) {
				port = Integer.parseInt(args[++i]);
			} else if (arg.equals("-s")) {
				String[] size = args[++i].split("x");

				if (size.length != 2) {
					System.err.println("Invalid size: " + arg);
					System.exit(1);
				}

				width = Integer.parseInt(size[0]);
				height = Integer.parseInt(size[1]);
			} else if (arg.equals("-help")) {
				usage();
				System.exit(0);
			} else {
				System.err.println("Unknown argument: " + arg);

				usage();
				System.exit(1);
			}
		}

		StendhalClient client = StendhalClient.get();

		if ((username != null) && (password != null) && (host != null)
				&& (port != 0)) {
			try {
				client.connect(host, port);
				client.login(username, password);
			} catch (Exception ex) {
				logger.error("Error connecting to server", ex);
				System.exit(2);
			}
		}

		Stendhal2D ui = new Stendhal2D(client, width, height);

		try {
			ui.run();
		} catch (Exception ex) {
			logger.error("Error running client", ex);
			System.exit(3);
		}
	}

	//
	//

	/**
	 * Callback to show Choose Outfit dialog.
	 */
	protected class ChooseOutfitCB implements ActionListener {
		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			chooseOutfit();
		}
	}

	/**
	 * Callback to exit the game.
	 */
	protected class ExitCB implements ActionListener {
		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			requestQuit();
		}
	}

	/**
	 * Callback to set a specific layout.
	 */
	protected class SideBarLayoutCB implements ActionListener {
		protected int mode;

		public SideBarLayoutCB(int mode) {
			this.mode = mode;
		}

		//
		// ActionListener
		//

		public void actionPerformed(ActionEvent ev) {
			setSideBar(mode);
		}
	}
}
