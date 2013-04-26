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
package games.stendhal.bot.textclient;

import games.stendhal.bot.core.StandardClientFramework;
import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.CStatusSender;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardHeaderedEventLine;
import games.stendhal.common.NotificationType;

import java.io.IOException;
import java.net.SocketException;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

/**
 * a text based ClientFramework
 *
 * @author hendrik
 */
public class TextClientFramework extends StandardClientFramework {

	private final boolean showWorld;

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
	 * @param showWorld
	 * @param createAccount
	 *            createAccount
	 * @throws SocketException
	 *             on an network error
	 */
	public TextClientFramework(String h, String u, String p, String c, String P, boolean showWorld, boolean createAccount)
			throws SocketException {
		super(h, u, p, c, P, createAccount);
		this.showWorld = showWorld;
		ClientSingletonRepository.setClientFramework(this);
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
		try {
			// System.out.println("Received perception " + message.getPerceptionTimestamp());

			handler.apply(message, worldObjects);

			handleChatEvents();

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

	/**
	 * handles public and private chat
	 */
	private void handleChatEvents() {
		try {
			for (final RPObject object : worldObjects.values()) {

				// ignore creatures and nameless things
				if (object.getRPClass().subclassOf("creature") || !object.has("name")) {
					continue;
				}
				String name = object.get("name");

				// for all events
				for (final RPEvent event : object.events()) {
					if (event.getName().equals("private_text")) {

						// private chat
						String text = event.get("text");
						NotificationType type;
						try {
							type = NotificationType.valueOf(event.get("texttype"));
						} catch (final RuntimeException e) {
							type = NotificationType.PRIVMSG;
						}
						ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(text, type));

					} else if (event.getName().equals("text")) {

						// public chat
						ClientSingletonRepository.getUserInterface().addEventLine(new StandardHeaderedEventLine(name, event.get("text")));
					}
				}

				// old style text attribute
				if (object.has("text")) {
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardHeaderedEventLine(name, object.get("text")));
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void execute() throws IOException, InterruptedException {
		CStatusSender.send();
		if (!createAccount) {
			new LoginScript(this).adminLogin();
		}

		while (true) {
			loop(0);
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				break;
			}
			if (!getConnectionState()) {
				System.out.println();
				System.out.println("Lost connection.");
				System.out.println();
				System.exit(1);
			}
		}
	}

}
