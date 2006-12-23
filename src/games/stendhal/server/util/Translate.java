package games.stendhal.server.util;

import games.stendhal.common.Grammar;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * A simple translation framework
 *
 * @author hendrik
 */
public class Translate {
	private static Logger logger = Logger.getLogger(Translate.class);
	private static Properties dictionary = new Properties();
	private static Grammar grammar = null;

	/**
	 * Loads the specified dictionary
	 *
	 * @param language the 2 letter language code
	 */
	public static void initLanguage(String language) {
		try {
			dictionary.load(Translate.class.getResourceAsStream("../properties/" + language + ".txt"));
			grammar = (Grammar) Class.forName(dictionary.getProperty("_grammar.class")).newInstance();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	/**
	 * Translates a text into the specified language
	 *
	 * @param text text to translate
	 * @param args arguments to integrate
	 * @return translated text
	 */
	public static String _(String text, String... args) {
		String translated = text;
		if (dictionary.get(text) != null) {
			translated = dictionary.getProperty(text);
		}
		// TODO: parse arguments
		return translated;
	}
}
