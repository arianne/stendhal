package games.stendhal.bot.textclient;

import java.io.IOException;
import java.net.SocketException;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

import games.stendhal.bot.core.StandardClientFramework;
import games.stendhal.client.ClientSingletonRepository;

/**
 * a text based ClientFramework
 *
 * @author hendrik
 */
public class TextClientFramework extends StandardClientFramework {

	private boolean showWorld;

	/**
	 * Creates a new TextClientFramework.
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
	public TextClientFramework(String h, String u, String p, String c, String P, boolean showWorld)
			throws SocketException {
		super(h, u, p, c, P);
		this.showWorld = showWorld;
		ClientSingletonRepository.setClientFramework(this);
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
		try {
			System.out.println("Received perception "
					+ message.getPerceptionTimestamp());

			handler.apply(message, worldObjects);
			final int i = message.getPerceptionTimestamp();

			final RPAction action = new RPAction();
			/*if (i % 50 == 21) {
				action.put("type", "chat");
				action.put("text", "Test");
				this.send(action);
			}*/
			if (showWorld) {
				System.out.println("<World contents ------------------------------------->");
				int j = 0;
				for (final RPObject object : worldObjects.values()) {
					j++;
					System.out.println(j + ". " + object);
				}
				System.out.println("</World contents ------------------------------------->");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() throws IOException, InterruptedException {
		new LoginScript(this).adminLogin();

		while (true) {
			loop(0);
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				break;
			}
		}
	}

}
