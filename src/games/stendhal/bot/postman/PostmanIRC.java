package games.stendhal.bot.postman;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 * IRC Bot for postman.
 * 
 * @author hendrik
 */
public class PostmanIRC extends PircBot {

	private static final String STENDHAL_POSTMAN_CONF = ".stendhal-postman-conf.xml";

	private static Logger logger = Logger.getLogger(PostmanIRC.class);

	private Properties prop = new Properties();

	private String gameServer;

	private static String SUPPORT_CHANNEL;// = "#arianne-support";

	private static String MAIN_CHANNEL;// = "#arianne-chat";

	public static List<String> channels = new LinkedList<String>();// = {
																	// PostmanIRC.SUPPORT_CHANNEL,
																	// PostmanIRC.MAIN_CHANNEL
																	// };

	/**
	 * Creates a new PostmanIRC.
	 * 
	 * @param gameServer
	 */
	public PostmanIRC(String gameServer) {
		this.gameServer = gameServer;
		try {
			this.prop.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_CONF));
			SUPPORT_CHANNEL = prop.getProperty("support");
			MAIN_CHANNEL = prop.getProperty("main");

			channels.add(SUPPORT_CHANNEL);
			channels.add(MAIN_CHANNEL);
			channels.remove(null);
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * Postman IRC bot.
	 * 
	 * @throws IOException
	 * @throws IrcException
	 * @throws InterruptedException
	 */
	public void connect() throws IOException, IrcException,
			InterruptedException {
		if (Boolean.parseBoolean(prop.getProperty("irc"))) {
			String nick = prop.getProperty("name");
			String pass = prop.getProperty("pass");

			setName(nick);
			setLogin(prop.getProperty("login"));
			setVersion("0.2.1");
			setVerbose(true);
			setAutoNickChange(true);
			setFinger("postman on " + gameServer);
			connect("irc.freenode.net");

			if (!getNick().equals(nick)) {
				sendMessage("NickServ", "ghost " + nick + " " + pass);
				Thread.sleep(5000);
				super.changeNick(nick);
			}
			for (String channelName : PostmanIRC.channels) {
						joinChannel(channelName);
			}
			sendMessage("NickServ", "identify " + pass);
		}
	}

	@Override
	protected void onDisconnect() {
		super.onDisconnect();
		Thread t = new Thread("wait for reconnect") {

			@Override
			public void run() {
				try {
					Thread.sleep(60 * 1000);
					connect();
				} catch (Exception e) {
					logger.error(e, e);
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	void sendSupportMessage(final String text) {
		sendMessage(SUPPORT_CHANNEL, text);
	}

	void sendMessageToAllChannels(final String text) {
		for (String channelName : channels) {
			sendMessage(channelName, text);
		}
	}

	/**
	 * For testing only.
	 * 
	 * @param args
	 *            ignored
	 * @throws IOException
	 *             IOException
	 * @throws IrcException
	 *             IrcException
	 * @throws InterruptedException
	 *             InterruptedException
	 */
	public static void main(String[] args) throws IOException, IrcException,
			InterruptedException {
		// Now start our bot up.
		PostmanIRC bot = new PostmanIRC(null);
		bot.connect();
	}
}
