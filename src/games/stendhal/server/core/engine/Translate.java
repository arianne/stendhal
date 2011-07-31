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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import marauroa.common.io.UnicodeSupportingInputStreamReader;

import org.apache.log4j.Logger;

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
		init("de");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void init(String language) {
		try {
			UnicodeSupportingInputStreamReader reader = new UnicodeSupportingInputStreamReader(Translate.class.getClassLoader().getResourceAsStream("data/language/" + language + ".txt"));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			texts.put(language, new HashMap(prop));
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
	public static String _(String text) {
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
