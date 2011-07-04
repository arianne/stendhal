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
package games.stendhal.bot.core;

import games.stendhal.common.Version;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.ClientFramework;
import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.crypto.Hash;
import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPObject;
import marauroa.common.game.Result;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * a standard implementation of the client framework
 * @author hendrik
 *
 */
public abstract class StandardClientFramework extends ClientFramework {
	private final static Logger logger = Logger.getLogger(StandardClientFramework.class);

	private final String host;

	private final String username;

	private final String password;

	protected String character;

	private final String port;

	protected PerceptionHandler handler;

	protected Map<RPObject.ID, RPObject> worldObjects;

	protected final boolean createAccount;

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
	 * @param createAccount
	 *            createAccount
	 * @throws SocketException
	 *             on an network error
	 */
	public StandardClientFramework(final String h, final String u, final String p, final String c, final String P, final boolean createAccount) throws SocketException {
		super("games/stendhal/log4j.properties");
		this.host = h;
		this.username = u;
		this.password = p;
		this.character = c;
		this.port = P;
		this.handler = new PerceptionHandler(new PerceptionErrorListener());
		this.worldObjects = new HashMap<RPObject.ID, RPObject>();
		this.createAccount = createAccount;
	}

	public void script() {
		// initialize random number generator to prevent timeouts when hundreds of clients are started.
		Hash.random(4);

		try {
			this.connect(host, Integer.parseInt(port));
			if (createAccount) {
				AccountResult account = createAccount(username, password, "email@mailinator.com");
				if (account.getResult() == Result.OK_CREATED) {
					login(username, password);
					CharacterResult character = createCharacter(username, new RPObject());
					logger.info("Creating character: " + character.getResult());
				} else {
					if (account.getResult() == Result.FAILED_PLAYER_EXISTS) {
						login(username, password);
					} else {
						logger.error(account);
					}
				}
			} else {
				this.login(username, password);
			}
			execute();
			this.logout();
			System.exit(0);

			// exit with an exit code of 1 on error
		} catch (final SocketException e) {
			System.err.println("Socket Exception");
			e.printStackTrace(System.err);
			Runtime.getRuntime().halt(1);
		} catch (final TimeoutException e) {
			System.err.println("Cannot connect to Stendhal server. Server is down?");
			e.printStackTrace(System.err);
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
		return Version.getVersion();
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
