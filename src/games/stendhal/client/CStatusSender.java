/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Random;

import org.apache.log4j.Logger;

import games.stendhal.common.Debug;
import marauroa.common.crypto.Hash;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.io.Persistence;

/**
 * sends client status information
 */
public final class CStatusSender {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(CStatusSender.class);

	/** filename for the settings persistence. */
	private static final String FILE_NAME = "cid";

	/**
	 * sends id, version and distribution
	 */
	public static void send() {
		String clientid = readID();
		if (clientid == null) {
			clientid = generateRandomString();
			saveID(clientid);
		}

		final RPAction action = new RPAction();

		// compatibility with old servers
		if (RPClass.getRPClass("cstatus") != null) {
			action.put("type", "cstatus");
		} else {
			action.put("type", "cid");
		}

		// a client id to help with the investigation of hacked accounts
		// especially in the common "angry sibling" case.
		if (clientid != null) {
			action.put("cid", clientid);
		}

		// the client version, the server will deactivate certain features that
		// are incompatible with old clients. E. g. changing of light and dark
		// in the current zone is implemented by retransmitting the tileset
		// information. an old client would mistake that for a zone change and
		// hang.
		String version = Debug.VERSION;
		if (Debug.PRE_RELEASE_VERSION != null) {
			version = version + " - " + Debug.PRE_RELEASE_VERSION;
		}
		action.put("version", version);

		// extract the signer of the client, so that we can ask bug
		// reporters to try again with the official client, if they
		// are using an unofficial one.
		try {
			Class<?> clazz = Class
					.forName("games.stendhal.client.update.Starter");
			if (clazz != null) {
				Object[] objects = clazz.getSigners();
				if (objects instanceof Certificate[]) {
					Certificate[] certs = (Certificate[]) objects;
					if ((certs.length > 0)) {
						byte[] key = certs[0].getPublicKey().getEncoded();
						action.put("dist", Hash.toHexString(Hash.hash(key)));
					}
				}
			}

			// Throwable: both errors and exceptions
		} catch (Throwable e) {
			logger.error(e, e);
		}

		// Get build number. The class is not in CVS to prevent lots of unwanted
		// conflicts. This has, however, the side effect, that it is missing in
		// an IDE (e. g. Eclipes) environment.
		// The build number is especially helpful for pre releases
		try {
			Class<?> clazz = Class.forName("games.stendhal.client.StendhalBuild");
			Object buildNumber = clazz.getMethod("getBuildNumber").invoke(null);
			if (buildNumber != null) {
				action.put("build", buildNumber.toString());
			}
		} catch (Throwable e) {
			logger.debug(e, e);
		}

		ClientSingletonRepository.getClientFramework().send(action);
	}

	private final static String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$/()@";

	/**
	 * generates a random string
	 *
	 * @return random string
	 */
	private static String generateRandomString() {
		final StringBuilder res = new StringBuilder();
		final Random rnd = new SecureRandom();
		for (int i = 0; i < 32; i++) {
			int pos = (int) (rnd.nextFloat() * CHARS.length());
			res.append(CHARS.charAt(pos));
		}

		return res.toString();
	}


	private static String readID() {
		String clientid = null;
		try {
			final InputStream is = Persistence.get().getInputStream(false, stendhal.getGameFolder(), FILE_NAME);
			final BufferedInputStream bis = new BufferedInputStream(is);
			try {
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				int result = bis.read();
				while (result != -1) {
					byte b = (byte) result;
					buf.write(b);
					result = bis.read();
				}
				clientid = buf.toString("UTF-8").trim();
			} finally {
				bis.close();
				is.close();
			}
		} catch (final IOException e) {
			// ignore exception
		}
		return clientid;
	}

	private static void saveID(String clientid) {
		try {
			final OutputStream os = Persistence.get().getOutputStream(false, stendhal.getGameFolder(), FILE_NAME);
			final OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
			try {
				writer.write(clientid);
			} finally {
				writer.close();
			}
		} catch (final IOException e) {
			logger.error("Can't write " + stendhal.getGameFolder() + FILE_NAME, e);
		}
	}
}
