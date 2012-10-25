/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.postman;

import games.stendhal.server.util.CounterMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.ReplyConstants;
import org.jibble.pircbot.User;

/**
 * IRC Bot for postman.
 *
 * @author hendrik
 */
public class PostmanIRC extends PircBot {


	private static final String STENDHAL_POSTMAN_CONF = ".stendhal-postman-conf.xml";
	private static final String STENDHAL_POSTMAN_ANSWERS = ".stendhal-postman-answers.xml";

	private static final Logger LOGGER = Logger.getLogger(PostmanIRC.class);

	/** myNick targetNick additionalData? :comment */
	// postman-bot-TEST hendrik hendrik :is logged in as
	// postman-bot-TEST hendrik :End of /WHOIS list.
	private static final Pattern patternWhoisResponse = Pattern.compile("^[^ ]* ([^ ]*) ([^ :]*) ?:.*");


	private static List<String> channels = new LinkedList<String>();
	private static List<String> signChannels = new LinkedList<String>();
	private static List<String> devChannels = new LinkedList<String>();
	private static String supportChannel;
	private static String mainChannel;
	private static String chatChannel;
	private static String devChannel;
	private final Properties conf = new Properties();
	private final String gameServer;
	private final FloodDetection floodDetection = new FloodDetection();
	private final CounterMap<String> kickedHostnames = new CounterMap<String>();

	/**
	 * Creates a new PostmanIRC.
	 *
	 * @param gameServer name of game server
	 */
	public PostmanIRC(final String gameServer) {
		this.gameServer = gameServer;
		try {
			this.conf.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_CONF));
			supportChannel = conf.getProperty("support");
			mainChannel = conf.getProperty("main");
			chatChannel = conf.getProperty("chat");
			devChannel = conf.getProperty("dev");

			channels.add(supportChannel);
			channels.add(mainChannel);
			channels.add(chatChannel);
			channels.remove(null);

			signChannels.add(supportChannel);
			signChannels.add(chatChannel);

