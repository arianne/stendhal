package games.stendhal.server.util;

import java.util.Properties;

/**
 * A simple translation framework
 *
 * @author hendrik
 */
public class Translate {
	private static Properties dictionary = new Properties();

	/**
	 * Loads the specified dictionary
	 *
	 * @param language the 2 letter language code
	 */
	public static void initLanguage(String language) {
		// TODO: implement this method
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
