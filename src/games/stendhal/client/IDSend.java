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
package games.stendhal.client;

import games.stendhal.common.Debug;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Random;

import marauroa.common.crypto.Hash;
import marauroa.common.game.RPAction;
import marauroa.common.io.Persistence;

import org.apache.log4j.Logger;

/**
 * sends id
 */
public final class IDSend {

	/** the logger instance. */
	private static final Logger logger = Logger
			.getLogger(IDSend.class);

	/** filename for the settings persistence. */
	private static final String FILE_NAME = "cid";

	private static String clientid = null;

	/**
	 * sends id, version and distribution
	 */
	public static void send() {
		readID();
		if(!haveID()) {
			generateID();
			saveID();
		}

		if(!haveID()) {
			return;
		}

		final RPAction action = new RPAction();

		action.put("type", "cid");
		action.put("id", clientid);
		String version = Debug.VERSION;
		if (Debug.PRE_RELEASE_VERSION != null) {
			version = version + " - " + Debug.PRE_RELEASE_VERSION;
		}
		action.put("version", version);

		try {
			Class<?> clazz = Class.forName("games.stendhal.client.update.Starter");
			if (clazz != null) {
				Object[] objects = clazz.getSigners();
				if ((objects != null) && objects instanceof Certificate[]) {
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
		ClientSingletonRepository.getClientFramework().send(action);

	}

	private static void generateID() {
		clientid = generateRandomString();
	}

	private final static String CHARS =
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$/()@";
	/**
	* generates a random string
	*
	* @return random string
	*/
	private static String generateRandomString() {
		final StringBuffer res = new StringBuffer();
		final Random rnd = new SecureRandom();
		for (int i = 0; i < 32; i++) {
			int pos = (int) (rnd.nextFloat() * CHARS.length());
			res.append(CHARS.charAt(pos));
		}

		return res.toString();
	}

	private static boolean haveID() {
		if(clientid == null) {
			return false;
		}
		return true;
	}

	private static void readID() {
		try {
			final InputStream is = Persistence.get().getInputStream(false, stendhal.getGameFolder(), FILE_NAME);
		    final BufferedInputStream bis = new BufferedInputStream(is);
			try {
			    ByteArrayOutputStream buf = new ByteArrayOutputStream();
			    int result = bis.read();
			    while(result != -1) {
			      byte b = (byte)result;
			      buf.write(b);
			      result = bis.read();
			    }
			    clientid = buf.toString().trim();
			} finally {
			    bis.close();
				is.close();
			}
		} catch (final IOException e) {
			// ignore exception
		}
	}

	private static void saveID() {
		try {
			final OutputStream os = Persistence.get().getOutputStream(false,
					stendhal.getGameFolder(), FILE_NAME);
			final OutputStreamWriter writer = new OutputStreamWriter(os);
			try {
				writer.write(clientid);
			} finally {
				writer.close();
			}
		} catch (final IOException e) {
			// ignore exception
			logger.error("Can't write " + stendhal.getGameFolder() + FILE_NAME, e);
		}

	}
}