			devChannels.add(mainChannel);
			devChannels.add(devChannel);
		} catch (final Exception e) {
			LOGGER.error(e, e);
		}
	}

	/**
	 * Postman IRC bot.
	 *
	 * @throws IOException  in case input/output error
	 * @throws IrcException in case of an irc issue
	 * @throws InterruptedException in case of a timeout
	 */
	public void connect() throws IOException, IrcException,
			InterruptedException {
		if (Boolean.parseBoolean(conf.getProperty("irc"))) {
			final String nick = conf.getProperty("name");
			final String pass = conf.getProperty("pass");

			setName(nick);
			setLogin(conf.getProperty("login"));
			setVersion("0.6");
			setVerbose(true);
			setAutoNickChange(true);
			setFinger("postman on " + gameServer);
			connect("irc.freenode.net");

			if (!getNick().equals(nick)) {
				sendMessage("NickServ", "ghost " + nick + " " + pass);
				Thread.sleep(5000);
				super.changeNick(nick);
			}
			sendMessage("NickServ", "identify " + pass);
			for (final String channelName : PostmanIRC.channels) {
				joinChannel(channelName);
			}
			joinChannel(devChannel);
		}
	}

	@Override
	protected void onDisconnect() {
		super.onDisconnect();
		final Thread t = new Thread("wait for reconnect") {

			@Override
			public void run() {
				try {
					Thread.sleep(60 * 1000);
					connect();
				} catch (final Exception e) {
					LOGGER.error(e, e);
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * sends a message to the support channel
	 *
	 * @param text text to send
	 */
	void sendSupportMessage(final String text) {
		sendMultilineMessage(supportChannel, text.replaceAll("Please use #/supportanswer .*", "").trim());
	}

	/**
	 * sends a multi line message to a target (channel or nickname)
	 *
	 * @param target  target
	 * @param message message
	 */
	public final void sendMultilineMessage(String target, String message) {
		StringTokenizer st = new StringTokenizer(message, "\r\n");
		while (st.hasMoreTokens()) {
			sendMessage(target, st.nextToken());
		}
	}

	/**
	 * sends a message to all channels
	 *
	 * @param text message to send
	 */
	void sendMessageToAllChannels(final String text) {
		for (final String channelName : channels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * sends a message to sign channels
	 *
	 * @param text message to send
	 */
	void sendMessageToSignChannels(String text) {
		for (final String channelName : signChannels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * sends a message to dev channels
	 *
	 * @param text message to send
	 */
	public void sendMessageToDevChannels(String text) {
		for (final String channelName : devChannels) {
			sendMultilineMessage(channelName, text);
		}
	}

	/**
	 * gets the game account name associated to an irc account
	 *
	 * @param ircAccountName irc account
	 * @return game account
	 */
	String getGameUsername(String ircAccountName) {
		return conf.getProperty("ircaccount-" + ircAccountName);
	}

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		super.onMessage(channel, sender, login, hostname, message);
		if ((message != null) && message.startsWith("$")) {
			handleCanedResponse(channel, message);
		}
		floodDetection.add(sender, message);
		handlePossibleFlood(channel, sender, hostname, message);
	}

	private void handlePossibleFlood(String channel, String sender, String hostname, String message) {
		User user = getUser(channel, sender);
		if ((user != null) && (user.isOp() || user.hasVoice())) {
			return;
		}
		if (sender.equals("postman")) {
			return;
		}

		if (floodDetection.isFlooding(sender, message)) {
			floodDetection.clear(sender);

			int cnt = kickedHostnames.getCount(hostname);
			kickedHostnames.add(hostname);

			if (cnt < 2) {
				EventRaiser.get().addEventHandler(EventType.IRC_OP, channel, new IrcFloodKick(sender, this, conf.getProperty("floodkick-message", "Please use http://pastebin.com/ to paste large amounts of text.")));
				sendMessage("ChanServ", "op " + channel);
			} else {
				for (final String channelName : channels) {
					EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcFloodKick(sender, this, conf.getProperty("floodkick-message", "Please use http://pastebin.com/ to paste large amounts of text.")));
					EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcFloodBan(hostname, this));
					sendMessage("ChanServ", "op " + channelName);
				}
			}
		}
	}

	/**
	 * gets an user object
	 *
	 * @param channel channel
	 * @param nick nick
	 * @return User or null
	 */
	private User getUser(String channel, String nick) {
		User[] users = super.getUsers(channel);
		if (users == null) {
			return null;
		}
		for (User user : users) {
			if (user.getNick().equals(nick)) {
				return user;
			}
		}
		return null;
	}

	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
		super.onPrivateMessage(sender, login, hostname, message);

		EventHandler handler = CommandFactory.create(message, sender, this);
		if (handler != null) {
			EventRaiser.get().addEventHandler(EventType.IRC_WHOIS, sender, handler);
			sendRawLineViaQueue("WHOIS "+ sender);
		} else if (message.equals("help")) {
			sendHelpReply(sender);
		} else if (message.startsWith("$")) {
			handleCanedResponse(sender, message);
		}
	}

	private void sendHelpReply(String sender) {
		sendMessage(sender, "Hello, I am postman.");
		sendMessage(sender, "In Stendhal I deliver messages to players and on IRC I help admins.");
		sendMessage(sender, " ");
		listCanedResponses(sender);
		sendMessage(sender, " ");
		sendMessage(sender, "Admin commands:");
		sendMessage(sender, " /msg postman ban <target> <hours> <message>");
		sendMessage(sender, " /msg postman adminnote <target> <message>");
		sendMessage(sender, " /msg postman ircban <ip>");
		sendMessage(sender, " /msg postman npcshout <name> <message>");
		sendMessage(sender, " /msg postman support <message>");
		sendMessage(sender, " /msg postman supporta <message>");
		sendMessage(sender, " /msg postman tellall <message>");
	}

	@Override
	protected void onServerResponse(int code, String response) {
		super.onServerResponse(code, response);

		// 330 is confirmed account someone is logged in to
		if ((code == 330) || (code == ReplyConstants.RPL_ENDOFWHOIS)) {
			Matcher matcher = PostmanIRC.patternWhoisResponse.matcher(response);
			if (matcher.find()) {
				// group(1): nick, group(2): account or ""
				EventRaiser.get().fire(EventType.IRC_WHOIS, matcher.group(1), matcher.group(2));
			}
		}
	}


	private void listCanedResponses(String target) {
		// load answer file (reload it every time so that it can be edited)
		Properties answers = new Properties();
		try {
			answers.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_ANSWERS));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (Object key : answers.keySet()) {
			sb.append(" " + key);
		}

		sendMessage(target, "I know the following caned messages:" + sb.toString());
	}

	private long lastCommand;

	private void handleCanedResponse(String channel, String message) {
		// prevent flooding
		if (lastCommand + 5000 > System.currentTimeMillis()) {
			return;
		}

		// extract commands
		StringTokenizer st = new StringTokenizer(message);
		String command = st.nextToken();
		String user = "";
		if (st.hasMoreElements()) {
			user = " " + st.nextToken();
		}

		// load answer file (reload it every time so that it can be edited)
		Properties answers = new Properties();
		try {
			answers.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_ANSWERS));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// get the entry, do nothing if not defined
		String canedMessage = answers.getProperty(command);
		if (canedMessage == null) {
			return;
		}

		// send it as message or action to the channel
		canedMessage = String.format(canedMessage, user);
		if (canedMessage.startsWith("/me")) {
			sendAction(channel, canedMessage.replaceAll("^/me ", ""));
		} else {
			sendMessage(channel, canedMessage);
		}

		lastCommand = System.currentTimeMillis();
	}

	/**
	 * bans an ip-address in all channels
	 *
	 * @param address address to ban
	 */
	public void banIp(String address) {
		for (final String channelName : channels) {
			EventRaiser.get().addEventHandler(EventType.IRC_OP, channelName, new IrcBan(address, this));
			sendMessage("ChanServ", "op " + channelName);
		}
	}

	@Override
	protected void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient) {
		super.onOp(channel, sourceNick, sourceLogin, sourceHostname, recipient);
		EventRaiser.get().fire(EventType.IRC_OP, channel, null);
		sendMessage("ChanServ", "deop " + channel);
	}

	/**
	 * forgets about the hosts that had been kiced for flooding
	 */
	public void flashThing() {
		kickedHostnames.clear();
		super.sendAction(mainChannel, "looks into the red flash");
	}

}
