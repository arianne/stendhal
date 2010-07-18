/* CC-BY Hendrik Brummermann <nhb_web@nexgo.de>
 * (But becomes GPL because it is required to link against GPL code).
 */
package games.stendhal.bot.postman;

import java.util.Date;
import java.util.StringTokenizer;

import marauroa.client.ClientFramework;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Postman.
 *
 * @author hendrik
 */
public class Postman implements Runnable {

	private static final String Y_COORD = "85";
	private static final String X_COORD = "112";
	private static final String POSTMAN_ZONE = "0_semos_plains_n";
	private static final String STENDHAL_POSTMAN_XML = ".stendhal-postman.xml";
	private static Logger logger = Logger.getLogger(Postman.class);
	private final ClientFramework clientManager;
	private final PostmanIRC postmanIRC;
	private static final String GREETING = "Hi, I am the postman. How can I #help you?";
	private static final String INTRO = "I store messages for offline players and deliver them on login.\n";
	private static final String HELP_MESSAGE = "Usage:\n/msg postman help \t This help-message\n/msg postman tell #player #message \t I will deliver your #message when #player logs in.";

	/**
	 * Creates a new postman.
	 *
	 * @param clientManager
	 *            ClientManager
	 * @param postmanIRC
	 *            postmanIRC
	 */
	public Postman(final ClientFramework clientManager, final PostmanIRC postmanIRC) {
		this.clientManager = clientManager;
		this.postmanIRC = postmanIRC;

	}

