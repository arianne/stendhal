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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

/**
 * IRC Bot for postman.
 * 
 * @author hendrik
 */
public class PostmanIRC extends PircBot {

	public static List<String> channels = new LinkedList<String>();
	
	private static final String STENDHAL_POSTMAN_CONF = ".stendhal-postman-conf.xml";
	private static final String STENDHAL_POSTMAN_ANSWERS = ".stendhal-postman-answers.xml";
	
	private static final Logger LOGGER = Logger.getLogger(PostmanIRC.class);
	
	private static String supportChannel;

	private static String mainChannel;

	private final Properties prop = new Properties();

	private final String gameServer;

	/**
	 * Creates a new PostmanIRC.
	 * 
	 * @param gameServer
	 */
	public PostmanIRC(final String gameServer) {
		this.gameServer = gameServer;
		try {
			this.prop.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_CONF));
			supportChannel = prop.getProperty("support");
			mainChannel = prop.getProperty("main");

			channels.add(supportChannel);
			channels.add(mainChannel);
			channels.remove(null);
		} catch (final Exception e) {
			LOGGER.error(e, e);
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
			final String nick = prop.getProperty("name");
			final String pass = prop.getProperty("pass");

			setName(nick);
			setLogin(prop.getProperty("login"));
			setVersion("0.4");
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

	void sendSupportMessage(final String text) {
		sendMultilineMessage(supportChannel, text.replaceAll("Please use #/supportanswer .*", "").trim());
	}

	public final void sendMultilineMessage(String target, String message) {
		StringTokenizer st = new StringTokenizer(message, "\r\n");
		while (st.hasMoreTokens()) {
			sendMessage(target, st.nextToken());
		}
	}

	void sendMessageToAllChannels(final String text) {
		for (final String channelName : channels) {
			sendMultilineMessage(channelName, text);
		}
	}

	

	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		super.onMessage(channel, sender, login, hostname, message);
		if ((message != null) && message.startsWith("$")) {
			handleCanedResponse(channel, message);
		}
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
		try {
			this.prop.loadFromXML(new FileInputStream(STENDHAL_POSTMAN_ANSWERS));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// get the entry, do nothing if not defined
		String canedMessage = prop.getProperty(command);
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
	public static void main(final String[] args) throws IOException, IrcException,
			InterruptedException {
		// Now start our bot up.
		final PostmanIRC bot = new PostmanIRC(null);
		bot.connect();
	}
}
