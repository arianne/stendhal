package games.stendhal.bot.postman;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 * IRC Bot for postman
 * 
 * @author hendrik
 */
public class PostmanIRC extends PircBot {

	private static Logger logger = Logger.getLogger(PostmanIRC.class);

	private Properties prop = new Properties();

	private String gameServer;

	/**
	 * Creates a new PostmanIRC
	 * 
	 * @param gameServer
	 */
	public PostmanIRC(String gameServer) {
		this.gameServer = gameServer;
		try {
			this.prop.loadFromXML(new FileInputStream(
					System.getProperty("user.home")
							+ "/.stendhal-postman-conf.xml"));
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

			joinChannel("#arianne");
			joinChannel("#arianne-support");
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

	/**
	 * For testing only
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