	/**
	 * Starts the /who thread.
	 */
	public void startThread() {
		final Thread t = new Thread(this, "Postman");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Processes a talk event.
	 * 
	 * @param object
	 *            the talking person
	 */
	public void processPublicTalkEvent(final RPObject object) {
		
		try {
			if (object == null) {
				return;
			}
			final int xdiff = object.getInt("x") - Integer.parseInt(X_COORD);
			final int ydiff = object.getInt("y") - Integer.parseInt(Y_COORD);
			if (xdiff * xdiff + ydiff * ydiff > 36) {
				logger.debug("***Postman*** object x: " + object.getInt("x") + ", object y: " + object.getInt("y")
							 + ", postman x " + Integer.parseInt(X_COORD) + ", postman y: " + Integer.parseInt(Y_COORD) 
							 + ", xdiff: " + xdiff + ", ydiff:  " + ydiff);
				return;
			}

			if (object.getRPClass().getName().equals("player")
					&& object.has("name")) {
				if (object.has("text")) {
					if (!object.get("name").equals("postman")) {
						final String text = object.get("text");
						String playerName = "";
						playerName = object.get("name");

						final java.text.Format formatter = new java.text.SimpleDateFormat(
								"[HH:mm] ");
						final String dateString = formatter.format(new Date());
						System.err.println(dateString + playerName + ": "
								+ text);

						final StringTokenizer st = new StringTokenizer(text, " ");
						String cmd = "";
						if (st.hasMoreTokens()) {
							cmd = st.nextToken();
						}
						if (cmd.equalsIgnoreCase("hi")) {
							chat(GREETING);
						} else if (cmd.equalsIgnoreCase("bye")) {
							chat("Bye.");
						} else if (cmd.equalsIgnoreCase("help")
								|| cmd.equalsIgnoreCase("info")
								|| cmd.equalsIgnoreCase("job")
								|| cmd.equalsIgnoreCase("offer")
								|| cmd.equalsIgnoreCase("letter")
								|| cmd.equalsIgnoreCase("parcel")) {
							chat(INTRO + HELP_MESSAGE);
						} else if (cmd.equalsIgnoreCase("msg")
								|| cmd.equalsIgnoreCase("tell")) {
							onTell(playerName, st);
						}
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e, e);
		}
	}

	/**
	 * Processes a talk event.
	 * 
	 * @param object
	 *            RPObject
	 * @param texttype
	 *            texttype
	 * @param text
	 *            text
	 */
	public void processPrivateTalkEvent(final RPObject object,
			final String texttype, final String text) {

		try {
			if (object == null) {
				return;
			}
			if (object.getRPClass().getName().equals("player")
					&& object.has("name")) {
				if (object.get("name").equals("postman")) {

					final java.text.Format formatter = new java.text.SimpleDateFormat(
							"[HH:mm] ");
					final String dateString = formatter.format(new Date());
					System.err.println(dateString + text);

					final StringTokenizer st = new StringTokenizer(text, " ");
					final String from = st.nextToken();
					
					// tells
					final String arianneCmd = st.nextToken();
					
					// you:
					st.nextToken(); 

					if (arianneCmd.equals("tells")) {
						// Command was send by a player
						// cmd
						String cmd = st.nextToken(); 
						if (cmd.startsWith("/")) {
							cmd = cmd.substring(1);
						}
						if (cmd.equalsIgnoreCase("tell")
								|| cmd.equalsIgnoreCase("msg")
								|| cmd.equalsIgnoreCase("/tell")
								|| cmd.equalsIgnoreCase("/msg")) {
							onTell(from, st);
						} else if (cmd.equalsIgnoreCase("hi")) {
							tell(from, GREETING);
						} else if (cmd.equalsIgnoreCase("help")
								|| cmd.equalsIgnoreCase("info")
								|| cmd.equalsIgnoreCase("job")
								|| cmd.equalsIgnoreCase("letter")
								|| cmd.equalsIgnoreCase("offer")
								|| cmd.equalsIgnoreCase("parcel")) {
							tell(from, INTRO + HELP_MESSAGE);
						} else if (cmd.equalsIgnoreCase("where")) {
							onWhere();
						} else {
							tell(from,
									"Sorry, I did not understand you. (Did you forget the \"tell\"?)\n"
											+ HELP_MESSAGE);
						}
					} else if (arianneCmd.equalsIgnoreCase("shouts:") || arianneCmd.equalsIgnoreCase("rented")) {
						postmanIRC.sendMessageToAllChannels(text);
					} else if (arianneCmd.equalsIgnoreCase("asks")
							|| arianneCmd.equalsIgnoreCase("answers")
							|| arianneCmd.equalsIgnoreCase("answer")
							   // for the new account messages
							|| "Support:".equals(from)
							   // for the npc zone shouts
							|| arianneCmd.equalsIgnoreCase("shouts")) {
						// answer is a typo in old server
						postmanIRC.sendSupportMessage(text);
						if (arianneCmd.equalsIgnoreCase("asks")) {
							dumpPlayerPosition();
						}
					}
				}

			}
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e, e);
		}
	}

	private void onTell(final String from, final StringTokenizer st) {
		String param = null;
		String msg = "";
		// System.err.println("!" + from + "! !" + cmd + "! !" + msg + "!");
		if (st.hasMoreTokens()) {
			
			// player
			param = st.nextToken(); 
		}
		if (st.hasMoreTokens()) {
			// the rest of the message
			msg = st.nextToken("\0").trim(); 
		}
		
		final RPAction action = new RPAction();
		action.put("type", "storemessageonbehalfofplayer");
		action.put("source", from);
		action.put("target", param);
		action.put("text", msg);
		send(action);

	}

	private void onWhere() {
		final RPAction who = new RPAction();
		who.put("type", "who");
		send(who);
	}

	private void tell(final String to, final String message) {
		if (to.equals("postman")) {
			logger.warn("I am not speaking to myself: " + message);
			return;
		}
		final RPAction tell = new RPAction();
		tell.put("type", "tell");
		tell.put("target", to);
		tell.put("text", message);
		send(tell);
	}

	private void chat(final String message) {
		final RPAction chat = new RPAction();
		chat.put("type", "chat");
		chat.put("text", message);
		send(chat);
	}

	@SuppressWarnings("unused")
	private void shout(final String message) {
		final RPAction chat = new RPAction();
		chat.put("type", "tellall");
		chat.put("text", message);
		send(chat);
	}

	private void teleportPostman() {
		final RPAction teleport = new RPAction();
		teleport.put("type", "teleport");
		teleport.put("target", "postman");
		teleport.put("zone", POSTMAN_ZONE);
		teleport.put("x", X_COORD);
		teleport.put("y", Y_COORD);
		send(teleport);
	}

	private void dumpPlayerPosition() {
		final RPAction chat = new RPAction();
		chat.put("type", "script");
		chat.put("target", "PlayerPositionMonitoring.class");
		send(chat);
	}

	public void run() {
		teleportPostman();
		try {
			Thread.sleep(400);
		} catch (final InterruptedException e) {
			logger.error(e, e);
		}
		while (true) {
			try {
				Thread.sleep(60 * 1000);
			} catch (final InterruptedException e) {
				logger.error(e, e);
			}
		}
	}

	private void send(final RPAction action) {
		clientManager.send(action);
	}
}
