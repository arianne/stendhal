/*
 * @(#) src/games/stendhal/client/gui/j2d/Stendhal2D.java
 *
 * $Id$
 *
 */

package games.stendhal.client.gui.j2d;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.NotificationType;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.StendhalGUI;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * A Stendhal user interface using 2D graphics.
 *
 * This is a place that developers can do GUI refactoring (hopefully) without
 * breaking the existing client until it is complete enough to replace the
 * old one.
 */
public class Stendhal2D extends StendhalGUI {
	/**
	 * The default game screen width.
	 */
	protected static final int	DEFAULT_WIDTH	= 640;

	/**
	 * The default game screen height.
	 */
	protected static final int	DEFAULT_HEIGHT	= 480;

	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(StendhalGUI.class);

	/**
	 * The game screen height.
	 */
	protected int	height;

	/**
	 * The game screen width.
	 */
	protected int	width;


	public Stendhal2D(final StendhalClient client) {
		this(client, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}


	public Stendhal2D(StendhalClient client, final int width, final int height) {
		super(client);

		this.width = width;
		this.height = height;


	}


	//
	// Stendhal2D
	//

	public void run() {

	}


	//
	// StendhalUI
	//

	/**
	 * Add an event line.
	 * 
	 */
	public void addEventLine(final String text) {

	}

	/**
	 * Add an event line.
	 * 
	 */
	public void addEventLine(final String header, final String text) {

	}

	/**
	 * Add an event line.
	 * 
	 */
	public void addEventLine(final String text, final NotificationType type) {

	}

	/**
	 * Add an event line.
	 * 
	 */
	public void addEventLine(final String header, final String text, final NotificationType type) {

	}

	/**
	 * Initiate outfit selection by the user.
	 */
	public void chooseOutfit() {

	}

	/**
	 * Initiate guild management by the user.
	 */
	public void ManageGuilds() {

	}


	/**
	 * Get the current game screen height.
	 * 
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * Get the game screen.
	 * 
	 * @return The game screen.
	 */
	public GameScreen getScreen() {
return null;
	}

	/**
	 * Get the current game screen width.
	 * 
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Request quit confirmation from the user.
	 */
	public void requestQuit() {

	}


	/**
	 * Set the input chat line text.
	 * 
	 * @param text
	 *	The text.
	 */
	public void setChatLine(String text) {

	}

	/**
	 * Set the offline indication state.
	 * 
	 * @param offline
	 *	<code>true</code> if offline.
	 */
	public void setOffline(boolean offline) {

	}

	/**
	 * Set the user's positiion.
	 *
	 * @param	x		The user's X coordinate.
	 * @param	y		The user's Y coordinate.
	 */
	public void setPosition(double x, double y) {

	}


	/**
	 * Display command line usage.
	 */
	protected static void usage() {
		System.err.println("Stendhal 2D\n");
		System.err.println("java " + Stendhal2D.class.getName() + " [-u <username> -p <password> -h <hostname> -port <port>] [-s WxH]");
		System.err.println("  -h <hostname>       Host that is running Stendhal server");
		System.err.println("  -port <port>        Port of the Stendhal server (try 32160)");
		System.err.println("  -u <username>       Username to log into Stendhal server");
		System.err.println("  -p <password>       Password to log into Stendhal server");
		System.err.println("  -s <width>x<height> Screen size.");
	}

	//
	//

	public static void main(final String [] args) {
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
				String [] size = args[++i].split("x");

				if(size.length != 2) {
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

		if ((username != null) && (password != null) && (host != null) && (port != 0)) {
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
}
