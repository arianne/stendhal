package games.stendhal.bot.core;

import games.stendhal.client.update.Version;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.ClientFramework;
import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

/**
 * a standard implementation of the client framework
 * @author hendrik
 *
 */
public abstract class StandardClientFramework extends ClientFramework {

	private final String host;

	private final String username;

	private final String password;

	protected String character;

	private final String port;

	protected marauroa.client.ClientFramework clientManager;

	protected PerceptionHandler handler;

	protected Map<RPObject.ID, RPObject> worldObjects;

	/**
	 * Creates a ShouterMain.
	 * 
	 * @param h
	 *            host
	 * @param u
	 *            username
	 * @param p
	 *            password
	 * @param c
	 *            character name
	 * @param P
	 *            port
	 * @throws SocketException
	 *             on an network error
	 */
	public StandardClientFramework(final String h, final String u, final String p, final String c, final String P) throws SocketException {
		super("games/stendhal/log4j.properties");
		this.host = h;
		this.username = u;
		this.password = p;
		this.character = c;
		this.port = P;
		this.handler = new PerceptionHandler(new PerceptionErrorListener());
		this.worldObjects = new HashMap<RPObject.ID, RPObject>();
	}

	public void script() {
		try {
			clientManager.connect(host, Integer.parseInt(port));
			clientManager.login(username, password);
			execute();
			clientManager.logout();
			System.exit(0);

			// exit with an exit code of 1 on error
		} catch (final SocketException e) {
			System.err.println("Socket Exception");
			Runtime.getRuntime().halt(1);
		} catch (final TimeoutException e) {
			System.err.println("Cannot connect to Stendhal server. Server is down?");
			Runtime.getRuntime().halt(1);
		} catch (final Exception e) {
			System.out.println(e);
			e.printStackTrace(System.err);
			Runtime.getRuntime().halt(1);
		}
	}

	public abstract void execute() throws IOException, InterruptedException;

	@Override
	protected String getGameName() {
		return "stendhal";
	}

	@Override
	protected String getVersionNumber() {
		return Version.VERSION;
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
		try {
			handler.apply(message, worldObjects);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(
			final List<TransferContent> items) {
		for (final TransferContent item : items) {
			item.ack = true;
		}

		return items;
	}

	@Override
	protected void onServerInfo(final String[] info) {
		// do nothing
	}

	@Override
	protected void onAvailableCharacters(final String[] characters) {
		try {
			chooseCharacter(character);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onTransfer(final List<TransferContent> items) {
		// do nothing
	}

	@Override
	protected void onPreviousLogins(final List<String> previousLogins) {
		// do nothing
	}


	/**
	 * prints the parmaeter required to connect to the server.
	 */
	public static void printConnectionParameters() {
		System.out.println("* -h\tHost that is running Marauroa server");
		System.out.println("* -P\tPort on which Marauroa server is running");
		System.out.println("* -u\tUsername to log into Marauroa server");
		System.out.println("* -p\tPassword to log into Marauroa server");
		System.out.println("* -c\tCharacter used to log into Marauroa server");
	}}
