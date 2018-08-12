/***************************************************************************
 *                (C) Copyright 2003-2011 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.io.UnicodeSupportingInputStreamReader;

/**
 * translation
 *
 * @author hendrik
 */
public class Translate {
	private static Logger logger = Logger.getLogger(Translate.class);

	private static Map<String, Map<String, String>> texts = new HashMap<String, Map<String, String>>();

	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();

	/**
	 * loads the language files
	 */
	public static void init() {
		init("es");
	}

	/**
	 * loads a lauguage file
	 *
	 * @param language language
	 */
	private static void init(String language) {
		try {
			InputStream is = Translate.class.getClassLoader().getResourceAsStream("data/languages/" + language + ".txt");
			if (is == null) {
				logger.warn("data/language/" + language + ".txt does not exist on classpath)");
				return;
			}
			BufferedReader reader = new BufferedReader(new UnicodeSupportingInputStreamReader(is, "UTF-8"));

			// we cannot use Properties.load because that does a) not support unicode and b) splits on the first space
			Map<String, String> map = new HashMap<String, String>();
			try {
				String line = reader.readLine();
				while (line != null) {
					String[] tokens = line.split("=", 2);
					if (tokens.length != 2) {
						System.err.println(line + "///" + Arrays.toString(tokens));
					} else {
						map.put(tokens[0], tokens[1]);
					}
					line = reader.readLine();
				}
			} finally {
				reader.close();
			}
			texts.put(language, map);
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	/**
	 * translate a text
	 *
	 * @param text text to translate
	 * @return translated text
	 */
	public static String getText(String text) {
		String language = threadLocal.get();
		String res = null;
		Map<String, String> map = texts.get(language);
		if (map != null) {
			res = map.get(text);
		}
		if (res == null) {
			res = text;
		}
		return res;
	}

	/**
	 * sets the language for this thread
	 *
	 * @param language language
	 */
	public static void setThreadLanguage(String language) {
		threadLocal.set(language);
	}
}
